package org.tvtower.db.constants;

public class ScriptFlag extends TVTFlag {

	ScriptFlag() {
		add("none");
		add("tradeable");
		add("sell on reaching production limit");
		add("remove on reaching production limit");
		add("pool refills production limits");
		add("pool randomizes attributes");
		add("pool removes tradeability");
	}
}
