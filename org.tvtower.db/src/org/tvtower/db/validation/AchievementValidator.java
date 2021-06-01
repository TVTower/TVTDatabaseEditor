package org.tvtower.db.validation;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import org.tvtower.db.constants.Constants;
import org.tvtower.db.constants.RewardType;
import org.tvtower.db.constants.TaskType;
import org.tvtower.db.database.Achievement;
import org.tvtower.db.database.AchievementData;
import org.tvtower.db.database.DatabasePackage;
import org.tvtower.db.database.Reward;
import org.tvtower.db.database.RewardData;
import org.tvtower.db.database.Task;
import org.tvtower.db.database.TaskData;

public class AchievementValidator extends AbstractDatabaseValidator {

	private static DatabasePackage $ = DatabasePackage.eINSTANCE;

	@Override
	public void register(EValidatorRegistrar registrar) {
	}

	@Check
	public void checkAchievement(Achievement a) {
		if (a.getTitle() == null || a.getTitle().getLstrings().isEmpty()) {
			error("title must be defined", $.getAchievement_Id());
		}
		if (a.getData() == null) {
			error("data must be defined", $.getAdvertisement_Id());
		}
	}

	@Check
	public void checkAchievementData(AchievementData d) {
		Constants.achievementFlag.isValidFlag(d.getFlags(), "flags", false)
				.ifPresent(e -> error(e, $.getAchievementData_Flags()));
		CommonValidation.getIntRangeError(d.getGroup(), "group", 0, 256, false)
				.ifPresent(e -> error(e, $.getAchievementData_Group()));
		CommonValidation.getIntRangeError(d.getIndex(), "index", 0, 32, false)
				.ifPresent(e -> error(e, $.getAchievementData_Index()));
		Constants.achievementCategory.isValidFlag(d.getCategory(), "category", true)
				.ifPresent(e -> error(e, $.getAchievementData_Category()));
		// TODO sprite_finished, sprite_unfinished from XML?
	}

	@Check
	public void checkTask(Task t) {
		if (t.getTitle() == null || t.getTitle().getLstrings().isEmpty()) {
			error("title must be defined", $.getTask_Id());
		}
		if (t.getText() == null || t.getText().getLstrings().isEmpty()) {
			error("text must be defined", $.getTask_Id());
		}
	}

	@Check
	public void checkTaskData(TaskData d) {
		TaskType t = Constants.taskType;
		t.isValidValue(d.getType(), "type", true).ifPresent(e -> error(e, $.getTaskData_Type()));
		t.getMinAudienceAbsError(d).ifPresent(e -> error(e, $.getTaskData_MinAudienceAbs()));
		t.getMinAudiencePercentError(d).ifPresent(e -> error(e, $.getTaskData_MinAudiencePercent()));
		t.getCheckMinuteError(d).ifPresent(e -> error(e, $.getTaskData_CheckMinute()));
		t.getCheckHourError(d).ifPresent(e -> error(e, $.getTaskData_CheckHour()));

		t.getMinReachAbsError(d).ifPresent(e -> error(e, $.getTaskData_MinAudienceAbs()));
		t.getMinReachPercentError(d).ifPresent(e -> error(e, $.getTaskData_MinAudiencePercent()));

		t.getKeyword1Error(d).ifPresent(e -> error(e, $.getTaskData_Keyword1()));
		t.getKeyword2Error(d).ifPresent(e -> error(e, $.getTaskData_Keyword2()));
		t.getKeyword3Error(d).ifPresent(e -> error(e, $.getTaskData_Keyword3()));
		t.getGenre1Error(d).ifPresent(e -> error(e, $.getTaskData_Genre1()));
		t.getGenre2Error(d).ifPresent(e -> error(e, $.getTaskData_Genre2()));
		t.getGenre3Error(d).ifPresent(e -> error(e, $.getTaskData_Genre3()));
		t.getQualityError(d, d.getMinQuality1(), "minQuality1").ifPresent(e -> error(e, $.getTaskData_MinQuality1()));
		t.getQualityError(d, d.getMinQuality2(), "minQuality2").ifPresent(e -> error(e, $.getTaskData_MinQuality2()));
		t.getQualityError(d, d.getMinQuality3(), "minQuality3").ifPresent(e -> error(e, $.getTaskData_MinQuality3()));
		t.getQualityError(d, d.getMaxQuality1(), "maxQuality1").ifPresent(e -> error(e, $.getTaskData_MaxQuality1()));
		t.getQualityError(d, d.getMaxQuality2(), "maxQuality2").ifPresent(e -> error(e, $.getTaskData_MaxQuality2()));
		t.getQualityError(d, d.getMaxQuality3(), "maxQuality3").ifPresent(e -> error(e, $.getTaskData_MaxQuality3()));
		minMaxError(d.getMinQuality1(), d.getMaxQuality1(), d, $.getTaskData_MinQuality1());
		minMaxError(d.getMinQuality2(), d.getMaxQuality2(), d, $.getTaskData_MinQuality2());
		minMaxError(d.getMinQuality3(), d.getMaxQuality3(), d, $.getTaskData_MinQuality3());
	}

	private void minMaxError(String min, String max, TaskData d, EStructuralFeature feature) {
		CommonValidation.getMinMaxError(min, max).ifPresent(e -> error(e, d, feature));
	}

	// TODO wenn nur ID gegeben, prüfen, dass ID an anderer Stelle vollständig
	// definiert ist
	@Check
	public void checkReward(Reward r) {
	}

	@Check
	public void checkRewardData(RewardData d) {
		RewardType t = Constants.rewardType;
		t.isValidValue(d.getType(), "type", true).ifPresent(e -> error(e, $.getRewardData_Type()));
		t.getMoneyError(d).ifPresent(e -> error(e, $.getRewardData_Money()));
	}

}
