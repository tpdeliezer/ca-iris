/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ss105;

import java.io.IOException;
import java.util.Date;
import us.mn.state.dot.tms.server.comm.ParsingException;

/**
 * Binned Sample Property
 *
 * @author Douglas Lau
 */
public class BinnedSampleProperty extends SS105Property {

	/** Sample age (number of intervals old) */
	protected final int age;

	/** Timestamp at end of sample interval */
	Date timestamp = null;

	/** Sample data for each lane */
	protected LaneSample[] samples = new LaneSample[0];

	/** Create a new binned sample property */
	public BinnedSampleProperty() {
		this(0);
	}

	/** Create a new binned sample property */
	public BinnedSampleProperty(int a) {
		age = a;
	}

	/** Check if the property has a checksum */
	protected boolean hasChecksum() {
		return true;
	}

	/** Format a basic "GET" request */
	protected String formatGetRequest() {
		if(age < 1)
			return "XD";
		else
			return "XD" + hex(age, 4);
	}

	/** Format a basic "SET" request */
	protected String formatSetRequest() {
		return null;
	}

	/** Maximum percentage value of lane sample data */
	static public final int MAX_PERCENT = 1024;

	/** Number of bytes per lane of sample data */
	static protected final int LANE_SAMPLE_BYTES = 29;

	/** Sample data for one lane */
	static public class LaneSample {

		public final int det;
		public final int volume;
		public final int speed;		// Miles per Hour
		public final int scans;		// 0-1024 (percentage)
		public final int small;		// 0-1024 (percentage)
		public final int medium;	// 0-1024 (percentage)
		public final int large;		// 0-1024 (percentage)

		protected LaneSample(String s) throws ParsingException {
			det = parseInt(s.substring(0, 1));
			volume = parseInt(s.substring(1, 9));
			speed = parseInt(s.substring(9, 13));
			scans = parseInt(s.substring(13, 17));
			small = parseInt(s.substring(17, 21));
			medium = parseInt(s.substring(21, 25));
			large = parseInt(s.substring(25, 29));
		}
		static int parseInt(String s) throws ParsingException {
			try {
				return Integer.parseInt(s, 16);
			}
			catch(NumberFormatException e) {
				throw new ParsingException("INVALID SAMPLE");
			}
		}
		public int getScans() {
			return scans;
		}
		static float percent(int i) {
			return 100 * i / (float)MAX_PERCENT;
		}
		public String toString() {
			return det + ": " + volume + ", " + speed + ", " +
				percent(scans) + ", " + small + ", " +
				medium + ", " + large;
		}
	}

	/** Parse the response to a QUERY */
	protected void parseQuery(String r) throws IOException {
		timestamp = TimeStamp.parse(r.substring(0, 8));
		String payload = r.substring(8);
		if(payload.length() % LANE_SAMPLE_BYTES != 0)
			throw new ParsingException("INVALID SAMPLE SIZE");
		int lanes = payload.length() / LANE_SAMPLE_BYTES;
		samples = new LaneSample[lanes];
		for(int i = 0, j = 0; i < lanes; i++) {
			samples[i] = new LaneSample(payload.substring(
				j, j + LANE_SAMPLE_BYTES));
			j += LANE_SAMPLE_BYTES;
		}
	}

	/** Get a string representation of the sample data */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("XD: ");
		sb.append(timestamp.toString());
		for(LaneSample ls: samples) {
			sb.append('\n');
			sb.append(ls.toString());
		}
		return sb.toString();
	}

	/** Get the highest detector sample number */
	protected int maxDetNumber() {
		int dets = 0;
		for(LaneSample ls: samples)
			dets = Math.max(dets, ls.det);
		return dets;
	}

	/** Get the volume array */
	public int[] getVolume() {
		int[] volume = new int[maxDetNumber()];
		for(LaneSample ls: samples)
			volume[ls.det - 1] = ls.volume;
		return volume;
	}

	/** Get the scan count array */
	public int[] getScans() {
		int[] scans = new int[maxDetNumber()];
		for(LaneSample ls: samples)
			scans[ls.det - 1] = ls.getScans();
		return scans;
	}

	/** Get the speed array */
	public int[] getSpeed() {
		int[] speed = new int[maxDetNumber()];
		for(LaneSample ls: samples)
			speed[ls.det - 1] = ls.speed;
		return speed;
	}
}
