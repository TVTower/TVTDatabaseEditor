package org.tvtower.db.validation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class CommonValidation {

	private static List<String> supportedCountries = ImmutableList.of("D", "DDR", "I", "USA", "CH", "CS", "J", "RU",
			"S", "A", "IND", "F", "DK", "SCO", "CDN", "GB", "HK", "BE", "CN", "PL", "NL", "RM", "BOL", "H", "AFG",
			"IRL", "IL", "ZA", "BM", "ROK", "AUS", "E");

	public static Optional<String> getBooleanError(String value, String fieldName, boolean mandatory) {
		if (mandatory && Strings.isNullOrEmpty(value)) {
			return Optional.of(fieldName + " is missing");
		}
		if (value != null && !("1".equals(value) || "0".equals(value))) {
			return Optional.of("boolean value must be 0 or 1");
		}
		return Optional.empty();
	}

	public static boolean isUserDB(EObject o) {
		return o.eResource().getURI().toString().contains("/user/");
	}

	public static Optional<String> getCountryError(String country, boolean multipleAllowed) {
		if (!Strings.isNullOrEmpty(country)) {
			if (country.indexOf(',') >= 0) {
				return Optional.of("Separator für mehrere Länder ist /");
			} else if (country.indexOf(' ') >= 0) {
				return Optional.of("Leerzeichen nicht erlaubt");
			}
			List<String> split = Splitter.on('/').trimResults().splitToList(country);
			if (!multipleAllowed && split.size() > 1) {
				return Optional.of("nur ein Land erlaubt");
			}
			for (String c : split) {
				if (!supportedCountries.contains(c)) {
					return Optional.of("unbekanntes Land " + c);
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

	public static Optional<String> getValueMissingError(String fieldName, String... values) {
		if(values==null || values.length==0) {
			throw new IllegalStateException("at least one value must be defined");
		}
		for (String v : values) {
			if(!Strings.isNullOrEmpty(v)) {
				return Optional.empty();
			}
		}
		return Optional.of(fieldName + " is missing");
	}
}
