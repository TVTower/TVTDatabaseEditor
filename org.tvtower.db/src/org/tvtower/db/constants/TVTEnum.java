package org.tvtower.db.constants;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public abstract class TVTEnum implements TVTHoverInfoCreator {

	private LinkedHashMap<String, String> items = new LinkedHashMap<>();

	void add(String key, String value) {
		if (items.containsKey(key)) {
			throw new IllegalStateException();
		} else {
			items.put(key, value);
		}
	}

	public Optional<String> isValidValue(String value, String fieldName, boolean mandatory) {
		if (mandatory && Strings.isNullOrEmpty(value)) {
			return Optional.of(fieldName + " is missing");
		}
		if (!Strings.isNullOrEmpty(value) && !items.containsKey(value)) {
			return Optional.of("invalid value " + value);
		}
		return Optional.empty();
	}

	public Optional<String> isValidList(String value) {
		if (!Strings.isNullOrEmpty(value)) {
			String[] elements = value.split(",", -1);
			for (String e : elements) {
				if (Strings.isNullOrEmpty(e)) {
					return Optional.of("empty list element");
				}
				Optional<String> error = isValidValue(e, "", false);
				if (error.isPresent()) {
					return error;
				}
			}
		}
		return Optional.empty();
	}

	public Map<String, String> forContentAssist() {
		return Maps.newLinkedHashMap(items);
	}

	@Override
	public String createHoverInfo(Object value) {
		if (value != null) {
			String asString = value.toString();
			if (asString.indexOf(',') >= 0) {
				List<String> translated = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(asString)
						.stream().map(s -> items.get(s)).filter(v -> v != null).collect(Collectors.toList());
				return Joiner.on(", ").join(translated);
			} else {
				return items.get(asString);
			}
		}
		return null;
	}
}
