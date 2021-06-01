package org.tvtower.db.constants;

import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.validation.DatabaseTime;

import com.google.common.collect.ImmutableMap;

public class Constants {

	public static final int MIN_YEAR = 1890;
	public static final int MAX_YEAR = 2200;

	public static ProgrammeGenre programmGenre = new ProgrammeGenre();
	public static ProgrammeType programmeType = new ProgrammeType();
	public static ProgrammeDataFlag programmeFlag = new ProgrammeDataFlag();
	public static LicenceType licenceType = new LicenceType();
	public static LicenceFlag licenceFlag = new LicenceFlag();
	public static ProgrammeDistribution distribution = new ProgrammeDistribution();

	public static GlobalVariable globalVariable = new GlobalVariable();
	public static RoleVariable roleVariable = new RoleVariable();

	public static JobFlag job = new JobFlag();
	public static Gender gender = new Gender();
	public static Country country = new Country();
	public static TrueFalse _boolean = new TrueFalse();

	public static TargetGroup targetgroup = new TargetGroup();
	public static PressureGroup pressuregroup = new PressureGroup();

	public static AchievementFlag achievementFlag = new AchievementFlag();
	public static AchievementCategory achievementCategory = new AchievementCategory();
	public static TaskType taskType = new TaskType();
	public static RewardType rewardType = new RewardType();

	public static NewsType newsType = new NewsType();
	public static NewsGenre newsGenre = new NewsGenre();
	public static NewsFlag newsFlag = new NewsFlag();
	public static TriggerType triggerType = new TriggerType();
	public static EffectType effectType = new EffectType();

	public static AdType adType = new AdType();

	public static ScriptFlag scriptFlag = new ScriptFlag();

	private static Map<EStructuralFeature, TVTHoverInfoCreator> hoverInfoCreators = createInfoMap();

	public static TVTHoverInfoCreator getHoverInfoCreator(EStructuralFeature f) {
		return hoverInfoCreators.get(f);
	}

	private static Map<EStructuralFeature, TVTHoverInfoCreator> createInfoMap() {
		DatabasePackage $ = DatabasePackage.eINSTANCE;
		DatabaseTime timeProvider=new DatabaseTime(null);
		return ImmutableMap.<EStructuralFeature, TVTHoverInfoCreator>builder()
				.put($.getJob_Function(), job)
				.put($.getJob_Required(), _boolean)
				.put($.getJob_Gender(), gender)

				.put($.getScriptTemplate_Product(), programmeType)
				.put($.getScriptTemplate_LicenceType(), licenceType)
				.put($.getScriptData_ProgrammeFlags(), programmeFlag)
				.put($.getScriptData_OptionalProgrammeFlags(), programmeFlag)
				.put($.getScriptData_LicenceFlags(), licenceFlag)
				.put($.getScriptData_ScriptFlags(), scriptFlag)
				.put($.getScriptData_Live_date(), timeProvider)
				//TODO broadcast flags!!
//				.put($.getScriptData_BroadcastFlags())
				.put($.getScriptGenres_MainGenre(), programmGenre)
				.put($.getScriptGenres_Subgenres(), programmGenre)

				.put($.getProgrammeRole_Country(), country)
				.put($.getProgrammeRole_Gender(), gender)

				.put($.getAdConditions_AllowedGenre(), programmGenre)
				.put($.getAdConditions_ProhibitedGenre(), programmGenre)
				.put($.getAdConditions_AllowedProgrammeType(), programmeType)
				.put($.getAdConditions_ProhibitedProgrammeType(), programmeType)
				.put($.getAdConditions_AllowedProgrammeFlag(), programmeFlag)
				.put($.getAdConditions_ProhibitedProgrammeFlag(), programmeFlag)
				.put($.getAdConditions_TargetGroup(), targetgroup)
				.put($.getAdConditions_ProPressureGroup(), pressuregroup)
				.put($.getAdConditions_ContraPressureGroup(), pressuregroup)

				.put($.getAdvertisementData_Available(), _boolean)
				.put($.getAdvertisementData_Type(), adType)
				.put($.getAdvertisementData_FixPrice(), _boolean)
				.put($.getAdvertisementData_FixInfomercialProfit(), _boolean)
				.put($.getAdvertisementData_ProPressureGroup(), pressuregroup)
				.put($.getAdvertisementData_ContraPressureGroup(), pressuregroup)

				.put($.getNewsItem_Type(), newsType)
				.put($.getNewsData_Genre(), newsGenre)
				.put($.getNewsData_Flags(), newsFlag)
				.put($.getNewsData_Fictional(), _boolean)
				.put($.getNewsData_Available(), _boolean)
				.put($.getNewsData_HappenTime(), timeProvider)

				.put($.getEffect_Trigger(), triggerType)
				.put($.getEffect_Type(), effectType)
				.put($.getEffect_Genre(), programmGenre)
				.put($.getEffect_Time(), timeProvider)
				.put($.getEffect_Enable(), _boolean)

				.put($.getPerson_Bookable(), _boolean)
				.put($.getPerson_Fictional(), _boolean)
				.put($.getPerson_LevelUp(), _boolean)
				.put($.getPerson_Gender(), gender)
				.put($.getPerson_Country(), country)
				.put($.getPerson_Job(), job)
				.put($.getPersonDetails_Country(), country)
				.put($.getPersonDetails_Gender(), gender)
				.put($.getPersonDetails_Fictional(), _boolean)
				.put($.getPersonDetails_Job(), job)
				.put($.getPersonData_TopGenre(), programmGenre)

				.put($.getProgramme_Product(), programmeType)
				.put($.getProgramme_LicenceType(), licenceType)
				.put($.getProgramme_Fictional(), _boolean)
				.put($.getProgrammeData_Country(), country)
				.put($.getProgrammeData_Distribution(), distribution)
				.put($.getProgrammeData_Maingenre(), programmGenre)
				.put($.getProgrammeData_Subgenre(), programmGenre)
				.put($.getProgrammeData_Flags(), programmeFlag)
				.put($.getProgrammeData_LicenceFlags(), licenceFlag)

				.put($.getProgrammeGroups_TargetGroup(), targetgroup)
				.put($.getProgrammeGroups_OptionalTargetGroup(), targetgroup)
				.put($.getProgrammeGroups_ProPressureGroup(), pressuregroup)
				.put($.getProgrammeGroups_ContraPressureGroup(), pressuregroup)

				.put($.getStaffMember_Function(), job)

				.put($.getTaskData_Type(), taskType)
				.put($.getTaskData_Genre1(), newsGenre)
				.put($.getTaskData_Genre2(), newsGenre)
				.put($.getTaskData_Genre3(), newsGenre)
				.put($.getRewardData_Type(), rewardType)
				.put($.getAchievementData_Flags(), achievementFlag).build();
	}
}