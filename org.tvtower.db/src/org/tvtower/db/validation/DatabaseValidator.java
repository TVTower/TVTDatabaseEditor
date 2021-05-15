/*
 * generated by Xtext 2.23.0
 */
package org.tvtower.db.validation;

import org.eclipse.xtext.validation.ComposedChecks;

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
@ComposedChecks(validators = { NewsValidator.class, CommonTagsValidator.class, PersonsValidator.class,
		AdValidator.class, ProgrammeValidator.class, ScriptValidator.class, AchievementValidator.class })
public class DatabaseValidator extends AbstractDatabaseValidator {
	
//	public static final String INVALID_NAME = "invalidName";
//
//	@Check
//	public void checkGreetingStartsWithCapital(Greeting greeting) {
//		if (!Character.isUpperCase(greeting.getName().charAt(0))) {
//			warning("Name should start with a capital",
//					DatabasePackage.Literals.GREETING__NAME,
//					INVALID_NAME);
//		}
//	}


}
