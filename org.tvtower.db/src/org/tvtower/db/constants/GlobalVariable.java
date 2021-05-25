package org.tvtower.db.constants;

public class GlobalVariable extends TVTEnum {

	GlobalVariable() {
		add("WORLDTIME:YEAR", "current year");
		add("WORLDTIME:GAMEDAY", "current day (number)");
		add("WORLDTIME:DAYLONG", "day of the week");
		add("WORLDTIME:GERMANCURRENCY", "current German currency");
		add("WORLDTIME:GERMANCAPITAL", "current German capital");

		add("STATIONMAP:COUNTRYNAME", "country");
		add("STATIONMAP:POPULATION", "population");
		add("STATIONMAP:RANDOMCITY", "generated city name");
	}
}
