package org.tvtower.db.validation;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.database.AdConditions;
import org.tvtower.db.database.Advertisement;
import org.tvtower.db.database.AdvertisementData;
import org.tvtower.db.database.DatabasePackage;

public class AdValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkAd(Advertisement ad) {
		if (ad.getTitle() == null) {
			error("title must be defined", $.getAdvertisement_Id());
		}
		if (ad.getData() == null) {
			error("data must be defined", $.getAdvertisement_Id());
		}
		if (ad.getConditions() == null) {
			error("conditions must be defined", $.getAdvertisement_Id());
		}
		if (ad.getDescription() == null) {
			error("description must be defined", $.getAdvertisement_Id());
		}
	}

	@Check
	public void checkAdData(AdvertisementData d) {
		if(d.getYearRangeFrom()!=null) {
			error("use availability", $.getAdvertisementData_YearRangeFrom());
		}
		if(d.getYearRangeTo()!=null) {
			error("use availability", $.getAdvertisementData_YearRangeTo());
		}
		//TODO
	}

	@Check
	public void checkAdConditions(AdConditions c) {
		//TODO check Targetgroups and pressure groups existing
		//TODO minImage 0-100
		//TODO programmeTypes
		//TODO audience in Mio?
		if (c.getProPressureGroup() != null && !"0".equals(c.getProPressureGroup())) {
			warning("pressure groups not yet supported", $.getAdConditions_ProPressureGroup());
		}
		if (c.getContraPressureGrouo() != null && !"0".equals(c.getContraPressureGrouo())) {
			warning("pressure groups not yet supported", $.getAdConditions_ContraPressureGrouo());
		}
	}

}
