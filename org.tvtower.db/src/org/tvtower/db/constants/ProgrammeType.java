package org.tvtower.db.constants;

public class ProgrammeType extends TVTEnum {

	public static final String SHOW="3";
	public static final String FEATURE="4";

	ProgrammeType() {
		add("0", "undefined");
		add("1", "movie");
		add("2", "series");
		add(SHOW, "show");
		add(FEATURE, "feature");
		add("5", "infomercial");
		add("6", "event");
		add("7", "other");
	}
}
