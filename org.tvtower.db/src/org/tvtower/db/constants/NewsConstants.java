package org.tvtower.db.constants;

import org.tvtower.db.database.NewsItem;

import com.google.common.collect.ImmutableMap;

//TODO rewrite
public class NewsConstants {

	public static final String TYPE_START_NEWS="0";
	public static final String TYPE_FOLLOWUP_NEWS="2";

	public static boolean isStartNews(NewsItem item) {
		return TYPE_START_NEWS.equals(item.getType());
	}

	public static boolean isValidNewsType(String type) {
		return type!=null && (TYPE_START_NEWS.equals(type) ||TYPE_FOLLOWUP_NEWS.equals(type));
	}
}
