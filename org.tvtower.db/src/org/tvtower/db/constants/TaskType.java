package org.tvtower.db.constants;

import java.util.Optional;

import org.tvtower.db.database.TaskData;
import org.tvtower.db.validation.CommonValidation;

//TODO case insensitive enum?
public class TaskType extends TVTEnum {

	TaskType() {
		add("reachAudience", "reachAudience");
		add("reachBroadcastArea", "reachBroadcastArea");
		add("BroadcastNewsShow", "broadcastNewsShow");
	}

	public Optional<String> getMinAudienceAbsError(TaskData d) {
		return getMinMaxError(isAudienceType(d), d.getMinAudienceAbs(), "minAudienceAbsolute", 0, Integer.MAX_VALUE);
	}

	public Optional<String> getMinAudiencePercentError(TaskData d) {
		return getMinMaxError(isAudienceType(d), d.getMinAudiencePercent(), "minAudienceQuote", 0, 100);
	}

	public Optional<String> getCheckMinuteError(TaskData d) {
		return getMinMaxError(isAudienceType(d), d.getCheckMinute(), "checkMinute", 0, 59);
	}

	public Optional<String> getCheckHourError(TaskData d) {
		return getMinMaxError(isAudienceType(d), d.getCheckHour(), "checkHour", 0, 23);
	}

	public Optional<String> getMinReachAbsError(TaskData d) {
		return getMinMaxError(isReachType(d), d.getMinReachAbs(), "minReachAbsolute", 0, Integer.MAX_VALUE);
	}

	public Optional<String> getMinReachPercentError(TaskData d) {
		return getMinMaxError(isReachType(d), d.getMinReachPercent(), "minReachPercentage", 0, 100);
	}

	private Optional<String> getMinMaxError(boolean typeMatch, String value, String fieldName, int min, int max) {
		if (typeMatch) {
			return CommonValidation.getIntRangeError(value, fieldName, min, max, false);
		} else if (value != null) {
			return Optional.of(fieldName + " not allowed for this type");
		}
		return Optional.empty();

	}

	public Optional<String> getKeyword1Error(TaskData d) {
		return getKeywordError(d, d.getKeyword1(), "keyword1");
	}

	public Optional<String> getKeyword2Error(TaskData d) {
		return getKeywordError(d, d.getKeyword2(), "keyword2");
	}

	public Optional<String> getKeyword3Error(TaskData d) {
		return getKeywordError(d, d.getKeyword2(), "keyword3");
	}

	private Optional<String> getKeywordError(TaskData d, String value, String fieldName) {
		if (isNewsType(d)) {
			// no real restriction
		} else if (value != null) {
			return Optional.of(fieldName + " not allowed for this type");
		}
		return Optional.empty();
	}

	public Optional<String> getGenre1Error(TaskData d) {
		return getGenreError(d, d.getGenre1(), "genre1");
	}

	public Optional<String> getGenre2Error(TaskData d) {
		return getGenreError(d, d.getGenre2(), "genre2");
	}

	public Optional<String> getGenre3Error(TaskData d) {
		return getGenreError(d, d.getGenre2(), "genre3");
	}

	private Optional<String> getGenreError(TaskData d, String value, String fieldName) {
		if (isNewsType(d)) {
			return Constants.newGenre.isValidValue(value, fieldName, false);
		} else if (value != null) {
			return Optional.of(fieldName + " not allowed for this type");
		}
		return Optional.empty();
	}

	public Optional<String> getQualityError(TaskData d, String value, String fieldName) {
		if (isNewsType(d)) {
			return CommonValidation.getIntRangeError(value, fieldName, 0, 100, false);
		} else if (value != null) {
			return Optional.of(fieldName + " not allowed for this type");
		}
		return Optional.empty();
	}

	private boolean isNewsType(TaskData d) {
		return "BroadcastNewsShow".equalsIgnoreCase(d.getType());
	}

	private boolean isAudienceType(TaskData d) {
		return "reachAudience".equalsIgnoreCase(d.getType());
	}

	private boolean isReachType(TaskData d) {
		return "reachBroadcastArea".equalsIgnoreCase(d.getType());
	}
}