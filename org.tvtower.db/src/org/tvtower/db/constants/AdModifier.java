package org.tvtower.db.constants;

import java.math.BigDecimal;
import java.util.Optional;

import org.tvtower.db.database.Modifier;
import org.tvtower.db.validation.CommonValidation;

public class AdModifier extends TVTEnum implements ModifierValueValidator {
	public static final String INFO_REFRESH = "topicality::infomercialRefresh";
	public static final String INFO_WEAROFF = "topicality::infomercialWearoff";

	AdModifier() {
		add(INFO_REFRESH, "refresh rate infomercial");
		add(INFO_WEAROFF, "wearoff infomercial on broadcast");
	}

	@Override
	public Optional<String> getValueError(Modifier m) {
		BigDecimal min = BigDecimal.ZERO;
		BigDecimal max = new BigDecimal(2);
		// TODO modifier specific ranges
		return CommonValidation.getDecimalRangeError(m.getValue(), "value", min, max, true);
	}
}
