package org.tvtower.db.constants;

public class EffectType extends TVTEnum {
	public static final String NEWS="triggernews";
	public static final String NEWS_CHOICE="triggernewschoice";
	public static final String PERSON="modifyPersonPopularity";
	public static final String GENRE="modifyMovieGenrePopularity";
	public static final String NEWS_AVAILABILITY="modifyNewsAvailability";
	
	EffectType() {
		add(NEWS, "news");
		add(NEWS_CHOICE, "one of several news");
		add(NEWS_AVAILABILITY, "news availability");
		add(PERSON, "change person popularity");
		add(GENRE, "change genre popularity");
	}

	public boolean isNewsTrigger(String type) {
		return type!=null && type.startsWith(NEWS);
	}

}
