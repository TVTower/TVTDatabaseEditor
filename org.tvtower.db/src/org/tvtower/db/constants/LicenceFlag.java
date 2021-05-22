package org.tvtower.db.constants;

public class LicenceFlag extends TVTFlag {

	LicenceFlag() {
		add("none");
		add("tradeable");
		add("sell on reaching broadcast limit");
		add("remove on reaching broadcast limit");
		add("pool refills broadcast limits");
		add("pool refills topicality");
		add("pool removes tradeability");
	}
}
