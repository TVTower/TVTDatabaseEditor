package org.tvtower.db.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//TODO in some cases ranges allowed?
public class DatabaseTime {
	private String value;

	public DatabaseTime(String value) {
		this.value = value;
	}

	public Optional<String> getError() {
		if (value != null) {
			String[] segments = value.split(",", -1);
			String type = segments[0];
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

}
