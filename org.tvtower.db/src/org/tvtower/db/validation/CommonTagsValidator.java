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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.database.Advertisement;
import org.tvtower.db.database.Availability;
import org.tvtower.db.database.ContainsLanguageStrings;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.GroupAttractivity;
import org.tvtower.db.database.LanguageString;
import org.tvtower.db.database.MayContainVariables;
import org.tvtower.db.database.Modifier;
import org.tvtower.db.database.Programme;
import org.tvtower.db.database.ProgrammeGroups;
import org.tvtower.db.database.ScriptTemplate;
import org.tvtower.db.database.UnnamedProperty;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

//TODO globale ID-Eindeutigkeit
//TODO validate created_by defined and not empty
public class CommonTagsValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;
	private static BigDecimal _100=new BigDecimal(100);
	private static List<String> progModifiers=ImmutableList.of("price","topicality::age","topicality::refresh","topicality::trailerRefresh", "topicality::wearoff","topicality::trailerWearoff","topicality::firstBroadcastDone","topicality::notLive","topicality::timesBroadcasted","callin::perViewerRevenue");
	private static List<String> scriptProgModifiers=progModifiers;
	private static List<String> adModifiers=ImmutableList.of("topicality::infomercialRefresh","topicality::infomercialWearoff");

	@Override
	public void register(EValidatorRegistrar registrar) {
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
			error("script defines year as well", $.getAvailability_Script());
		}
	}

	private boolean checkYear(String year, EAttribute f) {
		return checkNumber(year, true, 1950, 3000, "year", f);
	}

	// Mögliche Werte in game.gamescriptexpression
	// TIME_X, mit X in YEAR, DAY, HOUR, MINUTE, WEEKDAY, SEASON, DAYSPLAYED,
	// YEARSPLAYED, DAYOFMONTH, DAYOFYEAR, MONTH, (ISNIGHT, ISDAWN, ISDAY, ISDUSK)
	// returns if script defines year
	private boolean checkScript(Availability a) {
		boolean result = false;
		Pattern logicalOperatorPattern = Pattern.compile("\\|\\| | &amp;&amp;");
		String compareOperatorPattern = "=|<=|>=";
		String script = a.getScript();
		if (!Strings.isNullOrEmpty(a.getScript())) {
			List<String> compontens = Splitter.on(logicalOperatorPattern).trimResults().omitEmptyStrings()
					.splitToList(script);
			for (String s : compontens) {
				String[] kv = s.split(compareOperatorPattern);
				if (kv.length != 2) {
					error("cannot recognize pattern " + s, $.getAvailability_Script());
				} else {
					int min = 0;
					int max = 0;
					String key = kv[0].trim();
					String value = kv[1].trim();
					switch (key) {
					case "TIME_YEAR":
						min = Constants.MIN_YEAR;
						max = Constants.MAX_YEAR;
						result = true;
						break;
//					case "TIME_DAY":

					case "TIME_SEASON":
						max = 4;
						break;
					case "TIME_MONTH":
						max = 12;
						break;
					case "TIME_WEEKDAY":
						max = 6;
						break;
					case "TIME_DAYOFYEAR":
						warning("dayofyear should not be used", $.getAvailability_Script());
						break;
					case "TIME_DAYSPLAYED":
						max = 10000;
						break;
					case "TIME_YEARSSPLAYED":
						max = 150;
						break;
					case "TIME_DAYSOFMONTH":
						max = 31;
						break;
					case "TIME_HOUR":
						max = 23;
						break;
					case "TIME_MINUTE":
						max = 59;
						break;
					case "TIME_ISNIGHT":
						max = 1;
						break;
					case "TIME_ISDAWN":
						max = 1;
						break;
					case "TIME_ISDAY":
						max = 1;
						break;
					case "TIME_ISDUSK":
						max = 1;
						break;
					default:
						error("no valid script key " + key, $.getAvailability_Script());
						return result;
					}
					checkNumber(value, false, min, max, key, $.getAvailability_Script());
				}
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
		Set<String> languages = new HashSet<>();
		AtomicInteger optionsCount = new AtomicInteger(-1);
		//prepare duplicate entry checks
//		Set<String> lcontent=new HashSet<>();
		for (LanguageString l : c.getLstrings()) {
			String language = l.getLangage();
			if (languages.contains(language)) {
				error("duplicate language", l, $.getLanguageString_Langage());
			} else {
				languages.add(language);
			}
//			String content = l.getText();
//			if (lcontent.contains(content)) {
//				error("duplicate content " + (Strings.isNullOrEmpty(content) ? "<EMPTY>" : content), l,
//						$.getLanguageString_Langage());
//			} else {
//				lcontent.add(content);
//			}
			validateOptionsCount(l, optionsCount);
		}
	}

	private void validateOptionsCount(LanguageString s, AtomicInteger optionsCount) {
		int count = -1;
		if (!Strings.isNullOrEmpty(s.getText())) {
			//remove reference like [1|Full] from the text
			count = s.getText().replaceAll("\\[\\w+\\|\\w+\\]", " ").split("\\|").length;
		}
		if (optionsCount.get() < 0) {
			if (count >= 0) {
				optionsCount.set(count);
			}
		} else if (count >= 0 && count != optionsCount.get()) {
			error("options count mismatch with other language", s, $.getLanguageString_Text());
		}
	}

	@Check(CheckType.NORMAL)
	public void checkLanguageString(LanguageString s) {
		if (!s.getLangage().equals(s.getLangage2())) {
			error("language tags do not match", $.getLanguageString_Langage2());
		}
		String text = s.getText();
		if (text != null) {
			List<String> definedVariables=null;
			// anything between percent not containing percent or space
			Pattern pattern = Pattern.compile("%([^%\\s]+)%");
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String variable = matcher.group(1);
				if (!Constants.globalVariable.isValidValue(variable, "", false).isPresent()) {
					continue;
				} else if (variable.startsWith("PERSONGENERATOR")) {
					// TODO check person generator variable
					continue;
				}
				if (definedVariables == null) {
					definedVariables = getVariablesFromContainers(s);
				}
				if(definedVariables.isEmpty()) {
					error("no variable definitions found", $.getLanguageString_Text());
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
					MayContainVariables varibaleContainer = EcoreUtil2.getContainerOfType(s, MayContainVariables.class);
					if (varibaleContainer instanceof ScriptTemplate) {
						if (!Constants.roleVariable.isValidValue(variable, "", false).isPresent()) {
							found = true;
						}
					}
				}
				if (!found) {
					error("variable " + variable + " not defined", $.getLanguageString_Text());
				}
			}
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
			String key = prop.getKey();
			if (!validKeys.contains(key)) {
				error("invalid group", prop, $.getUnnamedProperty_Key());
			} else if (defined.contains(key)) {
				error("duplicate group", prop, $.getUnnamedProperty_Key());
			} else {
				defined.add(key);
			}
			CommonValidation.getDecimalRangeError(prop.getValue(), key, BigDecimal.ZERO, BigDecimal.TEN, true)
					.ifPresent(e -> error(e, prop, $.getUnnamedProperty_Value()));
		}
	}

	@Check
	public void checkProgrammeGroups(ProgrammeGroups g) {
		Constants.targetgroup.isValidFlag(g.getTargetGroup(), "target_groups", true)
				.ifPresent(e -> error(e, $.getProgrammeGroups_TargetGroup()));
		Constants.targetgroup.isValidFlag(g.getOptionalTargetGroup(), "target_groups_optional", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_OptionalTargetGroup()));
		Constants.pressuregroup.isValidFlag(g.getProPressureGroup(), "pro_pressure_groups", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_ProPressureGroup()));
		Constants.pressuregroup.isValidFlag(g.getContraPressureGroup(), "contra_pressure_groups", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_ContraPressureGroup()));
	}

	@Check
	public void checkModifiers(Modifier modifier) {
		CommonValidation.getDecimalRangeError(modifier.getValue(), "value", BigDecimal.ZERO, _100, true)
				.ifPresent(e -> error(e, $.getModifier_Value()));
		EObject container=modifier.eContainer().eContainer();
		List<String> allowedNames=null;
		if(container instanceof ScriptTemplate) {
			allowedNames=scriptProgModifiers;
		}else if(container instanceof Programme) {
			allowedNames=progModifiers;
		}else if(container instanceof Advertisement) {
			allowedNames=adModifiers;
		}
		if(allowedNames!=null) {
			if(!allowedNames.contains(modifier.getModName())) {
				warning("unbekannter Modifier", $.getModifier_ModName());
			}
		}
	}

}
