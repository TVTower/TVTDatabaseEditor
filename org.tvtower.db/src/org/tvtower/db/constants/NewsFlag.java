package org.tvtower.db.constants;

public class NewsFlag extends TVTFlag {

	public final long INVISIBLE;
	public final long UNIQUE_EVENT;

	NewsFlag() {
		add("none");
		add("send immediately");
		UNIQUE_EVENT=add("unique event");
		add("unskippable");
		add("send to all");
		add("keep ticker time");
		add("reset ticker time");
		add("reset happen time");
		add("special event");
		INVISIBLE = add("invisible event");
	}
}
