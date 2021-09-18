package org.tvtower.db.constants;

public class LicenceType extends TVTEnum {

	public static final String SINGLE="1";
	public static final String EPISODE="2";
	public static final String SERIES="3";

	LicenceType() {
		add("0", "unknown");
		add(SINGLE, "single");
		add(EPISODE, "episode");
		add(SERIES, "series");
		add("4", "collection");
		add("5", "collectionElement");
		add("6", "franchise");
	}

	//valid child type
	public String getChildType(String parentType) {
		switch (parentType) {
		case SERIES:
			return EPISODE;
		case "4":
			return "5";
		default:
			return "";
		}
	}
}
