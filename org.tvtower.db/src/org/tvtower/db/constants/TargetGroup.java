package org.tvtower.db.constants;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class TargetGroup extends TVTFlag {

	private final List<String> maleFemale;

	TargetGroup() {
		add("all");
		add("children");
		add("teenagers");
		add("housewives");
		add("employees");
		add("unemployed");
		add("managers");
		add("pensioners");
		add("women");
		add("men");
		maleFemale=createList();
	}

	private List<String> createList() {
		List<String> result=new ArrayList<>();
		result.addAll(forContentAssist().values());
		result.remove("all");
		List<String> variants=new ArrayList<>();
		for (String s : result) {
			if(!s.contains("men") && !s.contains("wives")) {
				variants.add(s+"_male");
				variants.add(s+"_female");
			}
		}
		result.addAll(variants);
		return ImmutableList.copyOf(result);
	}

	public List<String> maleFemale(){
		return maleFemale;
	}
}
