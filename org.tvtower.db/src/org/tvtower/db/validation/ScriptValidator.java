package org.tvtower.db.validation;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.database.ContainsMinMaxSlope;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.Job;
import org.tvtower.db.database.Jobs;
import org.tvtower.db.database.MinMaxSlope;
import org.tvtower.db.database.ScriptChildren;
import org.tvtower.db.database.ScriptData;
import org.tvtower.db.database.ScriptGenres;
import org.tvtower.db.database.ScriptTemplate;
import org.tvtower.db.database.ScriptTemplates;

public class ScriptValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;
	private static final int SEVENDAYS = 60 * 24 * 7;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkScript(ScriptTemplate t) {
		Constants.programmeType.isValidValue(t.getProduct(), "product", true)
				.ifPresent(e -> error(e, $.getScriptTemplate_Product()));
		Constants.licenceType.isValidValue(t.getLicenceType(), "licence_type", true)
				.ifPresent(e -> error(e, $.getScriptTemplate_LicenceType()));
		if ((t.eContainer() instanceof ScriptTemplates) && t.getIndex() != null) {
			error("index must not be defined for root elements", $.getScriptTemplate_Index());
		}

		if (t.getChildren() != null) {
			String expectedChildLicenceType = Constants.licenceType.getChildType(t.getLicenceType());
			String parentProduct = t.getProduct();

			ScriptChildren children = t.getChildren();
			for (int i = 0; i < children.getChild().size(); i++) {
				ScriptTemplate child = children.getChild().get(i);
				if (child.getProduct() != null && !child.getProduct().equals(parentProduct)) {
					error("product mismatch", child, $.getScriptTemplate_Product());
				}
				if (!expectedChildLicenceType.equals(child.getLicenceType())) {
					error("expect licence_type " + expectedChildLicenceType, child, $.getScriptTemplate_LicenceType());
				}
				if (child.getIndex() == null) {
					error("index must be defined", child, $.getScriptTemplate_Index());
				} else if (!("" + i).equals(child.getIndex())) {
					error("index " + i + " expected", child, $.getScriptTemplate_Index());
				}
			}
		}
		checkMinMaxSlope(t.getEpisodes(), 1, 32);
		checkMinMaxSlope(t.getStudioSize(), 1, 3);
		checkMinMaxSlope(t.getBlocks(), 1, 8);
		checkMinMaxSlope(t.getPrice(), 0, 10000000);
		checkMinMaxSlope(t.getPotential(), 0, 100);
		checkMinMaxSlope(t.getOutcome(), 0, 100);
		checkMinMaxSlope(t.getReview(), 0, 100);
		checkMinMaxSlope(t.getSpeed(), 0, 100);
		checkMinMaxSlope(t.getProductionTime(), 1, SEVENDAYS);
	}

	@Check
	public void checkScriptData(ScriptGenres g) {
		Constants.programmGenre.isValidValue(g.getMainGenre(), "maingenre", true)
				.ifPresent(e -> error(e, $.getScriptGenres_MainGenre()));
		Constants.programmGenre.isValidList(g.getSubgenres()).ifPresent(e -> error(e, $.getScriptGenres_Subgenres()));
	}

	@Check
	public void checkJobIndexes(Jobs jobs) {
		for (int i = 0; i < jobs.getJob().size(); i++) {
			Job job = jobs.getJob().get(i);
			if (job.getIndex() == null) {
				error("index must be defined", job, $.getJob_Index());
			} else if (!("" + i).equals(job.getIndex())) {
				error("index " + i + " expected", job, $.getJob_Index());
			}
		}
	}

	@Check
	public void checkJob(Job job) {
		Constants._boolean.isValidValue(job.getRequired(), "required", true)
				.ifPresent(e -> error(e, $.getJob_Required()));
		Constants.job.isValidSingleFlag(job.getFunction(), "function", true)
				.ifPresent(e -> error(e, $.getJob_Function()));
		Constants.gender.isValidValue(job.getGender(), "gender", false).ifPresent(e -> error(e, $.getJob_Gender()));
	}

	@Check
	public void checkScriptData(ScriptData d) {
		Constants.programmeFlag.isValidFlag(d.getFlags(), "flags", false)
				.ifPresent(e -> error(e, $.getScriptData_Flags()));
		Constants.programmeFlag.isValidFlag(d.getOptionalFlags(), "flags_optional", false)
				.ifPresent(e -> error(e, $.getScriptData_OptionalFlags()));
		Constants.licenceFlag.isValidFlag(d.getLicenceFlags(), "production_licence_flags", false)
				.ifPresent(e -> error(e, $.getScriptData_LicenceFlags()));
		// TODO livedate
		// TODO productionBroadcastFlags
		CommonValidation.getIntRangeError(d.getBroadcastTimeSlotStart(), "broadcast_time_slot_start", 0, 23, false)
				.ifPresent(e -> error(e, $.getScriptData_BroadcastTimeSlotStart()));
		CommonValidation.getIntRangeError(d.getBroadcastTimeSlotEnd(), "broadcast_time_slot_end", 0, 23, false)
				.ifPresent(e -> error(e, $.getScriptData_BroadcastTimeSlotStart()));
		CommonValidation.getIntRangeError(d.getProductionLimit(), "production_limit", 1, 256, false)
				.ifPresent(e -> error(e, $.getScriptData_ProductionLimit()));
		CommonValidation.getIntRangeError(d.getBroadcastLimit(), "production_broadcast_limit", 1, 256, false)
				.ifPresent(e -> error(e, $.getScriptData_BroadcastLimit()));
	}

	private void checkMinMaxSlope(ContainsMinMaxSlope container, int min, int max) {
		if (container != null) {
			MinMaxSlope data = container.getData();
			if (data.getValue() != null) {
				CommonValidation.getIntRangeError(data.getValue(), "value", min, max, true)
						.ifPresent(e -> error(e, data, $.getMinMaxSlope_Value()));
			} else {
				CommonValidation.getIntRangeError(data.getMin(), "min", min, max, true)
						.ifPresent(e -> error(e, data, $.getMinMaxSlope_Min()));
				CommonValidation.getIntRangeError(data.getMax(), "max", min, max, true)
						.ifPresent(e -> error(e, data, $.getMinMaxSlope_Max()));
				CommonValidation.getIntRangeError(data.getSlope(), "slope", 0, 100, false)
						.ifPresent(e -> error(e, data, $.getMinMaxSlope_Slope()));
				try {
					if (Integer.parseInt(data.getMin()) > Integer.parseInt(data.getMax())) {
						error("min must not be greater than max", data, $.getMinMaxSlope_Min());
					}
				} catch (NumberFormatException e) {
					// ignore - validation of min, already done
				}
			}
		}
	}

}
