package org.tvtower.db.validation.expressionhelper;

import java.util.List;

public class ConditionExpression extends AbstractExpression {

	public static String validateIf(List<String> params) {
		int paramCount = params.size();
		if(paramCount<1 || paramCount>3) {
			return "1 to 3 parameters expected instead of " + paramCount;
		}
		return null;
	}

	public static String validateSelect(List<String> params) {
		int paramCount = params.size();
		if(paramCount%2!=0) {
			return "even number of parameters expected instead of "+paramCount;
		}
		return null;
	}
}
