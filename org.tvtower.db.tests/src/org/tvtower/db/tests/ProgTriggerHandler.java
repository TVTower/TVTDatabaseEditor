package org.tvtower.db.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.xtext.EcoreUtil2;
import org.tvtower.db.database.Effect;
import org.tvtower.db.database.Programme;

class ProgTriggerHandler {

	private Map<String, String> triggers = new HashMap<>();
	private Map<String, String> triggeredBy = new HashMap<>();

	public void add(Programme p) {
		// store triggers in both directions
		List<Effect> effects = EcoreUtil2.getAllContentsOfType(p, Effect.class);
		if (effects != null) {
			effects.forEach(e -> {
				if ("modifyProgrammeAvailability".equals(e.getType())) {
					String triggered = ((Programme) e.getGuid()).getName();
					triggers.put(p.getName(), triggered);
					triggeredBy.put(triggered, p.getName());
				}
			});
		}
	}

	public boolean isTriggered(Programme p) {
		return triggeredBy.containsKey(p.getName());
	}

	public int getMinimalYear(Programme p, Map<String, Integer> yearForProg) {
		// find minimal release year of all programmes in the trigger group
		String id = p.getName();
		int result = yearForProg.get(id);
		String tmp = id;
		do {
			int year = yearForProg.get(tmp);
			if (year < result) {
				result = year;
			}
			tmp = triggeredBy.get(tmp);
		} while (tmp != null);
		tmp = id;
		do {
			int year = yearForProg.get(tmp);
			if (year < result) {
				result = year;
			}
			tmp = triggers.get(tmp);
		} while (tmp != null);
		return result;
	}
}