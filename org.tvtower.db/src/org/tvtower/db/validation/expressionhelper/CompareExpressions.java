package org.tvtower.db.validation.expressionhelper;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class CompareExpressions extends AbstractExpression {

	private static final String WORLD_TIME = "worldTime";

	// TODO 2 parameters required in availability script?
	public static String validate(List<String> params, String operator) {
		int paramCount = params.size();
		if (paramCount != 2 && paramCount != 4) {
			return "2 or 4 parameters expected instead of " + paramCount;
		}
		for (String param : params) {
			if (Strings.isNullOrEmpty(param)) {
				return "empty parameter";
			}
		}
		String p1 = params.get(0);
		String p2 = params.get(1);

		String t1 = getType(p1);
		String t2 = getType(p2);
		if (t1 == null) {
			return "could not determine type of " + p1;
		} else if (t2 == null) {
			return "could not determine type of " + p2;
		}
		if (WORLD_TIME.equals(t2)) {
			String tmp = t1;
			t1 = t2;
			t2 = tmp;
			tmp = p1;
			p1 = p2;
			p1 = tmp;
		}
		if (WORLD_TIME.equals(t1)) {
			return WorldTimeExpression.checkWorldTimeRange(p1, p2, t2, operator);
		} else if (!("var".equals(t1) || ("var".equals(t2)) && !Objects.equal(t1, t2))) {
			return "type mismatch " + t1 + " vs " + t2;
		}
		return null;
	}

	private static String getType(String param) {
		if (isStringParam(param)) {
			return "string";
		} else if (param.startsWith("&wt_")) {
			return WORLD_TIME;
		} else {
			try {
				Long.parseLong(param);
				return "long";
			} catch (NumberFormatException e) {
				try {
					new BigDecimal(param);
					return "double";
				} catch (NumberFormatException e2) {

				}
			}
			if (param.matches("\\w+")) {
				return "var";
			}
		}
		return null;
	}
}
