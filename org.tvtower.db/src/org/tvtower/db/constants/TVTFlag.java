package org.tvtower.db.constants;

import java.util.LinkedHashMap;
import java.util.Optional;

import com.google.common.base.Strings;

abstract class TVTFlag {

	private int count=0;
	private long MAX_FLAG;

	private LinkedHashMap<Long, String> items=new LinkedHashMap<>();

	long add(String value) {
		long key=count==0?0:(1<<(count-1));
		count++;
		if(items.containsKey(key)) {
			throw new IllegalStateException();
		}else {
			items.put(key, value);
		}
		MAX_FLAG=2*key;
		return key;
	}

	public final Optional<String> isValidSingleFlag(String value, String fieldName, boolean mandatory){
		if(mandatory && Strings.isNullOrEmpty(value)) {
			return Optional.of(fieldName +" is missing");
		}
		try {
			if(!Strings.isNullOrEmpty(value) && !items.containsKey(Long.parseLong(value))) {
				return Optional.of("invalid value " +value);
			}
		}catch(NumberFormatException e) {
			return Optional.of("no flag value" + value);
		}
		return Optional.empty();
	}

	public final Optional<String> isValidFlag(String value, String fieldName, boolean mandatory){
		if(mandatory && Strings.isNullOrEmpty(value)) {
			return Optional.of(fieldName +" is missing");
		}
		try {
			if(!Strings.isNullOrEmpty(value)) {
				long asNumber = Long.parseLong(value);
				if(asNumber<0 || asNumber>=MAX_FLAG) {
					return Optional.of("invalid value " +value);
				}
			}
		}catch(NumberFormatException e) {
			return Optional.of("no flag value" + value);
		}
		return Optional.empty();
	}
}
