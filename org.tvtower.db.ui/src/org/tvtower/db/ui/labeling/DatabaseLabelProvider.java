/*
 * generated by Xtext 2.23.0
 */
package org.tvtower.db.ui.labeling;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;
import org.tvtower.db.database.Achievement;
import org.tvtower.db.database.Achievements;
import org.tvtower.db.database.Advertisement;
import org.tvtower.db.database.Advertisements;
import org.tvtower.db.database.CelebrityPeople;
import org.tvtower.db.database.InsignificantPeople;
import org.tvtower.db.database.LanguageString;
import org.tvtower.db.database.News;
import org.tvtower.db.database.NewsItem;
import org.tvtower.db.database.Person;
import org.tvtower.db.database.Programme;
import org.tvtower.db.database.ProgrammeRole;
import org.tvtower.db.database.ProgrammeRoles;
import org.tvtower.db.database.Programmes;
import org.tvtower.db.database.ScriptTemplate;
import org.tvtower.db.database.ScriptTemplates;
import org.tvtower.db.database.Title;

import com.google.common.base.Strings;
import com.google.inject.Inject;

/**
 * Provides labels for EObjects.
 * 
 * See
 * https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#label-provider
 */
public class DatabaseLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public DatabaseLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}

	String text(Achievements achievements) {
		return "Achievements";
	}

	String text(Achievement a) {
		String title = fromTitle(a.getTitle());
		if (title != null) {
			return title;
		}
		return a.getId();
	}

	String text(Advertisements achievements) {
		return "Advertisements";
	}

	String text(Advertisement a) {
		String title = fromTitle(a.getTitle());
		if (title != null) {
			return title;
		}
		return a.getId();
	}

	String text(News news) {
		return "News";
	}

	String text(NewsItem i) {
		String title = fromTitle(i.getTitle());
		if (title != null) {
			return title;
		}
		return i.getName();
	}

	String image(NewsItem ele) {
		if (ele.getData() != null && ele.getData().getGenre() != null) {
			return "news/genre" + ele.getData().getGenre() + ".png";
		}
		return null;
	}

	String text(Programmes programmes) {
		return "Programmes";
	}

	String text(Programme p) {
		String title = fromTitle(p.getTitle());
		if (title != null) {
			return title;
		}
		return p.getId();
	}

	String text(ScriptTemplates programmes) {
		return "ScriptTemplates";
	}

	String text(ScriptTemplate t) {
		String title = fromTitle(t.getTitle());
		if (title != null) {
			return title;
		}
		return t.getName();
	}

	String text(CelebrityPeople celebrities) {
		return "Celebrities";
	}

	String text(InsignificantPeople otherPeople) {
		return "other People";
	}

	String text(ProgrammeRoles roles) {
		return "Roles";
	}

	String text(ProgrammeRole role) {
		String result = fromNames(role.getLastName(), role.getFirstName());
		if (result != null) {
			return result;
		}
		return role.getName();
	}

	String text(Person p) {
		String result = fromNames(p.getLastName(), p.getFirstName());
		if (result != null) {
			return result;
		}
		return p.getName();
	}

	String fromNames(String lastName, String firstName) {
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

	private String fromTitle(Title t) {
		if (t != null) {
			EList<LanguageString> lStrings = t.getLstrings();
			if (!lStrings.isEmpty()) {
				return lStrings.get(0).getText();
			}
		}
		return null;
	}

	// Labels and icons can be computed like this:

//	String text(Greeting ele) {
//		return "A greeting to " + ele.getName();
//	}
//
//	String image(Greeting ele) {
//		return "Greeting.gif";
//	}
}
