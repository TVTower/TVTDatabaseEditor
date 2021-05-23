package org.tvtower.db.constants;

import java.util.Optional;

import org.tvtower.db.database.RewardData;
import org.tvtower.db.validation.CommonValidation;

public class RewardType extends TVTEnum {

	RewardType() {
		add("money", "money");
	}

	public Optional<String> getMoneyError(RewardData d) {
		if("money".equalsIgnoreCase(d.getType())) {
			return CommonValidation.getIntRangeError(d.getMoney(), "money", 0, Integer.MAX_VALUE, true);
		}else {
			return Optional.of("money not allowed for this type");
		}
	}

}
