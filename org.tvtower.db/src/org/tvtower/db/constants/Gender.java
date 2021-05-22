package org.tvtower.db.constants;

public class Gender extends TVTEnum {

	Gender() {
		add("0", "undefined");
		add("1", "male");
		add("2", "female");
	}

	public boolean isUndefined(String value) {
		return "0".equals(value);
	}
}
