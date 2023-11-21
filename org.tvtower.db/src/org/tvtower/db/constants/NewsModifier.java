package org.tvtower.db.constants;

import java.math.BigDecimal;
import java.util.Optional;

import org.tvtower.db.database.Modifier;
import org.tvtower.db.validation.CommonValidation;

public class NewsModifier extends TVTEnum implements ModifierValueValidator {
	public static final String PRICE = "price";
	public static final String AGE = "topicality::age";
	public static final String WEAROFF = "topicality::wearoff";
	public static final String TIMES_BROADCASTED = "topicality::timesBroadcasted";

	NewsModifier() {
		add(PRICE, "price");
		add(AGE, "aging");
		add(WEAROFF, "topicality loss on broadcast");
		add(TIMES_BROADCASTED, "topicality loss due to number of broadcasts");
	}

	@Override
	public Optional<String> getValueError(Modifier m) {
		BigDecimal min = BigDecimal.ZERO;
		BigDecimal max = new BigDecimal(2);
		// TODO modifier specific ranges
		return CommonValidation.getDecimalRangeError(m.getValue(), "value", min, max, true);
	}
}
