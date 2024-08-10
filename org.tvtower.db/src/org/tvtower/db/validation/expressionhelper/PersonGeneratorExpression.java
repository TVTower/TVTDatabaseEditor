package org.tvtower.db.validation.expressionhelper;

import java.util.List;

import com.google.common.collect.ImmutableList;

//see game.gamescriptexpression.bmx#SEFN_PersonGenerator
//and base.util.persongenerator.bmx
class PersonGeneratorExpression extends AbstractExpression {

	private static final List<String> supportedNameParam = ImmutableList.of(//
			"name", "firstname", "lastname", "fullname", "title");
	private static final List<String> supportedGender = ImmutableList.of(//
			"m", "male", "f", "w", "female");

	public static String validate(List<String> params) {
		int paramCount = params.size();
		if (paramCount < 1 || paramCount > 4) {
			return "1-4 parameters expected";
		}
		String sub = params.get(0);
		if (!isStringFromCollection(sub, supportedNameParam)) {
			return "unsupported name parameter";
		}
		String country = params.get(1);
		if (!isStringParam(country)) {
			// TODO but it could be a variable returning the country...
			return "country parameter must be a string";
		}
		if (paramCount > 2) {
			String gender = params.get(2);
			if (!gender.equals("1") && !gender.equals("2")) {
				if (!isStringFromCollection(gender, supportedGender)) {
					return "unsupported gender parameter";
				}
			}
		}
		if (paramCount > 3) {
			String titleChance = params.get(3);
			try {
				double d = Double.parseDouble(titleChance);
				if (d < 0 || d > 1) {
					return "title chance must be between 0 and 1";
				}
			} catch (Exception e) {
				return "title chance must be a number";
			}
		}
		return null;
	}
}