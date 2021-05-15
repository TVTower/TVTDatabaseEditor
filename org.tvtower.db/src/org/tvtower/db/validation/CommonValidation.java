package org.tvtower.db.validation;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class CommonValidation {

	private static List<String> supportedCountries=ImmutableList.of("D", "DDR","I","USA","CH","CS","J","RU","S","A", "IND","F","DK","SCO","CDN","GB","HK","BE","CN","PL","NL","RM","BOL","H","AFG","IRL","IL","ZA","BM","ROK","AUS","E");

	public static Optional<String> getBooleanError(String value, boolean mandatory) {
		return Optional.empty();
	}

	public static boolean isUserDB(EObject o) {
		return o.eResource().getURI().toString().contains("/user/");
	}

	public static Optional<String> getCountryError(String country, boolean multipleAllowed){
		if(!Strings.isNullOrEmpty(country)) {
			if(country.indexOf(',')>=0) {
				return Optional.of("Separator für mehrere Länder ist /");
			}else if(country.indexOf(' ')>=0) {
				return Optional.of("Leerzeichen nicht erlaubt");
			}
			List<String> split = Splitter.on('/').trimResults().splitToList(country);
			if(!multipleAllowed && split.size()>1) {
				return Optional.of("nur ein Land erlaubt");
			}
			for (String c : split) {
				if(!supportedCountries.contains(c)) {
					return Optional.of("unbekanntes Land "+c);
				}
			}
		}
		return Optional.empty();
	}
}
