package org.tvtower.db.validation;

import java.math.BigDecimal;
import java.util.Optional;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.InsignificantPeople;
import org.tvtower.db.database.Person;
import org.tvtower.db.database.PersonData;
import org.tvtower.db.database.PersonDetails;
import org.tvtower.db.database.ProgrammeRole;
import org.tvtower.db.resource.PersonUtil;

import com.google.common.base.Strings;

//TODO generator, face code?
public class PersonsValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkGeneral(Person person) {
		if (person.getGenerator() == null) {
			CommonValidation.getValueMissingError("name", person.getFirstName(), person.getLastName())
					.ifPresent(e -> error(e, $.getRoleOrPerson_Name()));
		} else if("0".equals(person.getFictional()) || (person.getDetails()!=null && "0".equals(person.getDetails().getFictional()))){
			error("generated person must be fictional", $.getPerson_Generator());
		}
		Constants.job.isValidFlag(person.getJob(), "job", false).ifPresent(e -> error(e, $.getPerson_Job()));
		Constants._boolean.isValidValue(person.getFictional(), "fictional", false)
				.ifPresent(e -> error(e, $.getPerson_Fictional()));
		Constants._boolean.isValidValue(person.getCastable(), "castable", false)
				.ifPresent(e -> error(e, $.getPerson_Castable()));
		if (person.getFictional() != null && person.getDetails() != null
				&& person.getDetails().getFictional() != null) {
			error("fictional defined multiple times", $.getPerson_Fictional());
		}
		if (!PersonUtil.isFictional(person) && "1".equals(person.getCastable())) {
			error("non-fictional persons may not be castable", $.getPerson_Castable());
		}
		checkNames(person);
		checkDates(person);
	}

	@Check
	public void checkInsignificant(Person person) {
		if (isInsignificant(person)) {
			assertNotSet(person.getDetails(), "details", $.getPerson_Details());
			assertNotSet(person.getData(), "data", $.getPerson_Data());
			CommonValidation.getCountryError(person.getCountry(), false)
					.ifPresent(e -> error(e, $.getRoleOrPerson_Country()));
			if (isCastable(person)) {
				Constants.gender.isValidValue(person.getGender(), "gender", false)
						.ifPresent(e -> error(e, $.getRoleOrPerson_Gender()));
			} else {
				//real persons cannot be cast, so other gender is OK
				Constants.gender.isValidNonCastableValue(person.getGender(), "gender", false)
						.ifPresent(e -> error(e, $.getRoleOrPerson_Gender()));
			}
		}
	}

	private boolean isCastable(Person p) {
		if ("1".equals(p.getCastable())) {
			return true;
		}
		if ("0".equals(p.getCastable())) {
			return false;
		}
		return PersonUtil.isFictional(p);
	}

	@Check
	public void checkCelebrity(Person person) {
		if (!isInsignificant(person)) {
			assertNotSet(person.getLevelUp(), "levelup", $.getPerson_LevelUp());

			if ("1".equals(person.getCastable())) {
				if (!isGenderDefined(person)) {
					error("gender must be defined", $.getRoleOrPerson_Name());
				}
			}
			if (PersonUtil.isFictional(person)) {
				if (!isGenderDefined(person)) {
					error("cannot be used in cast", $.getRoleOrPerson_Name());
				}
			}
			defineInDetails(person.getCountry(), "country", $.getRoleOrPerson_Country());
			defineInDetails(person.getJob(), "job", $.getPerson_Job());
			defineInDetails(person.getGender(), "gender", $.getRoleOrPerson_Gender());

			if (person.getData() == null) {
				// randomized
			}
			if (person.getDetails() == null) {
				error("details must be defined", $.getRoleOrPerson_Name());
			}
		}
	}

	private void defineInDetails(String value, String fieldName, EStructuralFeature feature) {
		if (value != null) {
			error(fieldName + "should be defined in details", feature);
		}
	}

	@Check
	public void checkPersonData(PersonData d) {
		CommonValidation.getIntRangeError(d.getPopularity(), "popularity", 0, 100, false)
				.ifPresent(e -> error(e, $.getPersonData_Popularity()));
		CommonValidation.getIntRangeError(d.getPopularityTarget(), "popularity_target", 0, 100, false)
				.ifPresent(e -> error(e, $.getPersonData_PopularityTarget()));
		CommonValidation.getIntRangeError(d.getAffinity(), "affinity", 0, 100, false)
				.ifPresent(e -> error(e, $.getPersonData_Affinity()));
		CommonValidation.getIntRangeError(d.getFame(), "fame", 0, 100, false)
				.ifPresent(e -> error(e, $.getPersonData_Fame()));
		CommonValidation.getIntRangeError(d.getScandalizing(), "scandalizing", 0, 100, false)
				.ifPresent(e -> error(e, $.getPersonData_Scandalizing()));
		CommonValidation.getIntRangeError(d.getPower(), "power", 0, 100, false)
				.ifPresent(e -> error(e, $.getPersonData_Power()));
		CommonValidation.getIntRangeError(d.getHumor(), "humor", 0, 100, false)
				.ifPresent(e -> error(e, $.getPersonData_Humor()));
		CommonValidation.getIntRangeError(d.getCharisma(), "charisma", 0, 100, false)
				.ifPresent(e -> error(e, $.getPersonData_Charisma()));
		CommonValidation.getIntRangeError(d.getAppearance(), "appearance", 0, 100, false)
				.ifPresent(e -> error(e, $.getPersonData_Appearance()));
		Constants.programmGenre.isValidValue(d.getTopGenre(), "topgenre", false)
				.ifPresent(e -> error(e, $.getPersonData_TopGenre()));
		CommonValidation.getDecimalRangeError(d.getPriceMod(), "price_mod", BigDecimal.ZERO, BigDecimal.TEN, false)
				.ifPresent(e -> error(e, $.getPersonData_PriceMod()));
	}

	@Check
	public void checkPersonDetails(PersonDetails d) {
		CommonValidation.getCountryError(d.getCountry(), false).ifPresent(e -> error(e, $.getPersonDetails_Country()));
		Constants.job.isValidFlag(d.getJob(), "job", false).ifPresent(e -> error(e, $.getPersonDetails_Job()));
		if (isCastable((Person) d.eContainer())) {
			Constants.gender.isValidValue(d.getGender(), "gender", true)
					.ifPresent(e -> error(e, $.getPersonDetails_Gender()));
		} else {
			Constants.gender.isValidNonCastableValue(d.getGender(), "gender", true)
					.ifPresent(e -> error(e, $.getPersonDetails_Gender()));
		}
		Constants._boolean.isValidValue(d.getFictional(), "fictional", false)
				.ifPresent(e -> error(e, $.getPersonDetails_Fictional()));
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

	@Check
	public void checkRole(ProgrammeRole r) {
		CommonValidation.getValueMissingError("name", r.getFirstName(), r.getLastName())
				.ifPresent(e -> error(e, $.getRoleOrPerson_FirstName()));
//		CommonValidation.getValueMissingError("last_name", r.getLastName())
//				.ifPresent(e -> warning(e, $.getProgrammeRole_LastName()));
		CommonValidation.getCountryError(r.getCountry(), false).ifPresent(e -> error(e, $.getRoleOrPerson_Country()));
		Constants.gender.isValidValue(r.getGender(), "gender", false)
				.ifPresent(e -> error(e, $.getRoleOrPerson_Gender()));
		if (Constants.gender.isUndefined(r.getGender())) {
			addIssue("undefined gender", r, $.getRoleOrPerson_Gender(),
					DatabaseConfigurableIssueCodesProvider.ROLE_UNDEFINED_GENDER);
		}

		checkNameEndsWithS(r.getFirstName(), r.getFirstNameOriginal(), r, $.getRoleOrPerson_FirstName());
		checkNameEndsWithS(r.getLastName(), r.getLastNameOriginal(), r, $.getRoleOrPerson_LastName());
		checkNameEndsWithS(r.getNickName(), r.getNickNameOriginal(), r, $.getRoleOrPerson_NickName());
	}

	private void checkNames(Person person) {
		if (!PersonUtil.isFictional(person)) {
			if (!Strings.isNullOrEmpty(person.getNickName())) {
				if (Strings.isNullOrEmpty(person.getNickNameOrig())) {
					warning("Original Nickname is missing", $.getRoleOrPerson_NickName());
				}
			}
			if (!isInsignificant(person)) {
				// there are non-fictional spoof versions in fictional file, where original
				// names are not given
				if (!Strings.isNullOrEmpty(person.getFirstName())) {
					if (Strings.isNullOrEmpty(person.getFirstNameOrig())) {
						warning("Original First is missing", $.getRoleOrPerson_FirstName());
					}
				}
				if (!Strings.isNullOrEmpty(person.getLastName())) {
					if (Strings.isNullOrEmpty(person.getLastNameOrig())) {
						warning("Original Last is missing", $.getRoleOrPerson_LastName());
					}
				}
			}
		}

		checkNameEndsWithS(person.getFirstName(), person.getFirstNameOrig(), person, $.getRoleOrPerson_FirstName());
		checkNameEndsWithS(person.getLastName(), person.getLastNameOrig(), person, $.getRoleOrPerson_LastName());
		checkNameEndsWithS(person.getNickName(), person.getNickNameOrig(), person, $.getRoleOrPerson_NickName());

		// TODO improve spoof name similarity
		if (person.getFirstNameOrig() != null && person.getLastNameOrig() != null) {
			if ((person.getLastNameOrig().startsWith(person.getLastName())
					|| person.getLastName().startsWith(person.getLastNameOrig()))
					&& person.getFirstName().equals(person.getFirstNameOrig())) {
				addIssue("spoof name (too?) similar " + person.getFirstName() + " " + person.getLastName(), person,
						$.getPerson_LastNameOrig(), DatabaseConfigurableIssueCodesProvider.PERSON_SPOOF_NAME);
			}
		}
	}

	private void checkNameEndsWithS(String name, String origName, EObject p, EAttribute att) {
		if (name != null && origName != null) {
			boolean ns = name.endsWith("s");
			boolean os = origName.endsWith("s");
			if (ns != os) {
				addIssue("name/orig not both ending with s: " + name + " " + origName, p, att,
						DatabaseConfigurableIssueCodesProvider.PERSON_NAME_ENDING);
			}
		}
	}

	private void checkDates(Person person) {
		dateError(person.getBirthday()).ifPresent(e -> error(e, $.getPerson_Birthday()));
		dateError(person.getDeathday()).ifPresent(e -> error(e, $.getPerson_Deathday()));
		if (person.getDetails() != null) {
			dateError(person.getDetails().getBirthday())
					.ifPresent(e -> error(e, person.getDetails(), $.getPersonDetails_Birthday()));
			dateError(person.getDetails().getDeathday())
					.ifPresent(e -> error(e, person.getDetails(), $.getPersonDetails_Deathday()));
		}
	}

	private Optional<String> dateError(String dateString) {
		if (dateString != null) {
			if ("-1".equals(dateString) || dateString.startsWith("-1-")) {
				return Optional.of("relative year -1 is not supported");
			}
			// TODO further checks on dates
		}
		return Optional.empty();
	}
}
