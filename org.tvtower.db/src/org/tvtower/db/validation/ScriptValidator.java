package org.tvtower.db.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.BroadcastFlag;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.constants.LicenceFlag;
import org.tvtower.db.constants.LicenceType;
import org.tvtower.db.constants.ProgrammeDataFlag;
import org.tvtower.db.constants.ProgrammeType;
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

import com.google.common.base.Strings;

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
		boolean isRoot=t.eContainer() instanceof ScriptTemplates;
		if (isRoot && t.getIndex() != null) {
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
				if(child.getData()!=null && child.getData().getLicenceFlags() != null) {
					warning("be careful when defining child licence flags", child.getData(), $.getScriptData_LicenceFlags());
				}
				if(child.getData()!=null && child.getData().getScriptFlags() != null) {
					warning("be careful when defining child script flags", child.getData(), $.getScriptData_ScriptFlags());
				}
			}
		}
		checkMinMaxSlope(t.getStudioSize(), 1, 3);
		checkMinMaxSlope(t.getBlocks(), 1, 8);
		checkMinMaxSlope(t.getPrice(), 0, 10000000);
		checkMinMaxSlope(t.getPotential(), 0, 100);
		checkMinMaxSlope(t.getOutcome(), 0, 100);
		checkMinMaxSlope(t.getReview(), 0, 100);
		checkMinMaxSlope(t.getSpeed(), 0, 100);
		checkMinMaxSlope(t.getProductionTime(), 1, SEVENDAYS);

		if (t.getData() != null && t.getData().getProductionLimit() != null) {
			checkProductionLimit(t);
		}
		if (hasLiveFlag(t)) {
			checkLiveDateAndFlags(t);
		}
		validateEpisodes(t);
		if(isRoot) {
			checkJobIndexes(t);
		}
	}

	private boolean hasLiveFlag(ScriptTemplate t) {
		ProgrammeDataFlag flags = Constants.programmeFlag;
		return t.getData() != null && (flags.hasFlag(t.getData().getProgrammeFlags(), flags.LIVE)
				|| flags.hasFlag(t.getData().getOptionalProgrammeFlags(), flags.LIVE));
	}

	private void checkProductionLimit(ScriptTemplate t) {
		String limit = t.getData().getProductionLimit();
		if (!Strings.isNullOrEmpty(limit) && !"1".equals(limit)) {
			EStructuralFeature f = $.getScriptData_ProductionLimit();
			if (t.getChildren() != null) {
				error("limit definition not allowed if there are child templates", t.getData(), f);
			} else if (!LicenceType.SINGLE.equals(t.getLicenceType())) {
				error("limit definition only allowed for licence type 'single'", t.getData(), f);
			} else if (!(ProgrammeType.SHOW.equals(t.getProduct()) || ProgrammeType.FEATURE.equals(t.getProduct()))) {
				error("limit definition only allowed for product 'show'", t.getData(), f);
			}
		}
	}

	private void checkLiveDateAndFlags(ScriptTemplate t) {
		ScriptData data = t.getData();
		BroadcastFlag bcFlags = Constants.broadcastFlag;
		if (data.getLive_date() == null && (data.getBroadcastFlags() == null
				|| !bcFlags.hasFlag(data.getBroadcastFlags(), bcFlags.ALWAYS_LIVE))) {
			EStructuralFeature f = t.getData().getProgrammeFlags() != null ? $.getScriptData_ProgrammeFlags()
					: $.getScriptData_OptionalProgrammeFlags();
			warning("if the programme is to be live, either the live time has to be defined or the Always-Live-Flag has to be set",
					t.getData(), f);
		}
		LicenceFlag lFlags = Constants.licenceFlag;
		if(lFlags.hasFlag(data.getLicenceFlags(), lFlags.REFILL_BROADCAST_LIMIT)){
			warning("live scripts should not reset the broadcast limit (script can be bought again)",
					t.getData(), $.getScriptData_LicenceFlags());
		}
	}

	@Check
	public void checkScriptData(ScriptGenres g) {
		Constants.programmGenre.isValidValue(g.getMainGenre(), "maingenre", true)
				.ifPresent(e -> error(e, $.getScriptGenres_MainGenre()));
		Constants.programmGenre.isValidList(g.getSubgenres()).ifPresent(e -> error(e, $.getScriptGenres_Subgenres()));
	}

	private void validateEpisodes(ScriptTemplate t) {
		boolean hasEpisodes = t.getEpisodes() != null;
		if (hasEpisodes) {
			String licenceType = t.getLicenceType();
			if (LicenceType.SERIES.equals(licenceType)) {
				if (t.getChildren() == null) {
					error("if episodes are defined for a series, child templates must be present",
							$.getScriptTemplate_Episodes());
				} else {
					int count = t.getChildren().getChild().size();
					checkMinMaxSlope(t.getEpisodes(), 1, count);

					//making a pilot episode optional is OK
					int skipPilot = 0;
					if (count > 0 && t.getChildren().getChild().get(0).getEpisodes() != null) {
						MinMaxSlope pilotEpisodes = t.getChildren().getChild().get(0).getEpisodes().getData();
						if ("0".equals(pilotEpisodes.getMin())
								&& (pilotEpisodes.getMax() == null || "1".equals(pilotEpisodes.getMax()))) {
							skipPilot = 1;
						}
					}
					if (t.getChildren().getChild().stream().skip(skipPilot).anyMatch(c -> c.getEpisodes() != null)) {
						error("episodes in parent and children are not supported", $.getScriptTemplate_Episodes());
					}
				}
			} else if (LicenceType.EPISODE.equals(licenceType)) {
				checkMinMaxSlope(t.getEpisodes(), 0, 32);
			} else {
				error("episodes not supported for this licence type", t, $.getScriptTemplate_Episodes());
			}
		}
	}

	void checkJobIndexes(ScriptTemplate t) {
		Jobs jobs = t.getJobs();
		int parentJobSize = jobs.getJob().size();
		for (int i = 0; i < parentJobSize; i++) {
			Job job = jobs.getJob().get(i);
			if (job.getIndex() == null) {
				error("index must be defined", job, $.getJob_Index());
			} else if (!("" + i).equals(job.getIndex())) {
				error("index " + i + " expected", job, $.getJob_Index());
			}
		}
		if (t.getChildren() != null) {
			for (ScriptTemplate child : t.getChildren().getChild()) {
				jobs = child.getJobs();
				for (int i = 0; i < jobs.getJob().size(); i++) {
					Job job = jobs.getJob().get(i);
					if (job.getIndex() == null) {
						error("index must be defined", job, $.getJob_Index());
						// TODO support overriding a parent job?
					} else if (!("" + (i + parentJobSize)).equals(job.getIndex())) {
						error("index " + (i + parentJobSize) + " expected", job, $.getJob_Index());
					}
				}
			}
		}
	}

	@Check
	public void checkJob(Job job) {
		Constants._boolean.isValidValue(job.getRequired(), "required", true)
				.ifPresent(e -> error(e, $.getJob_Required()));
		Constants.job.isValidCastJob(job.getFunction(), "function", true).ifPresent(e -> error(e, $.getJob_Function()));
		Constants.gender.isValidValue(job.getGender(), "gender", false).ifPresent(e -> error(e, $.getJob_Gender()));
		Constants._boolean.isValidValue(job.getRandomRole(), "random_role", false)
				.ifPresent(e -> error(e, $.getJob_RandomRole()));
		//TODO generator country validation, propsal and hover
	}

	@Check
	public void checkScriptData(ScriptData d) {
		Constants.programmeFlag.isValidFlag(d.getProgrammeFlags(), "flags", false)
				.ifPresent(e -> error(e, $.getScriptData_ProgrammeFlags()));
		Constants.programmeFlag.isValidFlag(d.getOptionalProgrammeFlags(), "flags_optional", false)
				.ifPresent(e -> error(e, $.getScriptData_OptionalProgrammeFlags()));
		Constants.scriptFlag.isValidFlag(d.getScriptFlags(), "scriptflags", false)
				.ifPresent(e -> error(e, $.getScriptData_ScriptFlags()));
		Constants.targetgroup.isValidFlag(d.getTargetGroup(), "target_group", false)
				.ifPresent(e -> error(e, $.getScriptData_TargetGroup()));
		Constants.targetgroup.isValidFlag(d.getOptionalTargetGroup(), "target_group_optional", false)
				.ifPresent(e -> error(e, $.getScriptData_OptionalTargetGroup()));


		Constants.licenceFlag.isValidFlag(d.getLicenceFlags(), "production_licence_flags", false)
				.ifPresent(e -> error(e, $.getScriptData_LicenceFlags()));
		CommonValidation.getTimeError(d.getLive_date(), "live_date")
				.ifPresent(e -> error(e, $.getScriptData_Live_date()));
		Constants.broadcastFlag.isValidFlag(d.getBroadcastFlags(), "production_broadcast_flags", false)
				.ifPresent(e -> error(e, $.getScriptData_BroadcastFlags()));

		CommonValidation.getIntRangeError(d.getBroadcastTimeSlotStart(), "broadcast_time_slot_start", 0, 23, false)
				.ifPresent(e -> error(e, $.getScriptData_BroadcastTimeSlotStart()));
		CommonValidation.getIntRangeError(d.getBroadcastTimeSlotEnd(), "broadcast_time_slot_end", 0, 23, false)
				.ifPresent(e -> error(e, $.getScriptData_BroadcastTimeSlotEnd()));
		CommonValidation.getIntRangeError(d.getProductionLimit(), "production_limit", 1, 256, false)
				.ifPresent(e -> error(e, $.getScriptData_ProductionLimit()));
		CommonValidation.getIntRangeError(d.getBroadcastLimit(), "production_broadcast_limit", 1, 256, false)
				.ifPresent(e -> error(e, $.getScriptData_BroadcastLimit()));
		Constants._boolean.isValidValue(d.getAvailable(), "available", false)		
			.ifPresent(e -> error(e, $.getScriptData_Available()));


		ensureLimitHandling(d, d.getBroadcastLimit(), $.getScriptData_BroadcastLimit());
	}

	private void ensureLimitHandling(ScriptData d, String limit, EStructuralFeature limitFeature) {
		if (limit != null) {
			try {
				int l = Integer.parseInt(limit);
				if (l > 0) {
					List<String> applicableLicenceFlags = new ArrayList<>();
					applicableLicenceFlags.add(d.getLicenceFlags());
					ScriptTemplate script = ((ScriptTemplate) d.eContainer());
					if (script.eContainer() instanceof ScriptChildren) {
						ScriptTemplate parent = (ScriptTemplate) script.eContainer().eContainer();
						if (parent.getData() != null) {
							applicableLicenceFlags.add(parent.getData().getLicenceFlags());
						}
					}
					if (applicableLicenceFlags.stream().noneMatch(s -> Constants.licenceFlag.isLimitHandled(s))) {
						warning("if limit is defined, licence flag handling the limit should be set", d, limitFeature);
					}
				}
			} catch (NumberFormatException e) {
				// ignore
			}
		}
	}


	private void checkMinMaxSlope(ContainsMinMaxSlope container, int min, int max) {
		if (container != null) {
			MinMaxSlope data = container.getData();
			if (data.getValue() != null) {
				CommonValidation.getIntRangeError(data.getValue(), "value", min, max, true)
						.ifPresent(e -> error(e, data, $.getMinMaxSlope_Value()));
				// currently the grammar prevents this inconsistency, but the error message is
				// not very nice
//				assertNotSet(data.getMin(), "min", data,  $.getMinMaxSlope_Min());
//				assertNotSet(data.getMax(), "max", data,  $.getMinMaxSlope_Max());
//				assertNotSet(data.getSlope(), "slope", data,  $.getMinMaxSlope_Slope());
			} else {
				CommonValidation.getIntRangeError(data.getMin(), "min", min, max, true)
						.ifPresent(e -> error(e, data, $.getMinMaxSlope_Min()));
				CommonValidation.getIntRangeError(data.getMax(), "max", min, max, true)
						.ifPresent(e -> error(e, data, $.getMinMaxSlope_Max()));
				CommonValidation.getMinMaxError(data.getMin(), data.getMax())
						.ifPresent(e -> error(e, data, $.getMinMaxSlope_Min()));

				CommonValidation.getIntRangeError(data.getSlope(), "slope", 0, 100, false)
						.ifPresent(e -> error(e, data, $.getMinMaxSlope_Slope()));
//				assertNotSet(data.getValue(), "value", data,  $.getMinMaxSlope_Value());
			}
		}
	}

//	private void assertNotSet(String value, String field, MinMaxSlope data, EStructuralFeature f) {
//		if(!Strings.isNullOrEmpty(value)) {
//			error(field +" must not be set", data, f);
//		}
//	}

}
