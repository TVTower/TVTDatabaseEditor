package org.tvtower.db.constants;

import org.tvtower.db.database.NewsItem;

import com.google.common.collect.ImmutableMap;

public class NewsConstants {

	public static final String TYPE_START_NEWS="0";
	public static final String TYPE_FOLLOWUP_NEWS="2";

	public static final ImmutableMap<String, String> newsGenre=ImmutableMap.<String,String>builder()
			.put("0", "Politik/Wirtschaft")
			.put("1", "Showbiz")
			.put("2", "Sport")
			.put("3", "Technik/Medien")
			.put("4", "Tagesgeschehen")
			.put("5", "Kultur").build();

	public static boolean isStartNews(NewsItem item) {
		return TYPE_START_NEWS.equals(item.getType());
	}

	public static boolean isValidNewsType(String type) {
		return type!=null && (TYPE_START_NEWS.equals(type) ||TYPE_FOLLOWUP_NEWS.equals(type));
	}

	public static boolean isNewsGenre(String genre) {
		return newsGenre.containsKey(genre);
	}
}
