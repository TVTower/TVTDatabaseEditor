package org.tvtower.db.constants;

public class BroadcastFlag extends TVTFlag {

	public final long ALWAYS_LIVE;

	BroadcastFlag() {
		add("unknown");
		add("3rd-party material");
		add("not controllable");
		add("broadcast first time");
		add("broadcast first time special");
		add("broadcast first time done");
		add("broadcast first time special done");
		add("not available");
		add("hide price");
		add("broadcast limit active");
		ALWAYS_LIVE=add("always live");
		add("ignore player difficulty");
		add("ignored by betty");
		add("ignored by awards");
		add("exclusive to one owner");
		add("live time fixed");
		add("keep time slot restriction");
		add("time slot restriction active");
	}
}
