package org.tvtower.db.validation;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.Programme;
import org.tvtower.db.database.ProgrammeAttractivity;
import org.tvtower.db.database.ProgrammeData;
import org.tvtower.db.database.ProgrammeGroups;
import org.tvtower.db.database.ProgrammeModifier;
import org.tvtower.db.database.ProgrammeRatings;
import org.tvtower.db.database.ProgrammeReleaseTime;
import org.tvtower.db.database.Programmes;
import org.tvtower.db.database.Staff;

public class ProgrammeValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkProgramme(Programme p) {
		boolean isProgrammeHead=(p.eContainer() instanceof Programmes);
		if (p.getTitle() == null) {
			error("title must be defined", $.getProgramme_Id());
		}
		if (p.getDescription() == null && isProgrammeHead) {
			error("description must be defined", $.getProgramme_Id());
		}
		if (p.getData() == null) {
			if(isProgrammeHead) {
				error("data must be defined", $.getProgramme_Id());
			}
		} else {
			if (p.getData().getYear() != null && p.getReleaseTime() != null) {
				error("release time must be defined only once", $.getProgramme_ReleaseTime());
			}
		}
		if (p.getProduct() != null) {

		}
		if (p.getLicenceType() != null) {

		}
		// fictional
		// TODO check children
	}

	@Check
	public void checkProgrammeData(ProgrammeData d) {
		// TODO countries zusammensammeln
		if (d.getCountry() != null) {
		}
		if (d.getDistribution() != null) {

		}
		// TODO Country und Maingenre Pflicht, außer für "Children"
		if (d.getMaingenre() == null) {
//			error("genre missing",$.getProgrammeData_Blocks());
		}
	}

	@Check
	public void checkProgrammeRatings(ProgrammeRatings r) {

	}

	@Check
	public void checkProgrammeGroups(ProgrammeGroups g) {
//		if(g.getTargetGroup()!=null && !"0".equals(g.getTargetGroup())) {
//			error("targetGroup defined",$.getProgrammeGroups_TargetGroup());
//		}
//		if(g.getProPressureGroup()!=null && !"0".equals(g.getProPressureGroup())) {
//			error("pressureGroup defined",$.getProgrammeGroups_ProPressureGroup());
//		}
//		if(g.getContraPressureGroup()!=null && !"0".equals(g.getContraPressureGroup())) {
//			error("pressureGroup defined",$.getProgrammeGroups_ContraPressureGroup());
//		}
	}

	@Check
	public void checkProgrammeStaff(Staff staff) {

	}

	@Check
	public void checkModifiers(ProgrammeModifier modifier) {

	}

	@Check
	public void checkProgrammeAttractivity(ProgrammeAttractivity a) {

	}

	@Check
	public void checkProgrammeReleaseTime(ProgrammeReleaseTime t) {

	}
}
