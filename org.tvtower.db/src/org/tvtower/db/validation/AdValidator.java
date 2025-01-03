package org.tvtower.db.validation;

import java.math.BigDecimal;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.database.AdConditions;
import org.tvtower.db.database.Advertisement;
import org.tvtower.db.database.AdvertisementData;
import org.tvtower.db.database.DatabasePackage;

public class AdValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;
	private static final BigDecimal BD100 = new BigDecimal("100");

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkAd(Advertisement ad) {
		if (ad.getTitle() == null) {
			error("title must be defined", $.getAdvertisement_Name());
		}
		if (ad.getData() == null) {
			error("data must be defined", $.getAdvertisement_Name());
		}
		if (ad.getConditions() == null) {
			error("conditions must be defined", $.getAdvertisement_Name());
		}
		if (ad.getDescription() == null) {
			error("description must be defined", $.getAdvertisement_Name());
		}
	}

	@Check
	public void checkAdData(AdvertisementData d) {
		Constants._boolean.isValidValue(d.getAvailable(), "available", false)
				.ifPresent(e -> error(e, $.getAdvertisementData_Available()));
		Constants.adType.isValidValue(d.getType(), "type", false)
				.ifPresent(e -> error(e, $.getAdvertisementData_Type()));
		CommonValidation.getIntRangeError(d.getRepetitions(), "repetitions", 1, 16, true)
				.ifPresent(e -> error(e, $.getAdvertisementData_Repetitions()));
		CommonValidation.getIntRangeError(d.getDuration(), "duration", 0, 14, true)
				.ifPresent(e -> error(e, $.getAdvertisementData_Duration()));
		Constants._boolean.isValidValue(d.getFixPrice(), "fix_price", false)
				.ifPresent(e -> error(e, $.getAdvertisementData_FixPrice()));
		CommonValidation.getIntRangeError(d.getProfit(), "profit", 0, 10000, true)
				.ifPresent(e -> error(e, $.getAdvertisementData_Profit()));
		CommonValidation.getIntRangeError(d.getPenalty(), "penalty", 0, 10000, true)
				.ifPresent(e -> error(e, $.getAdvertisementData_Penalty()));
		Constants._boolean.isValidValue(d.getInfomercial(), "infomercial", false)
				.ifPresent(e -> error(e, $.getAdvertisementData_Infomercial()));
		CommonValidation.getIntRangeError(d.getQuality(), "quality", 0, 100, false)
				.ifPresent(e -> error(e, $.getAdvertisementData_Quality()));
		CommonValidation.getIntRangeError(d.getBlocks(), "blocks", 1, 5, false)
				.ifPresent(e -> error(e, $.getAdvertisementData_Blocks()));
		Constants._boolean.isValidValue(d.getFixInfomercialProfit(), "fix_infomercial_profit", false)
				.ifPresent(e -> error(e, $.getAdvertisementData_FixInfomercialProfit()));
		CommonValidation.getIntRangeError(d.getInfomercialProfit(), "infomercial_profit", 0, 10000, false)
				.ifPresent(e -> error(e, $.getAdvertisementData_InfomercialProfit()));
		Constants.pressuregroup.isValidFlag(d.getProPressureGroup(), "pro_pressure_groups", false)
				.ifPresent(e -> error(e, $.getAdvertisementData_ProPressureGroup()));
		Constants.pressuregroup.isValidFlag(d.getContraPressureGroup(), "contra_pressure_groups", false)
				.ifPresent(e -> error(e, $.getAdvertisementData_ContraPressureGroup()));

		if ("0".equals(d.getInfomercial())) {
			if (d.getInfomercialProfit() != null) {
				error("infomercial is disabled", $.getAdvertisementData_InfomercialProfit());
			}
			if (d.getFixInfomercialProfit() != null) {
				error("infomercial is disabled", $.getAdvertisementData_FixInfomercialProfit());
			}
			if (d.getBlocks() != null) {
				error("infomercial is disabled", $.getAdvertisementData_Blocks());
			}
		}
	}

	@Check
	public void checkAdConditions(AdConditions c) {
		CommonValidation.getDecimalRangeError(c.getMinAudience(), "min_audience", BigDecimal.ZERO, BD100, false)
				.ifPresent(e -> error(e, $.getAdConditions_MinAudience()));
		CommonValidation.getIntRangeError(c.getMinImage(), "min_image", 0, 100, false)
				.ifPresent(e -> error(e, $.getAdConditions_MinImage()));
		CommonValidation.getIntRangeError(c.getMaxImage(), "max_image", 0, 100, false)
				.ifPresent(e -> error(e, $.getAdConditions_MaxImage()));
		CommonValidation.getMinMaxError(c.getMinImage(), c.getMaxImage())
				.ifPresent(e -> error(e, $.getAdConditions_MinImage()));
		Constants.targetgroup.isValidFlag(c.getTargetGroup(), "target_group", false)
				.ifPresent(e -> error(e, $.getAdConditions_TargetGroup()));
		Constants.programmGenre.isValidValue(c.getAllowedGenre(), "allowed_genre", false)
				.ifPresent(e -> error(e, $.getAdConditions_AllowedGenre()));
		Constants.programmGenre.isValidValue(c.getProhibitedGenre(), "prohibited_genre", false)
				.ifPresent(e -> error(e, $.getAdConditions_ProhibitedGenre()));
		Constants.programmeFlag.isValidFlag(c.getAllowedProgrammeFlag(), "allowed_programme_flag", false)
				.ifPresent(e -> error(e, $.getAdConditions_AllowedProgrammeFlag()));
		Constants.programmeFlag.isValidFlag(c.getProhibitedProgrammeFlag(), "prohibited_programme_flag", false)
				.ifPresent(e -> error(e, $.getAdConditions_ProhibitedProgrammeFlag()));
		Constants.pressuregroup.isValidFlag(c.getProPressureGroup(), "pro_pressure_groups", false)
				.ifPresent(e -> error(e, $.getAdConditions_ProPressureGroup()));
		Constants.pressuregroup.isValidFlag(c.getContraPressureGroup(), "contra_pressure_groups", false)
				.ifPresent(e -> error(e, $.getAdConditions_ContraPressureGroup()));

		if (c.getProPressureGroup() != null && !"0".equals(c.getProPressureGroup())) {
			addIssue("pressure groups not yet supported", c, $.getAdConditions_ProPressureGroup(), DatabaseConfigurableIssueCodesProvider.UNSUPPORTED_ATTRIBUTE);
		}
		if (c.getContraPressureGroup() != null && !"0".equals(c.getContraPressureGroup())) {
			addIssue("pressure groups not yet supported",c, $.getAdConditions_ContraPressureGroup(), DatabaseConfigurableIssueCodesProvider.UNSUPPORTED_ATTRIBUTE);
		}
		if (c.getProhibitedGenre() != null && !"0".equals(c.getProhibitedGenre())) {
			addIssue("prohibited genre not yet supported",c, $.getAdConditions_ProhibitedGenre(), DatabaseConfigurableIssueCodesProvider.UNSUPPORTED_ATTRIBUTE);
		}
		if (c.getProhibitedProgrammeFlag() != null && !"0".equals(c.getProhibitedProgrammeFlag())) {
			addIssue("prohibited programme flag not yet supported",c, $.getAdConditions_ProhibitedProgrammeFlag(), DatabaseConfigurableIssueCodesProvider.UNSUPPORTED_ATTRIBUTE);
		}
	}

}
