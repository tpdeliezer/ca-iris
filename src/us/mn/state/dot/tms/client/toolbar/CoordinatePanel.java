/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.toolbar;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.JLabel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import us.mn.state.dot.geokit.GeodeticDatum;
import us.mn.state.dot.geokit.Position;
import us.mn.state.dot.geokit.UTMPosition;
import us.mn.state.dot.geokit.UTMZone;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.SystemAttributeHelper;

/**
 * A tool panel that contains the map coordinates of the mouse pointer.
 *
 * @author Stephen Donecker
 * @author Michael Darter
 * @company University of California, Davis
 */
public class CoordinatePanel extends ToolPanel implements MouseMotionListener {

	/** The map */
	protected final MapBean m_map;

	/** The label used for cursor coordinates */
	protected final JLabel m_coordinates = new JLabel();

	/** Determine displayed coordinate type */
	static protected final String COORDINATE_TYPE = 
		SystemAttrEnum.MAP_TOOLBAR_COORDS.getString();

	/** UTM zone for conversion to lat/lon */
	static protected final UTMZone UTM_ZONE =
		new UTMZone(SystemAttrEnum.MAP_UTM_ZONE.getInt(),
			SystemAttrEnum.MAP_NORTHERN_HEMISPHERE.getBoolean());

	/** The lat long decimal format */
	static protected final String LAT_LONG_DECIMAL_FORMAT = "0.000000";

	/** The UTM decimal format */
	static protected final String UTM_DECIMAL_FORMAT = "0.0";

	/** Constructor */
	public CoordinatePanel(MapBean m) {
		assert m !=  null;
		m_map = m;
		add(m_coordinates);
		// listen for mouse motion
		m_map.addMouseMotionListener(this);		
	}

	/** is this panel IRIS enabled? */
	public static boolean getIEnabled() {
		return true;
	}

	/** Process the mouse moved event and update the status bar */
	public void mouseMoved(MouseEvent e) {
		Point2D p = m_map.transformPoint(e.getPoint());
		// UTM
		if(COORDINATE_TYPE.toLowerCase().equals("utm")) {
			DecimalFormat df = 
				new DecimalFormat(UTM_DECIMAL_FORMAT);
			String easting = df.format(p.getX());
			String northing = df.format(p.getY());
			m_coordinates.setText(easting + " m E " + 
				northing + " m N");
		// WGS84
		} else {
			UTMPosition utm = new UTMPosition(UTM_ZONE, p.getX(),
				p.getY());
			Position pos = utm.getPosition(GeodeticDatum.WGS_84);
			DecimalFormat df = 
				new DecimalFormat(LAT_LONG_DECIMAL_FORMAT);
			String lat = df.format(pos.getLatitude());
			String lon = df.format(pos.getLongitude());
			m_coordinates.setText("lat " + lat + 
				"\u00B0 lon " + lon + "\u00B0"); 
		}
	}

	/** Process the mouse dragged event and update the status bar */
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	/** cleanup */
	public void dispose() {
		m_map.removeMouseMotionListener(this);		
	}
}
