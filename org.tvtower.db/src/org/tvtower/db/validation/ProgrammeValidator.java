package org.tvtower.db.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.Programme;
import org.tvtower.db.database.ProgrammeChildren;
import org.tvtower.db.database.ProgrammeData;
import org.tvtower.db.database.ProgrammeGroups;
import org.tvtower.db.database.ProgrammeRatings;
import org.tvtower.db.database.ProgrammeReleaseTime;
import org.tvtower.db.database.Programmes;
import org.tvtower.db.database.Staff;
import org.tvtower.db.database.StaffMember;

import com.google.common.base.Objects;

//TODO GroupAttractivitiy valieren (nach Umschreiben)
public class ProgrammeValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkProgramme(Programme p) {
		boolean isMainEntry = isMainEntry(p);
		boolean isRef = (p.getRef() != null);
		if (p.getTitle() == null && !isRef) {
			error("title must be defined", $.getProgramme_Name());
		}
		if (p.getDescription() == null && isMainEntry && !isRef) {
			error("description must be defined", $.getProgramme_Name());
		}
		if (p.getData() == null) {
			if (isMainEntry && !isRef) {
				error("data must be defined", $.getProgramme_Name());
			}
		} else {
			if (p.getData().getYear() != null && p.getReleaseTime() != null) {
				error("release time must be defined only once", $.getProgramme_ReleaseTime());
			}
			if (isMainEntry && !isRef) {
				CommonValidation.getValueMissingError("country", p.getData().getCountry())
						.ifPresent(e -> error(e, $.getProgramme_Data()));
				CommonValidation.getValueMissingError("maingenre", p.getData().getMaingenre())
						.ifPresent(e -> error(e, $.getProgramme_Data()));
				CommonValidation.getValueMissingError("distribution", p.getData().getDistribution())
						.ifPresent(e -> error(e, $.getProgramme_Data()));
			}
		}
		Constants._boolean.isValidValue(p.getFictional(), "fictional", false)
				.ifPresent(e -> error(e, $.getProgramme_Fictional()));
		Constants.licenceType.isValidValue(p.getLicenceType(), "licence_type", !isRef)
				.ifPresent(e -> error(e, $.getProgramme_LicenceType()));
		Constants.programmeType.isValidValue(p.getLicenceType(), "product", !isRef)
				.ifPresent(e -> error(e, $.getProgramme_Product()));

		if (p.getChildren() != null) {
			String expectedChildLicenceType = Constants.licenceType.getChildType(p.getLicenceType());
			ProgrammeChildren children = p.getChildren();
			for (int i = 0; i < children.getChild().size(); i++) {
				Programme child = children.getChild().get(i);
//mismatch of fictional flag in children possible
//				String parentFictional = p.getFictional();
				if (!expectedChildLicenceType.equals(child.getLicenceType())) {
					error("expect licence_type " + expectedChildLicenceType, child, $.getProgramme_LicenceType());
				}
//				if (parentFictional != null && child.getFictional() != null
//						&& !parentFictional.equals(child.getFictional())) {
//					error("fictional mismatch", child, $.getProgramme_Fictional());
//				}
				if (!Objects.equal(p.getProduct(), child.getProduct())) {
					error("product mismatch", child, $.getProgramme_Product());
				}
				if(child.getData()!=null && child.getData().getLicenceFlags() != null) {
					warning("be careful when defining child licence flags", child.getData(), $.getProgrammeData_LicenceFlags());
				}
			}
		}
	}

	@Check
	public void checkProgrammeData(ProgrammeData d) {
		CommonValidation.getCountryError(d.getCountry(), true).ifPresent(e -> error(e, $.getProgrammeData_Country()));
		CommonValidation.getIntRangeError(d.getYear(), "year", Constants.MIN_YEAR, Constants.MAX_YEAR, false)
				.ifPresent(e -> error(e, $.getProgrammeData_Year()));
		CommonValidation.getIntRangeError(d.getPrice(), "price", 0, 10000000, false)
				.ifPresent(e -> error(e, $.getProgrammeData_Price()));
		Constants.distribution.isValidValue(d.getDistribution(), "distribution", false)
				.ifPresent(e -> error(e, $.getProgrammeData_Distribution()));
		Constants.programmGenre.isValidValue(d.getMaingenre(), "maingenre", false)
				.ifPresent(e -> error(e, $.getProgrammeData_Maingenre()));
		Constants.programmGenre.isValidList(d.getSubgenre()).ifPresent(e -> error(e, $.getProgrammeData_Subgenre()));
		Constants.programmeFlag.isValidFlag(d.getFlags(), "flags", false)
				.ifPresent(e -> error(e, $.getProgrammeData_Flags()));
		Constants.licenceFlag.isValidFlag(d.getLicenceFlags(), "licence_flags", false)
				.ifPresent(e -> error(e, $.getProgrammeData_LicenceFlags()));
		CommonValidation.getIntRangeError(d.getBlocks(), "blocks", 1, 12, false)
				.ifPresent(e -> error(e, $.getProgrammeData_Blocks()));
		CommonValidation.getIntRangeError(d.getSlotStart(), "broadcast_time_slot_start", 0, 22, false)
				.ifPresent(e -> error(e, $.getProgrammeData_SlotStart()));
		CommonValidation.getIntRangeError(d.getSlotEnd(), "broadcast_time_slot_end", 1, 23, false)
				.ifPresent(e -> error(e, $.getProgrammeData_SlotEnd()));
		CommonValidation.getDecimalRangeError(d.getPriceMod(), "price_mod", BigDecimal.ZERO, BigDecimal.TEN, false)
				.ifPresent(e -> error(e, $.getProgrammeData_PriceMod()));
		CommonValidation.getIntRangeError(d.getBroadcastLimit(), "broadcast_limit", 1, 100, false)
				.ifPresent(e -> error(e, $.getProgrammeData_BroadcastLimit()));
		Constants.broadcastFlag.isValidFlag(d.getBroadcastFlags(), "broadcast_flags", false)
				.ifPresent(e -> error(e, $.getProgrammeData_BroadcastFlags()));
		CommonValidation.getIntRangeError(d.getLicenceBroadcastLimit(), "licence_broadcast_limit", 1, 100, false)
				.ifPresent(e -> error(e, $.getProgrammeData_LicenceBroadcastLimit()));
		Constants.broadcastFlag.isValidFlag(d.getLicenceBroadcastFlags(), "licence_broadcast_flags", false)
				.ifPresent(e -> error(e, $.getProgrammeData_LicenceBroadcastFlags()));
		ensureLimitHandling(d, d.getBroadcastLimit(), $.getProgrammeData_BroadcastLimit());
		ensureLimitHandling(d, d.getLicenceBroadcastLimit(), $.getProgrammeData_LicenceBroadcastLimit());
	}

	private void ensureLimitHandling(ProgrammeData d, String limit, EStructuralFeature limitFeature) {
		if (limit != null) {
			try {
				int l = Integer.parseInt(limit);
				if (l > 0) {
					List<String> applicableLicenceFlags = new ArrayList<>();
					applicableLicenceFlags.add(d.getLicenceFlags());
					Programme programme = ((Programme) d.eContainer());
					if (!isMainEntry(programme)) {
						Programme parent = (Programme) programme.eContainer().eContainer();
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

	@Check
	public void checkProgrammeRatings(ProgrammeRatings r) {
		CommonValidation.getIntRangeError(r.getCritics(), "critics", 0, 100, false)
				.ifPresent(e -> error(e, $.getProgrammeRatings_Critics()));
		CommonValidation.getIntRangeError(r.getSpeed(), "speed", 0, 100, false)
				.ifPresent(e -> error(e, $.getProgrammeRatings_Speed()));
		CommonValidation.getIntRangeError(r.getOutcome(), "outcome", 0, 100, false)
				.ifPresent(e -> error(e, $.getProgrammeRatings_Outcome()));
	}

	@Check
	public void checkProgrammeGroups(ProgrammeGroups g) {
		Constants.targetgroup.isValidFlag(g.getTargetGroup(), "target_groups", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_TargetGroup()));
		Constants.targetgroup.isValidFlag(g.getOptionalTargetGroup(), "target_groups_optional", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_OptionalTargetGroup()));
		Constants.pressuregroup.isValidFlag(g.getProPressureGroup(), "pro_pressure_groups", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_ProPressureGroup()));
		Constants.pressuregroup.isValidFlag(g.getContraPressureGroup(), "contra_pressure_groups", false)
				.ifPresent(e -> error(e, $.getProgrammeGroups_ContraPressureGroup()));
	}

	@Check
	public void checkProgrammeStaff(Staff staff) {
		int offset = getParentStaffCount(staff);
		if (offset > 0) {
			Set<Integer> indexes = new HashSet<>();
			int nextIndex = offset;
			EList<StaffMember> members = staff.getMember();
			for (int i = 0; i < members.size(); i++) {
				StaffMember member = members.get(i);
				try {
					int index = Integer.parseInt(member.getIndex());
					if (indexes.contains(index)) {
						error("duplicate index", member, $.getStaffMember_Index());
					} else {
						indexes.add(index);
						if (index < offset) {
							// overwrite existing job
						} else {
							if (index > (nextIndex)) {
								error("expected " + nextIndex, member, $.getStaffMember_Index());
							}
							nextIndex++;
						}
					}
				} catch (NumberFormatException e) {
					error("invalid index", member, $.getStaffMember_Index());
				}
			}
		} else {
			for (int i = 0; i < staff.getMember().size(); i++) {
				StaffMember member = staff.getMember().get(i);
				if (!("" + i).equals(member.getIndex())) {
					error("index " + i + " expected", member, $.getStaffMember_Index());
				}
			}
		}
	}

	private int getParentStaffCount(Staff staff) {
		EObject progContainer = staff.eContainer().eContainer();
		if (progContainer instanceof ProgrammeChildren) {
			Staff containerStaff = ((Programme) progContainer.eContainer()).getStaff();
			if (containerStaff != null) {
				return containerStaff.getMember().size();
			}
		}
		return 0;
	}

	@Check
	public void checkStaffMember(StaffMember m) {
		Constants.job.isValidFlag(m.getFunction(), "function", true)
				.ifPresent(e -> error(e, $.getStaffMember_Function()));
		Constants.job.isValidSingleFlag(m.getFunction(), "function", true)
				.ifPresent(e -> info(e, $.getStaffMember_Function()));
		// TODO validate generator?
	}

	@Check
	public void checkProgrammeReleaseTime(ProgrammeReleaseTime t) {
		CommonValidation.getIntRangeError(t.getYear(), "year", Constants.MIN_YEAR, Constants.MAX_YEAR, false)
				.ifPresent(e -> error(e, $.getProgrammeReleaseTime_Year()));
		CommonValidation.getIntRangeError(t.getDay(), "year", 1, 366, false)
				.ifPresent(e -> error(e, $.getProgrammeReleaseTime_Day()));
		CommonValidation.getIntRangeError(t.getDay(), "hour", 0, 236, false)
				.ifPresent(e -> error(e, $.getProgrammeReleaseTime_Hour()));

		CommonValidation.getIntRangeError(t.getYearRelative(), "year_relative", -100, 100, false)
				.ifPresent(e -> error(e, $.getProgrammeReleaseTime_YearRelative()));
		CommonValidation.getIntRangeError(t.getYearRelativeMin(), "year_relative_min", Constants.MIN_YEAR,
				Constants.MAX_YEAR, false).ifPresent(e -> error(e, $.getProgrammeReleaseTime_YearRelativeMin()));
		CommonValidation.getIntRangeError(t.getYearRelativeMax(), "year_relative_max", Constants.MIN_YEAR,
				Constants.MAX_YEAR, false).ifPresent(e -> error(e, $.getProgrammeReleaseTime_YearRelativeMax()));
		CommonValidation.getMinMaxError(t.getYearRelativeMin(), t.getYearRelativeMax())
				.ifPresent(e -> error(e, $.getProgrammeReleaseTime_YearRelativeMin()));

	}

	private boolean isMainEntry(Programme p) {
		return p.eContainer() instanceof Programmes;
	}
}
