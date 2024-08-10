package org.tvtower.db.validation.expressionhelper;

import java.util.List;

import com.google.common.collect.ImmutableList;

class StationMapExpression extends AbstractExpression {

	// see game.gamescriptexpression.bmx#SEFN_StationMap
	private static final List<String> supportedParams = ImmutableList.of(//
			"randomcity", "population", "mapname", "mapnameshort");

	public static String validate(List<String> params) {
		if (params.size() != 1) {
			return "single parameter expected";
		} else if (!isStringFromCollection(params.get(0), supportedParams)) {
			return "unsupported parameter";
		}
		return null;
	}
}