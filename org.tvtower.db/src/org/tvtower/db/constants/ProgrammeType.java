package org.tvtower.db.constants;

public class ProgrammeType extends TVTEnum {

	ProgrammeType() {
		add("0", "undefined");
		add("1", "movie");
		add("2", "series");
		add("3", "show");
		add("4", "feature");
		add("5", "infomercial");
		add("6", "event");
		add("7", "other");
	}
}
