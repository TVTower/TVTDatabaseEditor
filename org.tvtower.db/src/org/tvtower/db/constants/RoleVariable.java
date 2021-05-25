package org.tvtower.db.constants;

public class RoleVariable extends TVTEnum {

	RoleVariable() {
		add("GENRE","main genre");
		add("EPISODES","number of episodes");
		for(int i=1;i<8;i++) {
			addRole(i);
		}
	}

	private void addRole(int i) {
		add("ROLE"+i, "full name role "+i);
		add("ROLENAME"+i, "first name role "+i);
		
	}
}
