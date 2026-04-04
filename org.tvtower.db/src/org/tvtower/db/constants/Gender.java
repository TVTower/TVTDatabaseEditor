package org.tvtower.db.constants;

import java.util.Optional;

public class Gender extends TVTEnum {

	Gender() {
		add("0", "undefined");
		add("1", "male");
		add("2", "female");
//		add("3", "other"); not officially supported
	}

	public boolean isUndefined(String value) {
		return "0".equals(value);
	}

	public Optional<String> isValidNonCastableValue(String value, String fieldName, boolean mandatory) {
		// as insignificants cannot used in cast, do not raise error if value is "3"
		return isValidValue("3".equals(value) ? "2" : value, fieldName, mandatory);
	}
}
