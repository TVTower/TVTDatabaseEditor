package org.tvtower.db.constants;

import org.eclipse.xtext.EcoreUtil2;
import org.tvtower.db.database.Effect;
import org.tvtower.db.database.NewsItem;
import org.tvtower.db.database.ScriptTemplate;

public class TriggerType extends TVTEnum {

	TriggerType() {
		add("happen", "always");
		add("broadcast", "on every broadcast");
		add("broadcastDone", "after every broadcast");
		add("broadcastFirstTime", "on the first broadcast");
		add("broadcastFirstTimeDone", "when the first broadcast is done");
		add("productionStart", "on starting the production in the studio");
	}

	/**
	 * check if trigger type is allowed in the effect's context
	 * */
	public boolean isSupported(Effect e) {
		if("happen".equals(e.getTrigger())){
			if(EcoreUtil2.getContainerOfType(e, NewsItem.class)==null) {
				return false;
			}
		}else if("productionStart".equals(e.getTrigger())) {
			if(EcoreUtil2.getContainerOfType(e, ScriptTemplate.class)==null) {
				return false;
			}
		}
		return true;
	}

}
