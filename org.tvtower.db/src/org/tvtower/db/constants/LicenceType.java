package org.tvtower.db.constants;

public class LicenceType extends TVTEnum {

	LicenceType() {
		add("0", "unknown");
		add("1", "single");
		add("2", "episode");
		add("3", "series");
		add("4", "collection");
		add("5", "collectionElement");
		add("6", "franchise");
	}

	//valid child type
	public String getChildType(String parentType) {
		switch (parentType) {
		case "3":
			return "2";
		case "4":
			return "5";
		default:
			return "";
		}
	}
}
