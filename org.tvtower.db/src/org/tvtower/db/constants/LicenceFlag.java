package org.tvtower.db.constants;

public class LicenceFlag extends TVTFlag {

	public final long REFILL_BROADCAST_LIMIT;
	private final long SELL;
	private final long REMOVE;
	
	LicenceFlag() {
		add("none");
		add("tradeable");
		SELL=add("sell on reaching broadcast limit");
		REMOVE=add("remove on reaching broadcast limit");
		REFILL_BROADCAST_LIMIT= add("pool refills broadcast limits");
		add("pool refills topicality");
		add("pool removes tradeability");
	}

	//is a flag set that defines what happens upon reachting the broadcast limit
	public boolean isLimitHandled(String value) {
		return hasFlag(value, SELL + REMOVE);
	}
}
