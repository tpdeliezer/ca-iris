/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.incident;

import us.mn.state.dot.map.marker.AbstractMarker;

/**
 * Marker used to paint incidents.
 *
 * @author Douglas Lau
 */
public class IncidentMarker extends AbstractMarker {

	/** Maximum size (in user coordinates) to render incident marker */
	static protected final int MARKER_SIZE = 1000;

	/** Create a new incident marker */
	public IncidentMarker() {
		this(MARKER_SIZE);
	}

	/** Create a new incident marker */
	public IncidentMarker(float size) {
		super(5);
		size = Math.min(MARKER_SIZE, size);
		float half = size / 2;
		float quarter = size / 4;
		float x = 0;
		float y = 0;
		path.moveTo(x, y);
		path.lineTo(x += half, y);
		path.lineTo(x -= quarter, y += size);
		path.lineTo(x -= quarter, y -= size);
		path.closePath();
	}
}
