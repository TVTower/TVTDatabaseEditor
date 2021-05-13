package org.tvtower.db.validation;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.InsignificantPeople;
import org.tvtower.db.database.Person;
import org.tvtower.db.database.PersonData;
import org.tvtower.db.database.PersonDetails;

import com.google.common.base.Strings;

//TODO general gender check
//TODO general country check, job, 
//TODO generator, face code?
//TODO boolean bookable, levelup, fictional
public class PersonsValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkGeneral(Person person) {
		if(person.getGenerator()==null) {
			if(Strings.isNullOrEmpty(person.getFirstName()) && Strings.isNullOrEmpty(person.getLastName())) {
				error("name fehlt", $.getPerson_Name());
			}
		}
	}

	@Check
	public void checkInsignificant(Person person) {
		if (isInsignificant(person)) {
			assertNotSet(person.getDetails(), "details", $.getPerson_Details());
			assertNotSet(person.getData(), "data", $.getPerson_Data());
		}
	}

	@Check
	public void checkCelebrity(Person person) {
		if (!isInsignificant(person)) {
			assertNotSet(person.getLevelUp(), "levelup", $.getPerson_LevelUp());

			if ("1".equals(person.getBookable())) {
				if (person.getGender() == null && (person.getData() == null || person.getDetails().getGender() == null)) {
					error("gender must be defined", $.getPerson_Name());
				}
				//TODO falls fictional - Fehler!
			}

			if (person.getData() == null) {
				//randomized
			}
			if (person.getDetails() == null) {
				error("details must be defined", $.getPerson_Name());
			}
			if (isFictional(person)) {
				if (!isGenderDefined(person)) {
					error("cannot be used in cast", $.getPerson_Name());
				}
			}
		}
	}

	@Check
	public void checkPersonData(PersonData d) {
		//TODO
	}

	@Check
	public void checkPersonDetails(PersonDetails d) {
		//TODO - check overwriting of person attributes?
	}

	private boolean isFictional(Person p) {
		return "1".equals(p.getFictional()) || (p.getDetails() != null && "1".equals(p.getDetails().getFictional()));
	}

	private boolean isGenderDefined(Person p) {
		return p.getGender() != null && !"0".equals(p.getGender()) || p.getDetails() != null
				&& p.getDetails().getGender() != null && !"0".equals(p.getDetails().getGender());
	}

	private void assertNotSet(Object value, String featureName, EStructuralFeature f) {
		if (value != null) {
			error(featureName + " must not be set", f);
		}
	}

	private boolean isInsignificant(Person p) {
		return p.eContainer() instanceof InsignificantPeople;
	}
}
