package org.tvtower.db.validation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.tvtower.db.constants.Constants;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public class CommonValidation {

	public static boolean isUserDB(EObject o) {
		return o.eResource().getURI().toString().contains("/user/");
	}

	public static Optional<String> getCountryError(String country, boolean multipleAllowed) {
		if (!Strings.isNullOrEmpty(country)) {
			if (country.indexOf(',') >= 0) {
				return Optional.of("separator is /");
			} else if (country.indexOf(' ') >= 0) {
				return Optional.of("no spaces allowed");
			}
			List<String> split = Splitter.on('/').trimResults().splitToList(country);
			if (!multipleAllowed && split.size() > 1) {
				return Optional.of("only one country allowed");
			}
			for (String c : split) {
				if (Constants.country.isValidValue(c, "", false).isPresent()) {
					return Optional.of("unknown country code " + c);
				}
			}
		}
		return Optional.empty();
	}

	public static Optional<String> getIntRangeError(String value, String fieldName, int min, int max,
			boolean mandatory) {
		if (mandatory && Strings.isNullOrEmpty(value)) {
			return Optional.of(fieldName + " is missing");
		}
		if (!Strings.isNullOrEmpty(value)) {
			try {
				int asNumber = Integer.parseInt(value);
				if (asNumber < min) {
					return Optional.of(value + " is smaller than " + min);
				} else if (asNumber > max) {
					return Optional.of(value + " bigger than " + max);
				}
			} catch (NumberFormatException e) {
				return Optional.of(value + " is not a valid number");
			}
		}
		return Optional.empty();
	}

	public static Optional<String> getDecimalRangeError(String value, String fieldName, BigDecimal min, BigDecimal max,
			boolean mandatory) {
		if (mandatory && Strings.isNullOrEmpty(value)) {
			return Optional.of(fieldName + " is missing");
		}
		if (!Strings.isNullOrEmpty(value)) {
			try {
				BigDecimal asNumber = new BigDecimal(value);
				if (asNumber.compareTo(min) < 0) {
					return Optional.of(value + " is smaller than " + min);
				} else if (asNumber.compareTo(max) > 0) {
					return Optional.of(value + " bigger than " + max);
				}
			} catch (NumberFormatException e) {
				return Optional.of(value + " is not a valid number");
			}
		}
		return Optional.empty();
	}

	public static Optional<String> getMinMaxError(String min, String max) {
		if (!Strings.isNullOrEmpty(min) && !Strings.isNullOrEmpty(max)) {
			try {
				if (new BigDecimal(min).compareTo(new BigDecimal(max)) > 0) {
					return Optional.of("min-value is greater than max-value");
				}
			} catch (NumberFormatException e) {
				// ignore - handled elsewhere
			}
		}
		return Optional.empty();
	}

	public static Optional<String> getValueMissingError(String fieldName, String... values) {
		if (values == null || values.length == 0) {
			throw new IllegalStateException("at least one value must be defined");
		}
		for (String v : values) {
			if (!Strings.isNullOrEmpty(v)) {
				return Optional.empty();
			}
		}
		return Optional.of(fieldName + " is missing");
	}

	public static Optional<String> getTimeError(String value, String fieldName) {
		// TODO time formats
		return Optional.empty();
	}
}
