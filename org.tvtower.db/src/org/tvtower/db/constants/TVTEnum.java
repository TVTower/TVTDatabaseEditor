package org.tvtower.db.constants;

import java.util.LinkedHashMap;
import java.util.Optional;

import com.google.common.base.Strings;

abstract class TVTEnum {

	private LinkedHashMap<String, String> items=new LinkedHashMap<>();

	void add(String key, String value) {
		if(items.containsKey(key)) {
			throw new IllegalStateException();
		}else {
			items.put(key, value);
		}
	}

	public Optional<String> isValidValue(String value, String fieldName, boolean mandatory){
		if(mandatory && Strings.isNullOrEmpty(value)) {
			return Optional.of(fieldName +" is missing");
		}
		if(!Strings.isNullOrEmpty(value) && !items.containsKey(value)) {
			return Optional.of("invalid value " +value);
		}
		return Optional.empty();
	}

	public Optional<String> isValidList(String value){
		if(!Strings.isNullOrEmpty(value)) {
			String[] elements = value.split(",",-1);
			for (String e : elements) {
				if(Strings.isNullOrEmpty(e)) {
					return Optional.of("empty list element");
				}
				Optional<String> error = isValidValue(e, "", false);
				if(error.isPresent()) {
					return error;
				}
			}
		}
		return Optional.empty();
	}
}
