/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2012  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms;

/**
 * Time interval values.  All values are in seconds.
 *
 * @author Douglas Lau
 */
public final class Interval {

	/** Number of seconds in a minute */
	static public final int MINUTE = 60;

	/** Number of seconds in an hour */
	static public final int HOUR = 60 * MINUTE;

	/** Number of seconds in a day */
	static public final int DAY = 24 * HOUR;

	/** Number of milliseconds in interval */
	private final long millis;

	/** Number of seconds in interval */
	public final int seconds() {
		return (int)(millis / 1000);
	}

	/** Create a new interval */
	private Interval(int s) {
		millis = s * 1000;
	}

	/** Create a new interval */
	private Interval(float s) {
		millis = Math.round(s * 1000d);
	}

	/** Get the number of intervals per hour */
	public int per_hour() {
		return Math.round(HOUR * 1000f / millis);
	}

	/** Create an interval of a number of hours */
	static public Interval hour(int h) {
		return new Interval(h * HOUR);
	}

	/** Create an interval of a number of minutes */
	static public Interval minute(int m) {
		return new Interval(m * MINUTE);
	}

	/** Create an interval of a number of seconds */
	static public Interval second(int s) {
		return new Interval(s);
	}

	/** Create an interval of a number of seconds */
	static public Interval second(float s) {
		return new Interval(s);
	}
}
