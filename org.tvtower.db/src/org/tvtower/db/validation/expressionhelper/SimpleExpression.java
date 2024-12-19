package org.tvtower.db.validation.expressionhelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableList;

public class SimpleExpression {

	private static final Pattern SIMPLE_EXPRESSION_PATTERN = Pattern.compile("\\$\\{((\\.\\w+)(:[^$:]+?)*)\\}");
	private static final Pattern SIMPLE_EXPRESSION_PARAM_PATTERN = Pattern.compile("(:[^$:]+)");
	private static final Pattern FUNCTION_PATTERN = Pattern.compile("\\$\\{(\\.\\w+):");
	private static final Pattern WT_REPLACE_PATTERN = Pattern.compile("&wt_(\\\"\\w+\\\")");
	private static final Pattern MISSING_DOT_PATTERN = Pattern.compile("\\$\\{(\\w+):");

	private static final Pattern COLON_IN_STRING_PATTERN=Pattern.compile(":\"[^\":${]*(:)[^\":${]*\"");

	private static final List<String> KNOWN_COMPLEX_FUNCTIONS = ImmutableList.of(//
			".and", ".eq", ".gt", ".gte", ".lt", ".lte", ".or", ".if", ".person", ".csv");

	public static List<SimpleExpression> get(String contentRaw) {
		Set<Integer> startIndexes = new HashSet<>();
		List<SimpleExpression> result = new ArrayList<>();
		String content = contentRaw;
		//replace colon from simple strings in order to find the correct number of function arguments
		if (contentRaw.indexOf('$') >= 0) {
			Matcher matcher = COLON_IN_STRING_PATTERN.matcher(content);

			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(sb, ":" + matcher.group().replace(':', ' '));
			}
			matcher.appendTail(sb);
			content = sb.toString();
		} else {
			return result;
		}

		Matcher simpleExpressionMatcher = SIMPLE_EXPRESSION_PATTERN.matcher(content);
		while (simpleExpressionMatcher.find()) {
			String fct = simpleExpressionMatcher.group(2).toLowerCase();
			String all = simpleExpressionMatcher.group(1);
			Matcher paramMatcher = SIMPLE_EXPRESSION_PARAM_PATTERN.matcher(all);
			List<String> params = new ArrayList<>();
			while (paramMatcher.find()) {
				params.add(paramMatcher.group(1).substring(1));
			}
			SimpleExpression exp = new SimpleExpression();
			exp.function = fct;
			exp.all = all;
			exp.params = params;
			result.add(exp);
			startIndexes.add(simpleExpressionMatcher.start());
		}

		// at least check if the funciton is known
		Matcher allFunctionsMatcher = FUNCTION_PATTERN.matcher(content);
		while (allFunctionsMatcher.find()) {
			String fct = allFunctionsMatcher.group(1).toLowerCase();
			if (!startIndexes.contains(allFunctionsMatcher.start())) {
				SimpleExpression exp = new SimpleExpression();
				exp.function = fct;
				result.add(exp);
			}
		}

		// check for function syntax errors
		Matcher functionNameErrorMatcher = MISSING_DOT_PATTERN.matcher(content);
		while (functionNameErrorMatcher.find()) {
			String fct = functionNameErrorMatcher.group(1).toLowerCase();
			SimpleExpression exp = new SimpleExpression();
			exp.function = fct;
			result.add(exp);
		}

		return result;
	}

	String all;
	private String function;
	private List<String> params;

	private void replaceWorldTimeSimplifyier() {
		Matcher m = WT_REPLACE_PATTERN.matcher(all);
		if (m.find()) {
			all = all.replace(m.group(0), "${.worldtime:" + m.group(1) + "}");
		}
	}

	public String getFunction() {
		return function;
	}

	public List<String> getParams() {
		return params;
	}

	public String getValidationError(EObject context) {
		String error = null;
		String errorInfix = " in simple expression ";
		if (getFunction() != null && getFunction().charAt(0) != '.') {
			error = "missing function dot - ${" + getFunction() + ":...";
		} else if (params != null) {
			switch (getFunction()) {
			case ".worldtime":
				error = WorldTimeExpression.validate(params);
				break;
			case ".stationmap":
				error = StationMapExpression.validate(params);
				break;
			case ".persongenerator":
				error = PersonGeneratorExpression.validate(params);
				break;
			case ".locale":
				error = TrivialExpressions.validateLocale(params);
				break;
			case ".ucfirst":
				error = TrivialExpressions.validateUcfirst(params);
				break;
			case ".self":
				error = SelfExpression.validate(params, context);
				break;
			case ".if":
				error = ConditionExpression.validateIf(params);
				break;
			case ".select":
				error = ConditionExpression.validateSelect(params);
				break;
			case ".csv":
				error = CsvExpression.validate(params);
				break;
			case ".eq":
			case ".neq":
			case ".gt":
			case ".gte":
			case ".lt":
			case ".lte":
				error = CompareExpressions.validate(params, function);
				errorInfix = " in compare expression ";
				replaceWorldTimeSimplifyier();
				break;
			// TODO validate role,programme,person...
			case ".role":
			case ".programme":
			case ".person":
				break;
			default:
				error = "unknown function " + getFunction();
			}
			if (error != null) {
				error = error + errorInfix + all;
			}
		} else {
			if (!KNOWN_COMPLEX_FUNCTIONS.contains(getFunction())) {
				error = "unknown complex function " + getFunction();
			}
		}
		return error;
	}
}
