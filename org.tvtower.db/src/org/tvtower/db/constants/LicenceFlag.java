package org.tvtower.db.constants;

public class LicenceFlag extends TVTFlag {

	public final long REFILL_BROADCAST_LIMIT;
	
	LicenceFlag() {
		add("none");
		add("tradeable");
		add("sell on reaching broadcast limit");
		add("remove on reaching broadcast limit");
		REFILL_BROADCAST_LIMIT= add("pool refills broadcast limits");
		add("pool refills topicality");
		add("pool removes tradeability");
	}
}
