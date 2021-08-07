package org.tvtower.db.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.tvtower.db.constants.DayOfWeek;
import org.tvtower.db.constants.TVTHoverInfoCreator;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class DatabaseTime implements TVTHoverInfoCreator {
	private static final DayOfWeek days = new DayOfWeek();
	private static final Map<String, List<TimeSegmentValidator>> defaultParameterValidators = createValidatorMap();

	private String type;
	private String[] segments;

	public DatabaseTime(String value) {
		setValue(value);
	}

	private static Map<String, List<TimeSegmentValidator>> createValidatorMap() {
		Map<String, List<TimeSegmentValidator>> result = new HashMap<>();
		result.put("0", Lists.newArrayList());
		result.put("1", Lists.newArrayList(intValidator(1), intValidator(2)));
		result.put("2", Lists.newArrayList(intValidator(1), intValidator(2), hourValidator(3), hourValidator(4)));
		result.put("3", Lists.newArrayList(new TimeSegmentValidator(1, 0, 6), hourValidator(2), hourValidator(3)));
		result.put("4", Lists.newArrayList(intValidator(1), new TimeSegmentValidator(2, 1, 12),
				new TimeSegmentValidator(3, 1, 31)));
		result.put("5", Lists.newArrayList(intValidator(1), intValidator(2), //
				new TimeSegmentValidator(3, 1, 12), new TimeSegmentValidator(4, 1, 12),
				new TimeSegmentValidator(5, 1, 31), new TimeSegmentValidator(6, 1, 31)));
		result.put("6", Lists.newArrayList(intValidator(1), new TimeSegmentValidator(2, 0, 365), hourValidator(3),
				minuteValidator(4)));
		result.put("7", Lists.newArrayList(intValidator(1), intValidator(2), //
				new TimeSegmentValidator(3, 0, 365), new TimeSegmentValidator(4, 0, 365), //
				hourValidator(5), hourValidator(6), minuteValidator(7), minuteValidator(8)));
		result.put("8", Lists.newArrayList(intValidator(1), //
				hourValidator(2), hourValidator(3), minuteValidator(4), minuteValidator(5)));
		return result;
	}

	private static TimeSegmentValidator intValidator(int index) {
		return new TimeSegmentValidator(index, 0, Integer.MAX_VALUE);
	}

	private static TimeSegmentValidator hourValidator(int index) {
		return new TimeSegmentValidator(index, 0, 23);
	}

	private static TimeSegmentValidator minuteValidator(int index) {
		return new TimeSegmentValidator(index, 0, 59);
	}

	private void setValue(String value) {
		if (value != null) {
			segments = value.split(",", -1);
			type = segments[0];
		}
	}

	public Optional<String> getError() {
		if (type != null) {
			List<TimeSegmentValidator> validators = defaultParameterValidators.get(type);
			if (validators == null) {
				return Optional.of("invalid time type");
			}
			int paramLength = validators.size();
			if (segments.length - 1 > paramLength) {
				return Optional.of("only " + paramLength + " parameters allowed");
			}
			for (TimeSegmentValidator validator : validators) {
				Optional<String> result = validator.getError(segments);
				if (result.isPresent()) {
					return result;
				}
			}
			Optional<String> rangeError = null;
			switch (type) {
			case "1":
				rangeError = getRangeError(segments, 1, 2);
				break;
			case "2":
				Optional<String> tmp = getRangeError(segments, 1, 2);
				rangeError = tmp.isPresent() ? tmp : getRangeError(segments, 3, 4);
				break;
			case "3":
				rangeError = getRangeError(segments, 2, 3);
				break;
			case "5":
				rangeError = type5Error(segments);
				break;
			case "7":
				rangeError = timeIntervalError(segments, 1, 4);
				break;
			case "8":
				rangeError = timeIntervalError(segments, 2, 2);
				break;
			default:
				break;
			}
			if (rangeError != null) {
				return rangeError;
			}
		}
		return Optional.empty();
	}

	private Optional<String> type5Error(String[] segments) {
		Optional<String> rangeError = timeIntervalError(segments, 1, 3);
		if (rangeError.isPresent()) {
			return rangeError;
		}
		if (segments.length > 2) {
			int year1 = Integer.parseInt(segments[1]);
			int year2 = Integer.parseInt(segments[2]);
			if (year1 < 1000 && year2 >= 1000 || year1 >= 1000 && year2 < 1000) {
				return Optional.of("relative and absolute year definitions must not be mixed");
			}
		}
		return Optional.empty();
	}

	private Optional<String> timeIntervalError(String[] segments, int startIndex, int iterations) {
		for (int i = 0; i < iterations; i++) {
			int minIndex = startIndex + 2 * i;
			int maxIndex = minIndex + 1;
			if (segments.length <= maxIndex) {
				break;
			}
			Optional<String> error = getRangeError(segments, minIndex, maxIndex);
			if (error.isPresent()) {
				return error;
			} else if (!segments[minIndex].equals(segments[maxIndex])) {
				break;
			}
		}
		return Optional.empty();
	}

	private static class TimeSegmentValidator {
		int index;
		int min;
		int max;

		public TimeSegmentValidator(int index, int min, int max) {
			this.index = index;
			this.min = min;
			this.max = max;
		}

		public Optional<String> getError(String[] segments) {
			if (index < segments.length) {
				String v = segments[index];
				Optional<String> error = CommonValidation.getIntRangeError(v, "parameter " + index, min, max, false);
				return error;
			}
			return Optional.empty();
		}
	}

	private Optional<String> getRangeError(String[] segments, int indexMin, int indexMax) {
		try {
			if (indexMax < segments.length) {
				if (Integer.parseInt(segments[indexMin]) > Integer.parseInt(segments[indexMax])) {
					return Optional.of("parameter " + indexMin + " is greater than parameter " + indexMax);
				}
			}
		} catch (Exception e) {
			// ignre format checked elsewhere
		}
		return Optional.empty();
	}

	@Override
	public String createHoverInfo(Object value) {
		if (value != null) {
			setValue(value.toString());
			String[] p = new String[15];
			for (int i = 0; i < 15; i++) {
				p[i] = "?";
			}
			for (int i = 0; i < segments.length; i++) {
				p[i] = segments[i];
			}
			if (!getError().isPresent()) {
				switch (type) {
				case "0":
					return "immediately";
				case "1":
					if (p[1].equals(p[2])) {
						return String.format("in %s hours", p[1]);
					} else {
						return String.format("in %s,..., or %s hours", p[1], p[2]);
					}
				case "2":// in x days between h1,h2
					return String.format("%s %s", inDays(p[1], p[2]), hours(p[3], p[4], "00", "00"));
				case "3":// next X between h1, h2
					return String.format("next %s %s", days.createHoverInfo(p[1]), hours(p[2], p[3], "00", "00"));
				case "4":
					if (Integer.parseInt(p[1]) < 1000) {
						return String.format("on %s/%s in %s years", p[2], p[3], p[1]);
					} else {
						return String.format("%s-%s-%s", p[1], p[2], p[3]);
					}
				case "5":
					if (Integer.parseInt(p[1]) < 1000) {
						if (p[1].equals(p[2])) {
							return String.format("between %s/%s and %s/%s in %s years", p[3], p[5], p[4], p[6], p[1]);
						} else {
							return String.format("between %s/%s in %s years and %s/%s in %s years", p[3], p[5], p[1],
									p[4], p[6], p[2]);
						}
					} else {
						return String.format("between %s-%s-%s and %s-%s-%s", p[1], p[3], p[5], p[2], p[4], p[6]);
					}
				case "6":
					return String.format("year %s game day %s %s:%s", p[1], p[2], p[3], p[4]);
				case "7":
					return String.format("between year %s game day %s %s:%s and year %s game day %s %s:%s", p[1], p[3],
							p[5], padMinute(p[7]), p[2], p[4], p[6], padMinute(p[8]));
				case "8":
					return String.format("on a work day %s days from now %s", p[1], hours(p[2], p[3], p[4], p[5]));
				default:
					return "some complicated Time";
				}
			}
		}
		return null;
	}

	private String inDays(String d1, String d2) {
		if (d1.equals(d2)) {
			return String.format("in %s days", d1);
		} else {
			return String.format("in %s to %s days", d1, d2);
		}
	}

	private String hours(String h1, String h2, String m1, String m2) {
		String min1 = padMinute(m1);
		String min2 = padMinute(m2);
		if (h1.equals(h2) && m1.equals(m2)) {
			return String.format("at %s:%s", h1, min1);
		} else {
			return String.format("between %s:%s and %s:%s", h1, min1, h2, min2);
		}
	}

	private String padMinute(String m) {
		return "?".equals(m) ? "00" : Strings.padStart(m, 2, '0');
	}
}
