package org.tvtower.db.constants;

public class ProgrammeGenre extends TVTEnum {

	ProgrammeGenre() {
		add("0", "undefined");
		add("1", "adventure");
		add("2", "action");
		add("3", "animation");
		add("4", "crime");
		add("5", "comedy");
		add("6", "documentary");
		add("7", "drama");
		add("8", "erotic");
		add("9", "family");
		add("10", "fantasy");
		add("11", "history");
		add("12", "horror");
		add("13", "monumental");
		add("14", "mystery");
		add("15", "romance");
		add("16", "scifi");
		add("17", "thriller");
		add("18", "western");

		add("100", "show");
		add("101", "show - politics");
		add("102", "show - music");
		add("103", "talkshow");
		add("104", "gameshow");

		add("200", "event");
		add("201", "event - politics");
		add("202", "event - music");
		add("203", "event - sports");
		add("204", "event - showbiz");

		add("300", "feature");
		add("301", "feature - yellowpress");

		add("400", "infomercial");
		add("401", "newsspecial");
	}
}
