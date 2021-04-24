package org.tvtower.db.validation;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.database.Availability;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.LanguageString;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

//TODO validate availability script
public class CommonTagsValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

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

	//Mögliche Werte in game.gamescriptexpression
	//TIME_X, mit X in YEAR, DAY, HOUR, MINUTE, WEEKDAY, SEASON, DAYSPLAYED, YEARSPLAYED, DAYOFMONTH, DAYOFYEAR, (ISNIGHT, ISDAWN, ISDAY, ISDUSK) 
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
						min = 1950;
						max = 3000;
						result = true;
						break;
					case "TIME_SEASON":
						max = 4;
						break;
					case "TIME_WEEKDAY":
						max = 6;
						break;
//					case "TIME_DAYOFYEAR":
//						break;
					case "TIME_HOUR":
						max = 23;
						break;
					default:
						//TODO revert to error
						warning("no valid script key " + key, $.getAvailability_Script());
						return result;
					}
					checkNumber(value, false, min, max, key, $.getAvailability_Script());
				}
			}
		}
		return result;
	}

	//TODO extract for common usage
	// returns whether the number is actually defined
	private boolean checkNumber(String number, boolean allowMinusOne, int min, int max, String period,
			EStructuralFeature f) {
		if ("-1".equals(number)) {
			if (!allowMinusOne) {
				error("-1 not allowed for " + period, f);
			}
			return false;
		} else {
			try {
				int y = Integer.parseInt(number);
				if (y < min || y > max) {
					error(period + " out of range", f);
				}
			} catch (NumberFormatException e) {
				error("illegal value for " + period, f);
			}
			return true;
		}
	}

	@Check(CheckType.NORMAL)
	public void checkLanguageString(LanguageString s) {
		//TODO prüfe Variablenverwendung
		//Konsistenz Start/EndTag?
	}

}
