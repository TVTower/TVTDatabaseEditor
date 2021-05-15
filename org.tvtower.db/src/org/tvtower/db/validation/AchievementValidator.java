package org.tvtower.db.validation;

import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
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
		if (a.getTitle() == null ||a.getTitle().getLanguageString().isEmpty()) {
			error("title must be defined", $.getAchievement_Id());
		}
		if (a.getData() == null) {
			error("data must be defined", $.getAdvertisement_Id());
		}
	}

	@Check
	public void checkAchievementData(AchievementData d) {
		// TODO
	}

	@Check
	public void checkTask(Task t) {
		if(t.getTitle()==null ||t.getTitle().getLanguageString().isEmpty()) {
			error("title must be defined", $.getTask_Id());
		}
		if(t.getText()==null ||t.getText().getTexts().isEmpty()) {
			error("text must be defined", $.getTask_Id());
		}
	}

	@Check
	public void checkTaskData(TaskData c) {
	}

	@Check
	public void checkReward(Reward c) {
	}

	@Check
	public void checkRewardData(RewardData c) {
	}

}
