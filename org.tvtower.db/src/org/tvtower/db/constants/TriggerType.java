package org.tvtower.db.constants;

public class TriggerType extends TVTEnum {

	TriggerType() {
		add("happen", "always");
		add("broadcast", "on every broadcast");
		add("broadcastDone", "after every broadcast");
		add("broadcastFirstTime", "on the first broadcast");
		add("broadcastFirstTimeDone", "when the first broadcast is done");
	}

}
