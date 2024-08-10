package org.tvtower.db.validation.expressionhelper;

import java.util.List;
import java.util.Optional;

import org.tvtower.db.constants.Constants;
import org.tvtower.db.validation.CommonValidation;

import com.google.common.collect.ImmutableList;

class WorldTimeExpression extends AbstractExpression {

	// see game.gamescriptexpression.bmx#SEFN_WorldTime
	private static final List<String> supportedParams = ImmutableList.of(//
			"year", "month", "day", "hour", "minute", //
			"daysplayed", "dayplaying", "yearsplayed", //
			"weekday", "season", "dayofmonth", "dayofyear", //
			"isnight", "isdawn", "isday", "isdusk");

	public static String validate(List<String> params) {
		if (params.size() != 1) {
			return "single parameter expected";
		} else if (!isStringFromCollection(params.get(0), supportedParams)) {
			return "unsupported parameter";
		}
		return null;
	}

	// TODO check month < 1 etc. does not make sense?
	static String checkWorldTimeRange(String wtParameter, String compare, String compareType, String operator) {
		if (!"long".equals(compareType)) {
			return "comparison only possible with number";
		}
		int min = 0;
		int max = 0;
		String key = wtParameter.substring(5, wtParameter.length() - 1);
		switch (key) {
		case "year":
			min = Constants.MIN_YEAR;
			max = Constants.MAX_YEAR;
			break;
		case "month":
			min = 1;
			max = 12;
			break;
//		case "day":
		case "hour":
			max = 23;
			break;
		case "minute":
			max = 59;
			break;
		case "daysplayed":
		case "dayplaying":
			max = 10000;
			break;
		case "yearsplayed":
			max = 150;
		case "weekday":
			max = 6;
			break;
		case "season":
			min = 1;
			max = 4;
			break;
		case "dayofmonth":
			max = 31;
			break;
//		case "dayofyear":
//			warning("dayofyear should not be used");
//			break;
//		case "isnight":
//			max = 1;
//			break;
//		case "isdawn":
//			max = 1;
//			break;
//		case "isday":
//			max = 1;
//			break;
//		case "isdusk":
//			max = 1;
//			break;
		default:
			return key + " not supported";
		}
		return checkNumber(compare, min, max, key);
	}

	private static String checkNumber(String number, int min, int max, String period) {
		Optional<String> error = CommonValidation.getIntRangeError(number, period, min, max, true);
		if (error.isPresent()) {
			return error.get();
		}
		return null;
	}
}
