package org.tvtower.db.validation.expressionhelper;

import java.util.List;

class CsvExpression extends AbstractExpression {

	//TODO check types if possible
	static String validate(List<String> params) {
		int paramCount = params.size();
		if (paramCount < 2 || paramCount > 4) {
			return "two to four parameters expected";
		}
		return null;
	}
}
