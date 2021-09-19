package org.tvtower.db.resource;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;
import org.tvtower.db.database.Person;
import org.tvtower.db.database.Programme;

public class DatabaseResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {

	public static final String PERSON_NAME_KEY="n";
	public static final String PERSON_FICTIONAL_KEY="f";
	public static final String PROGRAMME_TITLE_KEY="t";
	public static final String PROGRAMME_TYPE_KEY="k";

	@Override
	public boolean createEObjectDescriptions(EObject o, IAcceptor<IEObjectDescription> acceptor) {
		if(o instanceof Programme) {
			acceptor.accept(EObjectDescription.create(getQualifiedNameProvider().apply(o), o, programmeUserData((Programme)o)));
		} else if(o instanceof Person) {
			acceptor.accept(EObjectDescription.create(getQualifiedNameProvider().apply(o), o, personUserData((Person)o)));
		} else {
			return super.createEObjectDescriptions(o, acceptor);
		}
		return true;
	}

	private Map<String,String> personUserData(Person p){
		Map<String,String> result=new HashMap<>();
		String name=PersonUtil.displayName(p);
		if(name!=null) {
			result.put(PERSON_NAME_KEY, name);
		}
		if(PersonUtil.isFictional(p)) {
			result.put(PERSON_FICTIONAL_KEY,"1");
		}
		return result;
	}

	private Map<String,String> programmeUserData(Programme p){
		Map<String,String> result=new HashMap<>();
		if(p.getTitle()!=null && p.getTitle().getLstrings()!=null && !p.getTitle().getLstrings().isEmpty()) {
			String title=p.getTitle().getLstrings().get(0).getText();
			result.put(PROGRAMME_TITLE_KEY, title);
		}
		result.put(PROGRAMME_TYPE_KEY, p.getProduct());
		return result;
	}
}
