package org.tvtower.db.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.print.attribute.standard.Severity;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.preferences.IPreferenceValues;
import org.eclipse.xtext.preferences.IPreferenceValuesProvider;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.ConfigurableIssueCodesProvider;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.eclipse.xtext.validation.SeverityConverter;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.constants.ModifierValueValidator;
import org.tvtower.db.constants.NewsType;
import org.tvtower.db.constants.TVTEnum;
import org.tvtower.db.database.Advertisement;
import org.tvtower.db.database.Availability;
import org.tvtower.db.database.ContainsLanguageStrings;
import org.tvtower.db.database.Database;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.GroupAttractivity;
import org.tvtower.db.database.LanguageString;
import org.tvtower.db.database.MayContainVariables;
import org.tvtower.db.database.Modifier;
import org.tvtower.db.database.NewsItem;
import org.tvtower.db.database.Programme;
import org.tvtower.db.database.ProgrammeGroups;
import org.tvtower.db.database.ScriptTemplate;
import org.tvtower.db.database.UnnamedProperty;
import org.tvtower.db.validation.expressionhelper.ScriptExpression;
import org.tvtower.db.validation.expressionhelper.SimpleExpression;

import com.google.common.base.Strings;
import com.google.inject.Inject;

//TODO validate created_by defined and not empty
public class CommonTagsValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;
	private static final Pattern SIMPLE_VARIABLE_PATTERN= Pattern.compile("\\$\\{(\\w+)\\}"); 

	@Inject
	private IPreferenceValuesProvider valuesProvider;
	@Inject
	private ConfigurableIssueCodesProvider issuCodeProvider;

	private boolean checkLocalizationDuplicates=false;
	
	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void setupLanguageStringValidation(Database db) {
		IPreferenceValues preferenceValues = valuesProvider.getPreferenceValues(db.eResource());
		String value = preferenceValues.getPreference(issuCodeProvider.getConfigurableIssueCodes()
				.get(DatabaseConfigurableIssueCodesProvider.VALIDATE_LOCALIZATION_DUPLICATES));
		checkLocalizationDuplicates=!SeverityConverter.SEVERITY_IGNORE.equals(value);
	}

	@Check
	public void checkAvailability(Availability a) {
		boolean scriptDefinesYear = checkScript(a);
		boolean hasYear = false;
		if (!Strings.isNullOrEmpty(a.getYearFrom())) {
			if (checkYear(a.getYearFrom(), $.getAvailability_YearFrom())) {
				hasYear = true;
			}
		}
		if (!Strings.isNullOrEmpty(a.getYearTo())) {
			if (checkYear(a.getYearTo(), $.getAvailability_YearTo())) {
				hasYear = true;
			}
		}
		if (scriptDefinesYear && hasYear) {
			error("script restricts year as well", $.getAvailability_Script());
		}
	}

	private boolean checkYear(String year, EAttribute f) {
		return checkNumber(year, true, 1950, 3000, "year", f);
	}

	//ScriptExpression checks simple expressions and parameters of worldtime comparisons
	private boolean checkScript(Availability a) {
		boolean result = false;
		String script = a.getScript();
		if (!Strings.isNullOrEmpty(script)) {
			ScriptExpression s=new ScriptExpression(script, a);
			result= s.hasYearExpression();
			for (String error : s.getErrors()) {
				error(error,$.getAvailability_Script());
			}
		}
		return result;
	}

	// returns whether the number is actually defined
	private boolean checkNumber(String number, boolean allowMinusOne, int min, int max, String period,
			EStructuralFeature f) {
		if ("-1".equals(number)) {
			if (!allowMinusOne) {
				error("-1 not allowed for " + period, f);
			}
			return false;
		} else {
			CommonValidation.getIntRangeError(number, period, min, max, true)
					.ifPresent(e -> error(period + ": " + e, f));
			return true;
		}
	}

	@Check(CheckType.NORMAL)
	public void checkLanguageStringsContainer(ContainsLanguageStrings c) {
		String globalText=c.getGlobal();
		if(globalText!=null) {
			if(germanContainsIllegalQuote(globalText)) {
				error("contains non-German quotes", c, $.getContainsLanguageStrings_Global());
			}
			validateExpressions(globalText, c, $.getContainsLanguageStrings_Global());

			if (checkLocalizationDuplicates && globalText.contains("${")
					&& (globalText.contains("cast") || globalText.contains("role"))) {
				addIssue("role/cast could be localized", c, $.getContainsLanguageStrings_Global(), DatabaseConfigurableIssueCodesProvider.VALIDATE_LOCALIZATION_DUPLICATES);
			}
		}

		Set<String> languages = new HashSet<>();
		AtomicInteger optionsCount = new AtomicInteger(-1);
		//prepare duplicate entry checks
//		boolean plMissing=true;

		if (checkLocalizationDuplicates) {
			int languageCount = c.getLstrings().size();
			if (languageCount == 0) {
			} else if (c.getLstrings().size() == 1) {
				LanguageString l = c.getLstrings().get(0);
				if (!"all".equals(l.getLangage()) // && languageCount>1
				) {
					addIssue("use unspecified/all languages pattern: " + l.getText(), l, $.getLanguageString_Langage(), DatabaseConfigurableIssueCodesProvider.VALIDATE_LOCALIZATION_DUPLICATES);
				}
			} else if (c.getLstrings().stream().map(l -> l.getText()).distinct().count() == 1) {
				addIssue("all identical: " + c.getLstrings().get(0).getText(), c, $.getContainsLanguageStrings_Lstrings(), DatabaseConfigurableIssueCodesProvider.VALIDATE_LOCALIZATION_DUPLICATES);
			}
		}

		for (LanguageString l : c.getLstrings()) {
			String language = l.getLangage();
			if (languages.contains(language)) {
				error("duplicate language", l, $.getLanguageString_Langage());
			} else {
				languages.add(language);
			}
			validateOptionsCount(l, optionsCount);
			if("de".equals(language)) {
				String content=l.getText();
				if(germanContainsIllegalQuote(content)) {
					error("contains non-German quotes", l, $.getLanguageString_Langage());
				}
			}
//			if("pl".equals(language)) {
//				plMissing=false;
//			}
		}
		
//		if(plMissing && c instanceof Title) {
//			error("polish missing",c.getLstrings().get(0), $.getLanguageString_Langage());
//		}
	}

	private boolean germanContainsIllegalQuote(String s) {
		if (Strings.isNullOrEmpty(s)) {
			return false;
		} else if (s.indexOf('”') >= 0) {
			return true;
		} else {
			int expCount = 0;
			char[] arr = s.toCharArray();
			char current;
			for (int i = 0; i < arr.length; i++) {
				current = arr[i];
				if (expCount == 0 && current == '"') {
					return true;
				}
				if (current == '$') {
					if (arr[i + 1] == '{') {
						expCount++;
					}
				} else if (expCount > 0 && current == '}') {
					expCount--;
				}
			}
		}
		return false;
	}

	private void validateOptionsCount(LanguageString s, AtomicInteger optionsCount) {
		int count = -1;
		if (!Strings.isNullOrEmpty(s.getText())) {
			//TODO what about options within an expression
			count = s.getText().split("\\|").length;
		}
		if (optionsCount.get() < 0) {
			if (count >= 0) {
				optionsCount.set(count);
			}
		} else if (count >= 0 && count != optionsCount.get()) {
			error("options count mismatch with other language", s, $.getLanguageString_Text());
		}
	}

	//TODO implement variable validation for followup news
	private boolean isFollowUpNews(EObject o) {
		NewsItem newsItem = EcoreUtil2.getContainerOfType(o, NewsItem.class);
		if(newsItem!=null && NewsType.FOLLOW_UP_NEWS.equals(newsItem.getType())) {
			return true;
		}
		return false;
	}

	@Check(CheckType.NORMAL)
	public void checkLanguageString(LanguageString s) {
		if (!s.getLangage().equals(s.getLangage2())) {
			error("language tags do not match", $.getLanguageString_Langage2());
		}
		String text = s.getText();
		if (text != null) {
			validateExpressions(text, s, $.getLanguageString_Text());
		}
	}

	private void validateExpressions(String text, EObject context, EStructuralFeature f) {
		List<String> definedVariables=null;
		//we don't do full expression evaluation only simple variables ${abc}
		//and simple functions ${.name:parameter1:parameter2} without inner expressions

		//first simple variables
		Matcher simpleVariableMatcher = SIMPLE_VARIABLE_PATTERN.matcher(text);
		while (simpleVariableMatcher.find()) {
			String variable = simpleVariableMatcher.group(1);
			if (definedVariables == null) {
				definedVariables = getVariablesFromContainers(context);
			}
			if(definedVariables.isEmpty()) {
				if(!isFollowUpNews(context)) {
					error("no variable definitions found", f);
				}
				continue;
			}
			boolean found = false;
			for (String def : definedVariables) {
				if (variable.equalsIgnoreCase(def)) {
					found = true;
					break;
				}
			}
			if (!found) {
				error("simple variable " + variable + " not defined", f);
			}
		}

		//TODO nested variable ${prefix_${variant}}

		//then function calls
		//personGenerator, random city, roles/persons depending on context
		List<SimpleExpression> simpleExpressions=SimpleExpression.get(text);
//		MayContainVariables vContainer = EcoreUtil2.getContainerOfType(s, MayContainVariables.class);
//		if (varibaleContainer instanceof ScriptTemplate) {
		for (SimpleExpression exp : simpleExpressions) {
			String error = exp.getValidationError(context);
			if (error != null) {
				error(error, f);
			}
//			//TODO param could be a variable - search in definedVariables
//			System.out.println(fct+" "+params);
		}
	}


	private List<String> getVariablesFromContainers(EObject o) {
		MayContainVariables vContainer = EcoreUtil2.getContainerOfType(o, MayContainVariables.class);
		if (vContainer == null) {
			return new ArrayList<>();
		} else {
			List<String> result = getVariablesFromContainers(vContainer.eContainer());
			if(vContainer.getVariables()!=null) {
				result.addAll(vContainer.getVariables().getVariable().stream().map(v->v.getVar()).collect(Collectors.toList()));
			}
			return result;
		}
	}

	@Check
	public void checkGroupAttractivity(GroupAttractivity a) {
		Set<String> defined = new HashSet<>();
		List<String> validKeys = Constants.targetgroup.maleFemale();
		for (UnnamedProperty prop : a.getData()) {
			boolean checkNumber=true;
			String key = prop.getKey();
			if("comment".equals(key)) {
				checkNumber=false;
			}else if (!validKeys.contains(key)) {
				error("invalid group", prop, $.getUnnamedProperty_Key());
			} else if (defined.contains(key)) {
				error("duplicate group", prop, $.getUnnamedProperty_Key());
			} else {
				defined.add(key);
			}
			if(checkNumber) {
				CommonValidation.getDecimalRangeError(prop.getValue(), key, BigDecimal.ZERO, new BigDecimal(2), true)
				.ifPresent(e -> error(e, prop, $.getUnnamedProperty_Value()));
			}
		}
	}

	@Check
	public void checkProgrammeGroups(ProgrammeGroups g) {
		Constants.targetgroup.isValidFlag(g.getTargetGroup(), "target_groups", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_TargetGroup()));
		Constants.pressuregroup.isValidFlag(g.getProPressureGroup(), "pro_pressure_groups", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_ProPressureGroup()));
		Constants.pressuregroup.isValidFlag(g.getContraPressureGroup(), "contra_pressure_groups", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_ContraPressureGroup()));
	}

	@Check
	public void checkModifiers(Modifier modifier) {
		TVTEnum validator = null;
		EObject container = modifier.eContainer().eContainer();
		if (container instanceof ScriptTemplate || container instanceof Programme) {
			validator = Constants.programmeModifier;
		} else if (container instanceof Advertisement) {
			validator = Constants.adModifier;
		}
		if (container instanceof NewsItem) {
			validator = Constants.newsModifier;
		}
		if (validator != null && validator instanceof ModifierValueValidator) {
			validator.isValidValue(modifier.getModName(), "modifier", true)
					.ifPresent(e -> error(e, modifier, $.getModifier_ModName()));
			((ModifierValueValidator) validator).getValueError(modifier)
					.ifPresent(e -> error(e, modifier, $.getModifier_Value()));
		}
	}

}
