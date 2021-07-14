package org.tvtower.db.constants;

import java.util.Optional;

public class JobFlag extends TVTFlag {

	JobFlag() {
		add("unkown");
		add("director");
		add("actor");
		add("scriptwriter");
		add("host");
		add("musician");
		add("supporting actor");
		add("guest");
		add("reporter");

		add("politician"); // 256
		add("painter");
		add("writer");
		add("model");
		add("sportsman");
	}

	public Optional<String> isValidCastJob(String value, String fieldName, boolean mandatory) {
		Optional<String> validSingleFlag = isValidSingleFlag(value, value, false);
		if (validSingleFlag.isPresent()) {
			return validSingleFlag;
		} else if (value != null && Integer.parseInt(value) > 128) {
			return Optional.of("not a valid cast job");
		}
		return Optional.empty();
	}
}
