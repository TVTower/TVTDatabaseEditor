package org.tvtower.db.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.tvtower.db.constants.DayOfWeek;
import org.tvtower.db.constants.TVTHoverInfoCreator;

public class DatabaseTime implements TVTHoverInfoCreator {
	private static final DayOfWeek days = new DayOfWeek();

	private String value;
	private String[] segments;
	private String type;

	public DatabaseTime(String value) {
		setValue(value);
	}

	private void setValue(String value) {
		if (value != null) {
			segments = value.split(",", -1);
			type = segments[0];
		}
	}

	public Optional<String> getError() {
		if (value != null) {
			List<TimeSegmentValidator> validators = new ArrayList<>();
			switch (type) {
			case "0":
				break;
			case "1":
				validators.add(new TimeSegmentValidator(1, 0, 23));
				validators.add(new TimeSegmentValidator(2, 0, 23));
				break;
			case "2":
				validators.add(new TimeSegmentValidator(1, 0, Integer.MAX_VALUE));
				validators.add(new TimeSegmentValidator(2, 0, Integer.MAX_VALUE));
				validators.add(new TimeSegmentValidator(3, 0, 23));
				validators.add(new TimeSegmentValidator(4, 0, 23));
				break;
			case "3":
				validators.add(new TimeSegmentValidator(1, 1, 7));
				validators.add(new TimeSegmentValidator(2, 0, 23));
				validators.add(new TimeSegmentValidator(3, 0, 23));
				break;
			case "4":
				validators.add(new TimeSegmentValidator(1, 0, Integer.MAX_VALUE));
				validators.add(new TimeSegmentValidator(2, 1, 12));
				validators.add(new TimeSegmentValidator(3, 1, 31));
				validators.add(new TimeSegmentValidator(4, 0, 23));
				validators.add(new TimeSegmentValidator(5, 0, 59));
				break;
			case "5":
				validators.add(new TimeSegmentValidator(1, 0, Integer.MAX_VALUE));
				validators.add(new TimeSegmentValidator(2, 0, Integer.MAX_VALUE));
				validators.add(new TimeSegmentValidator(3, 1, 12));
				validators.add(new TimeSegmentValidator(4, 1, 12));
				validators.add(new TimeSegmentValidator(5, 1, 31));
				validators.add(new TimeSegmentValidator(6, 1, 31));
				validators.add(new TimeSegmentValidator(7, 0, 23));
				validators.add(new TimeSegmentValidator(8, 0, 23));
				validators.add(new TimeSegmentValidator(9, 0, 59));
				validators.add(new TimeSegmentValidator(10, 0, 59));
				break;
			case "6":
				validators.add(new TimeSegmentValidator(1, 0, Integer.MAX_VALUE));
				validators.add(new TimeSegmentValidator(2, 0, 365));
				validators.add(new TimeSegmentValidator(3, 0, 23));
				validators.add(new TimeSegmentValidator(4, 0, 59));
				break;
			case "7":
				validators.add(new TimeSegmentValidator(1, 0, Integer.MAX_VALUE));
				validators.add(new TimeSegmentValidator(2, 0, Integer.MAX_VALUE));
				validators.add(new TimeSegmentValidator(3, 0, 365));
				validators.add(new TimeSegmentValidator(4, 0, 365));
				validators.add(new TimeSegmentValidator(5, 0, 23));
				validators.add(new TimeSegmentValidator(6, 0, 23));
				validators.add(new TimeSegmentValidator(7, 0, 59));
				validators.add(new TimeSegmentValidator(8, 0, 59));
				break;
			case "8":
				validators.add(new TimeSegmentValidator(1, 0, Integer.MAX_VALUE));
				validators.add(new TimeSegmentValidator(2, 0, 23));
				validators.add(new TimeSegmentValidator(3, 0, 23));
				validators.add(new TimeSegmentValidator(4, 0, 59));
				validators.add(new TimeSegmentValidator(5, 0, 59));
				break;
			default:
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
			if (getError().isEmpty()) {
				switch (type) {
				case "0":
					return "immediately";
				case "1":
					if (p[1].equals(p[2])) {
						return String.format("in %s hours", p[1]);
					} else {
						return String.format("in %s to %s hours", p[1], p[2]);
					}
				case "2":// in x days between h1,h2
					return String.format("%s %s", inDays(p[1], p[2]), hours(p[3], p[4], "00", "00"));
				case "3":// next X between h1, h2
					return String.format("next %s %s", days.createHoverInfo(p[1]), hours(p[2], p[3], "00", "00"));
				case "4":
					// TODO relative year
					return String.format("%s-%s-%s %s", p[1], p[2], p[3], hours(p[4], p[4], p[5], p[5]));
				case "5":
					// TODO relative year?
					return String.format("between %s-%s-%s %s:%s and %s-%s-%s %s:%s", p[1], p[3], p[5], p[7], p[9],
							p[2], p[4], p[6], p[8], p[10]);
				case "6":
					// TODO relative year?
					return String.format("year %s game day %s %s:%s", p[1], p[2], p[3], p[4]);
				case "7":
					// TODO relative year?
					// TODO nicer minutes
					return String.format("between year %s game day %s %s:%s and year %s game day %s %s:%s", p[1], p[3],
							p[5], p[7], p[2], p[4], p[6], p[8]);
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
		String min1 = "?".equals(m1) ? "00" : m1;
		String min2 = "?".equals(m2) ? "00" : m2;
		if (h1.equals(h2) && m1.equals(m2)) {
			return String.format("at %s:%s", h1, min1);
		} else {
			return String.format("between %s:%s and %s:%s", h1, min1, h2, min2);
		}
	}
}
