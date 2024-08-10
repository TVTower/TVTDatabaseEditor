package org.tvtower.db.validation.expressionhelper;

import java.util.Collection;

class AbstractExpression {

	protected static boolean isStringFromCollection(String param, Collection<String> allowedValues) {
		return isStringParam(param) && allowedValues.contains(param.substring(1, param.length() - 1));
	}

	protected static boolean isStringParam(String param) {
		return param.length() > 1 && param.charAt(0) == '"' && param.charAt(param.length() - 1) == '"';
	}
}
