/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2008  Minnesota Department of Transportation
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

import java.sql.ResultSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * The RoadImpl class represents a single road which can be used to logically
 * group traffic management devices.
 *
 * @author Douglas Lau
 */
public class RoadImpl extends BaseObjectImpl implements Road {

	/** Abbreviation regex pattern */
	static protected final Pattern ABBREV_PATTERN =
		Pattern.compile("[A-Za-z0-9]{0,6}");

	/** Load all the roads */
	static protected void loadAll() throws TMSException {
		System.err.println("Loading roads...");
		namespace.registerType(SONAR_TYPE, RoadImpl.class);
		store.query("SELECT name, abbrev, r_class, direction, alt_dir" +
			" FROM road;", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.add(new RoadImpl(
					row.getString(1),	// name
					row.getString(2),	// abbrev 
					row.getShort(3),	// r_class
					row.getShort(4),	// direction
					row.getShort(5)		// alt_dir
				));
			}
		});
	}

	/** Store a video monitor */
	public void doStore() throws TMSException {
		store.update("INSERT INTO " + getTable() +
			" (name, abbrev, r_class, direction, alt_dir) VALUES " +
			"('" + name + "', '" + abbrev + "', '" + r_class +
			"', '" + direction + "', '" + alt_dir + "');");
	}

	/** Get the database table name */
	public String getTable() {
		return SONAR_TYPE;
	}

	/** Get the SONAR type name */
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new road */
	public RoadImpl(String n) {
		super(n);
		abbrev = "";
	}

	/** Create a new road */
	protected RoadImpl(String n, String a, short c, short d, short ad) {
		super(n);
		abbrev = a;
		r_class = c;
		direction = d;
		alt_dir = ad;
	}

	/** Test if another road starts with the same name */
	public boolean matchRootName(RoadImpl other) {
		return name.startsWith(other.name) ||
			other.name.startsWith(name);
	}

	/** Abbreviated name (for detector and station names) */
	protected String abbrev;

	/** Set the abbreviated name */
	public void setAbbrev(String a) {
		abbrev = a;
	}

	/** Set the abbreviated name */
	public void doSetAbbrev(String a) throws TMSException {
		if(a.equals(abbrev))
			return;
		Matcher m = ABBREV_PATTERN.matcher(a);
		if(!m.matches())
			throw new ChangeVetoException("Invalid abbrev: " + a);
		store.update(this, "abbrev", a);
		setAbbrev(a);
	}

	/** Get the abbreviated name */
	public String getAbbrev() {
		return abbrev;
	}

	/** Road class */
	protected short r_class;

	/** Set the road class */
	public void setRClass(short c) {
		r_class = c;
	}

	/** Set the road class */
	public void doSetRClass(short c) throws TMSException {
		if(c == r_class)
			return;
		store.update(this, "r_class", c);
		setRClass(c);
	}

	/** Get the road class */
	public short getRClass() {
		return r_class;
	}

	/** Check if the road is a freeway */
	public boolean isFreeway() {
		return r_class == FREEWAY || r_class == CD_ROAD;
	}

	/** Direction (NORTH_SOUTH or EAST_WEST) */
	protected short direction;

	/** Set the direction */
	public void setDirection(short d) {
		direction = d;
	}

	/** Set the direction */
	public void doSetDirection(short d) throws TMSException {
		if(d == direction)
			return;
		if(d != NONE && d != NORTH_SOUTH && d != EAST_WEST)
			throw new ChangeVetoException("Invalid direction");
		store.update(this, "direction", d);
		setDirection(d);
	}

	/** Get the direction */
	public short getDirection() {
		return direction;
	}

	/** Alternate direction (NORTH, SOUTH, EAST, or WEST) */
	protected short alt_dir;

	/** Set the alternate direction */
	public void setAltDir(short ad) {
		alt_dir = ad;
	}

	/** Set the alternate direction */
	public void doSetAltDir(short ad) throws TMSException {
		if(ad == alt_dir)
			return;
		if(ad != NONE && ad != NORTH && ad != SOUTH && ad != EAST &&
		   ad != WEST)
			throw new ChangeVetoException("Invalid direction");
		store.update(this, "alt_dir", ad);
		setAltDir(ad);
	}

	/** Get the alternate direction */
	public short getAltDir() {
		return alt_dir;
	}

	/** Filter for alternate directions on a North-South road */
	protected short filterNorthSouth(short d) {
		if(alt_dir == EAST) {
			if(d == EAST)
				return NORTH;
			if(d == WEST)
				return SOUTH;
		} else if(alt_dir == WEST) {
			if(d == WEST)
				return NORTH;
			if(d == EAST)
				return SOUTH;
		}
		return d;
	}

	/** Filter for alternate directions on an East-West road */
	protected short filterEastWest(short d) {
		if(alt_dir == NORTH) {
			if(d == NORTH)
				return EAST;
			if(d == SOUTH)
				return WEST;
		} else if(alt_dir == SOUTH) {
			if(d == SOUTH)
				return EAST;
			if(d == NORTH)
				return WEST;
		}
		return d;
	}

	/** Filter the freeway direction which matches the given direction */
	public short filterDirection(short d) {
		if(direction == NORTH_SOUTH)
			return filterNorthSouth(d);
		else if(direction == EAST_WEST)
			return filterEastWest(d);
		else
			return d;
	}
}
