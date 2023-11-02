package org.tvtower.db.validation;

import java.math.BigDecimal;
import java.util.Optional;

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
					.ifPresent(e -> error(e, $.getPerson_Name()));
		}
		Constants.job.isValidFlag(person.getJob(), "job", false).ifPresent(e -> error(e, $.getPerson_Job()));
		Constants._boolean.isValidValue(person.getFictional(), "fictional", false)
				.ifPresent(e -> error(e, $.getPerson_Fictional()));
		Constants._boolean.isValidValue(person.getBookable(), "bookable", false)
				.ifPresent(e -> error(e, $.getPerson_Bookable()));
		if(person.getFictional()!=null && person.getDetails()!=null && person.getDetails().getFictional()!=null){
			error("fictional defined multiple times", $.getPerson_Fictional());
		}
		checkNameChanges(person);
		checkDates(person);
	}

	@Check
	public void checkInsignificant(Person person) {
		if (isInsignificant(person)) {
			assertNotSet(person.getDetails(), "details", $.getPerson_Details());
			assertNotSet(person.getData(), "data", $.getPerson_Data());
			CommonValidation.getCountryError(person.getCountry(), false)
					.ifPresent(e -> error(e, $.getPerson_Country()));
			Constants.gender.isValidValue(person.getGender(), "gender", false)
					.ifPresent(e -> error(e, $.getPerson_Gender()));
		}
	}

	@Check
	public void checkCelebrity(Person person) {
		if (!isInsignificant(person)) {
			assertNotSet(person.getLevelUp(), "levelup", $.getPerson_LevelUp());

			if ("1".equals(person.getBookable())) {
				if (!isGenderDefined(person)) {
					error("gender must be defined", $.getPerson_Name());
				}
			}
			if (PersonUtil.isFictional(person)) {
				if (!isGenderDefined(person)) {
					error("cannot be used in cast", $.getPerson_Name());
				}
			}
			defineInDetails(person.getCountry(), "country", $.getPerson_Country());
			defineInDetails(person.getJob(), "job", $.getPerson_Job());
			defineInDetails(person.getGender(), "gender", $.getPerson_Gender());

			if (person.getData() == null) {
				// randomized
			}
			if (person.getDetails() == null) {
				error("details must be defined", $.getPerson_Name());
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
		Constants.gender.isValidValue(d.getGender(), "gender", true)
				.ifPresent(e -> error(e, $.getPersonDetails_Gender()));
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
				.ifPresent(e -> error(e, $.getProgrammeRole_FirstName()));
//		CommonValidation.getValueMissingError("last_name", r.getLastName())
//				.ifPresent(e -> warning(e, $.getProgrammeRole_LastName()));
		CommonValidation.getCountryError(r.getCountry(), false).ifPresent(e -> error(e, $.getProgrammeRole_Country()));
		Constants.gender.isValidValue(r.getGender(), "gender", false)
				.ifPresent(e -> error(e, $.getProgrammeRole_Gender()));
		if (Constants.gender.isUndefined(r.getGender())) {
			warning("undefined gender", $.getProgrammeRole_Gender());
		}
	}

	private void checkNameChanges(Person person) {
		if(!PersonUtil.isFictional(person)){
			if(!Strings.isNullOrEmpty(person.getNickName())){
				if(Strings.isNullOrEmpty(person.getNickNameOrig())){
					warning("Original Nickname is missing", $.getPerson_NickName());
				}
			}
			if(!isInsignificant(person)) {
				//there are non-fictional spoof versions in fictional file, where original names are not given
				if(!Strings.isNullOrEmpty(person.getFirstName())){
					if(Strings.isNullOrEmpty(person.getFirstNameOrig())){
						warning("Original First is missing", $.getPerson_FirstName());
					}
				}
				if(!Strings.isNullOrEmpty(person.getLastName())){
					if(Strings.isNullOrEmpty(person.getLastNameOrig())){
						warning("Original Last is missing", $.getPerson_LastName());
					}
				}
			}
		}

		//TODO check and improve names
//		if(person.getFirstNameOrig()!=null && person.getLastNameOrig()!=null) {
//			if((person.getLastNameOrig().startsWith(person.getLastName()) || person.getLastName().startsWith(person.getLastNameOrig()))
//					&&person.getFirstName().equals(person.getFirstNameOrig())){
//				error("names too similar", $.getPerson_LastNameOrig());
//			}
//		}
	}

	private void checkDates(Person person) {
		dateError(person.getBirthday()).ifPresent(e -> error(e, $.getPerson_Birthday()));
		dateError(person.getDeathday()).ifPresent(e -> error(e, $.getPerson_Deathday()));
		if(person.getDetails()!=null) {
			dateError(person.getDetails().getBirthday()).ifPresent(e -> error(e, person.getDetails(), $.getPersonDetails_Birthday()));
			dateError(person.getDetails().getDeathday()).ifPresent(e -> error(e, person.getDetails(), $.getPersonDetails_Deathday()));
		}
	}

	private Optional<String> dateError(String dateString) {
		if(dateString!=null) {
			if("-1".equals(dateString) ||dateString.startsWith("-1-")) {
				return Optional.of("relative year -1 is not supported");
			}
			//TODO further checks on dates
		}
		return Optional.empty();
	}
}
