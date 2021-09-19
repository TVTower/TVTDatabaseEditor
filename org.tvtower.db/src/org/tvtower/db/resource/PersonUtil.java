package org.tvtower.db.resource;

import org.tvtower.db.database.Person;

import com.google.common.base.Strings;

public class PersonUtil {

	public static boolean isFictional(Person p) {
		return p.getGenerator()!=null || 
				"1".equals(p.getFictional()) || 
				(p.getDetails() != null && "1".equals(p.getDetails().getFictional()));
	}

	public static String displayName(Person p) {
		return fromNames(p.getLastName(), p.getFirstName());
	}

	public static String fromNames(String lastName, String firstName) {
		String l = Strings.emptyToNull(lastName);
		String f = Strings.emptyToNull(firstName);
		if (l != null && f != null) {
			return l + ", " + f;
		} else if (l != null) {
			return l;
		} else if (f != null) {
			return f;
		}
		return null;
	}
}
