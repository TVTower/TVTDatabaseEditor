package org.tvtower.db.constants;

public class ProgrammeDataFlag extends TVTFlag {

	public final long LIVE;
	public final long PAID;
	public final long SERIES;

	ProgrammeDataFlag() {
		add("none");
		LIVE=add("live");
		add("animation");
		add("culture");
		add("cult");
		add("trash");
		add("b-movie");
		add("x-rated");

		PAID=add("paid");
		SERIES=add("series");
		add("scripted");
		add("customproduction");
		add("invisible");
		add("live on tape");
	}

//	@Override
//	protected long getObsoleteOrUnsupportedFlags() {
//		return LIVE;
//	}
}
