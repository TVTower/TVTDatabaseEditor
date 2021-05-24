package org.tvtower.db.constants;

public class EffectType extends TVTEnum {
	public static final String NEWS="triggernews";
	public static final String NEWS_CHOICE="triggernewschoice";
	public static final String PERSON="modifyPersonPopularity";
	public static final String GENRE="modifyMovieGenrePopularity";
	
	EffectType() {
		add(NEWS, "always");
		add(NEWS_CHOICE, "on every broadcast");
		add(PERSON, "after every broadcast");
		add(GENRE, "on the first broadcast");
	}

	public boolean isNewsTrigger(String type) {
		return type!=null && type.startsWith(NEWS);
	}

}
