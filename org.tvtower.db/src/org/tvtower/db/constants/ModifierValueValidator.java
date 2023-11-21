package org.tvtower.db.constants;

import java.util.Optional;

import org.tvtower.db.database.Modifier;

public interface ModifierValueValidator {

	Optional<String> getValueError(Modifier m);
}
