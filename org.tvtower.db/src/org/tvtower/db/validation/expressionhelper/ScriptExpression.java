package org.tvtower.db.validation.expressionhelper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

public class ScriptExpression {

	private List<String> errors = new ArrayList<>();
	private boolean definesYear;

	public ScriptExpression(String script, EObject context) {
		List<SimpleExpression> simpleExpressions = SimpleExpression.get(script);
		//script with worldtime functions replaced so that comparisons are "simple" scripts
		String scriptSimplifiedWithTypes=script;
		for (SimpleExpression e : simpleExpressions) {
			String error = e.getValidationError(context);
			if (error != null) {
				errors.add(error);
			} else if (".worldtime".equals(e.getFunction())) {
				if (e.getParams().get(0).contains("year")) {
					definesYear = true;
				}
				scriptSimplifiedWithTypes=scriptSimplifiedWithTypes.replace("${"+e.all+"}", "&wt_"+e.getParams().get(0));
			}
		}
		List<SimpleExpression> wtCompareExpressions = SimpleExpression.get(scriptSimplifiedWithTypes);
		for (SimpleExpression e : wtCompareExpressions) {
			String error = e.getValidationError(context);
			if (error != null) {
				errors.add(error);
			}
		}
	}

	public boolean hasYearExpression() {
		return definesYear;
	}

	public List<String> getErrors() {
		return errors;
	}
}
