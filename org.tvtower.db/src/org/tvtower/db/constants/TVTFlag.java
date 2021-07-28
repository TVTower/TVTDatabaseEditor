package org.tvtower.db.constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public abstract class TVTFlag implements TVTHoverInfoCreator {

	private int count = 0;
	private long MAX_FLAG;

	private LinkedHashMap<Long, String> items = new LinkedHashMap<>();

	long add(String value) {
		long key = count == 0 ? 0 : (1 << (count - 1));
		count++;
		if (items.containsKey(key)) {
			throw new IllegalStateException();
		} else {
			items.put(key, value);
		}
		MAX_FLAG = 2 * key;
		return key;
	}

	public final Optional<String> isValidSingleFlag(String value, String fieldName, boolean mandatory) {
		if (mandatory && Strings.isNullOrEmpty(value)) {
			return Optional.of(fieldName + " is missing");
		}
		try {
			if (!Strings.isNullOrEmpty(value) && !items.containsKey(Long.parseLong(value))) {
				return Optional.of(value + " is not a valid single flag");
			}
		} catch (NumberFormatException e) {
			return Optional.of(value + " is not a number");
		}
		return Optional.empty();
	}

	public final Optional<String> isValidFlag(String value, String fieldName, boolean mandatory) {
		if (mandatory && Strings.isNullOrEmpty(value)) {
			return Optional.of(fieldName + " is missing");
		}
		try {
			if (!Strings.isNullOrEmpty(value)) {
				long asNumber = Long.parseLong(value);
				if (asNumber < 0 || asNumber >= MAX_FLAG) {
					return Optional.of("invalid value " + value);
				}
			}
		} catch (NumberFormatException e) {
			return Optional.of("no flag value" + value);
		}
		return Optional.empty();
	}

	public Map<Long, String> forContentAssist() {
		return Maps.newLinkedHashMap(items);
	}

	public final boolean hasFlag(String value, long flag) {
		if (value != null) {
			try {
				return (Long.parseLong(value) & flag) > 0;
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		return false;
	}

	@Override
	public String createHoverInfo(Object value) {
		if (value != null) {
			if ("0".equals(value.toString())) {
				return items.get(0L);
			} else {
				try {
					long asLong = Long.parseLong(value.toString());
					List<String> result = new ArrayList<>();
					for (Entry<Long, String> entry : items.entrySet()) {
						if ((asLong & entry.getKey().intValue()) > 0) {
							result.add(entry.getValue());
						}
					}
					if (!result.isEmpty()) {
						return Joiner.on(", ").join(result);
					}
				} catch (Exception e) {
					// ignore
				}
			}
		}
		return null;
	}
}
