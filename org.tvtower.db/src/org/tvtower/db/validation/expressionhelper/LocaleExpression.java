package org.tvtower.db.validation.expressionhelper;

import java.util.List;

class LocaleExpression extends AbstractExpression {

	static String validate(List<String> params) {
		int paramCount = params.size();
		if (paramCount < 1 || paramCount > 2) {
			return "one or two parameters expected";
		}
		// TODO supported languages?
		if (paramCount == 2 && !isStringParam(params.get(1))) {
			return "language parameter must be a string";
		}
		return null;
	}
}
