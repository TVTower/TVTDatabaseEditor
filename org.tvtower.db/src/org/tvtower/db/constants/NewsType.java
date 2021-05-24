package org.tvtower.db.constants;

import org.tvtower.db.database.NewsItem;

public class NewsType extends TVTEnum {

	public static final String INITIAL_NEWS = "0";
	public static final String FOLLOW_UP_NEWS = "2";

	NewsType() {
		add(INITIAL_NEWS, "initial news");
		add(FOLLOW_UP_NEWS, "following news");
	}

	public boolean isStartNews(NewsItem item) {
		return item != null && INITIAL_NEWS.equals(item.getType());
	}
}
