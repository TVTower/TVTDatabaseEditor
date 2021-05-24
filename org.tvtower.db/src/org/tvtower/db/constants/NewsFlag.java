package org.tvtower.db.constants;

public class NewsFlag extends TVTFlag {

	NewsFlag() {
		add("none");
		add("unique event");
		add("send immediately");
		add("unskippable");
		add("send to all");
		add("keep ticker time");
		add("reset ticker time");
		add("reset happen time");
		add("special event");
		add("invisible event");
	}
}
