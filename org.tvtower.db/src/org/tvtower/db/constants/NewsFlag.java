package org.tvtower.db.constants;

public class NewsFlag extends TVTFlag {

	public final long INVISIBLE;

	NewsFlag() {
		add("none");
		add("send immediately");
		add("unique event");
		add("unskippable");
		add("send to all");
		add("keep ticker time");
		add("reset ticker time");
		add("reset happen time");
		add("special event");
		INVISIBLE = add("invisible event");
	}
}
