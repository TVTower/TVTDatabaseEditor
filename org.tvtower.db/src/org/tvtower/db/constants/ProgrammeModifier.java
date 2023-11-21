package org.tvtower.db.constants;

import java.math.BigDecimal;
import java.util.Optional;

import org.tvtower.db.database.Modifier;
import org.tvtower.db.validation.CommonValidation;

public class ProgrammeModifier extends TVTEnum implements ModifierValueValidator {
	public static final String PRICE = "price";
	public static final String AGE = "topicality::age";
	public static final String REFRESH = "topicality::refresh";
	public static final String TRAILER_REFRESH = "topicality::trailerRefresh";
	public static final String WEAROFF = "topicality::wearoff";
	public static final String TRAILER_WEAROFF = "topicality::trailerWearoff";
	public static final String FIRST_BROADCAST = "topicality::firstBroadcastDone";
	public static final String NOT_LIVE = "topicality::notLive";
	public static final String TIMES_BROADCASTED = "topicality::timesBroadcasted";
	public static final String PER_VIEWER_REVENUE = "callin::perViewerRevenue";

	private static final BigDecimal TWO = new BigDecimal(2);
	private static final BigDecimal FIVE = new BigDecimal(5);

	ProgrammeModifier() {
		add(PRICE, "price");
		add(AGE, "aging");
		add(REFRESH, "refresh rate");
		add(TRAILER_REFRESH, "refresh rate trailer");
		add(WEAROFF, "topicality loss on broadcast");
		add(TRAILER_WEAROFF, "waroff trailer on broadcast");
		add(FIRST_BROADCAST, "topicality loss on first broadcast");
		add(NOT_LIVE, "topicality loss if not live");
		add(TIMES_BROADCASTED, "topicality loss due to number of broadcasts");
		add(PER_VIEWER_REVENUE, "income per viewer for call-in");
	}

	@Override
	public Optional<String> getValueError(Modifier m) {
		BigDecimal min = BigDecimal.ZERO;
		// TODO modifier specific ranges
		BigDecimal max = TWO;
		if (AGE.equals(m.getModName())) {
			max = FIVE;
		}
		return CommonValidation.getDecimalRangeError(m.getValue(), "value", min, max, true);
	}
}
