/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server;

import java.util.HashMap;
import java.util.Map;
import java.sql.ResultSet;
import us.mn.state.dot.sonar.Namespace;
import us.mn.state.dot.sonar.SonarException;
import us.mn.state.dot.tms.Beacon;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.Controller;
import us.mn.state.dot.tms.DeviceRequest;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.TMSException;
import us.mn.state.dot.tms.server.comm.BeaconPoller;
import us.mn.state.dot.tms.server.comm.MessagePoller;

/**
 * A Beacon is a light which flashes toward oncoming traffic.
 *
 * @author Douglas Lau
 */
public class BeaconImpl extends DeviceImpl implements Beacon {

	/** Load all the beacons */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, BeaconImpl.class);
		store.query("SELECT name, geo_loc, controller, pin, notes, " +
			"camera, message FROM iris." + SONAR_TYPE + ";",
			new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new BeaconImpl(
					namespace,
					row.getString(1),	// name
					row.getString(2),	// geo_loc
					row.getString(3),	// controller
					row.getInt(4),		// pin
					row.getString(5),	// notes
					row.getString(6),	// camera
					row.getString(7)	// message
				));
			}
		});
	}

	/** Get a mapping of the columns */
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("geo_loc", geo_loc);
		map.put("controller", controller);
		map.put("pin", pin);
		map.put("notes", notes);
		map.put("camera", camera);
		map.put("message", message);
		return map;
	}

	/** Get the database table name */
	public String getTable() {
		return "iris." + SONAR_TYPE;
	}

	/** Get the SONAR type name */
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a new beacon with a string name */
	public BeaconImpl(String n) throws TMSException, SonarException {
		super(n);
		GeoLocImpl g = new GeoLocImpl(name);
		g.notifyCreate();
		geo_loc = g;
	}

	/** Create a beacon */
	protected BeaconImpl(String n, GeoLocImpl l, ControllerImpl c,
		int p, String nt, CameraImpl cam, String m)
	{
		super(n, c, p, nt);
		geo_loc = l;
		camera = cam;
		message = m;
		initTransients();
	}

	/** Create a beacon */
	protected BeaconImpl(Namespace ns, String n, String l,
		String c, int p, String nt, String cam, String m)
	{
		this(n, (GeoLocImpl)ns.lookupObject(GeoLoc.SONAR_TYPE, l),
		       (ControllerImpl)ns.lookupObject(Controller.SONAR_TYPE,c),
			p, nt,
			(CameraImpl)ns.lookupObject(Camera.SONAR_TYPE, cam), m);
	}

	/** Destroy an object */
	public void doDestroy() throws TMSException {
		super.doDestroy();
		geo_loc.notifyRemove();
	}

	/** Device location */
	protected GeoLocImpl geo_loc;

	/** Get the device location */
	public GeoLoc getGeoLoc() {
		return geo_loc;
	}

	/** Camera from which the beacon can be seen */
	protected Camera camera;

	/** Set the verification camera */
	public void setCamera(Camera c) {
		camera = c;
	}

	/** Set the verification camera */
	public void doSetCamera(Camera c) throws TMSException {
		if(c == camera)
			return;
		store.update(this, "camera", c);
		setCamera(c);
	}

	/** Get verification camera */
	public Camera getCamera() {
		return camera;
	}

	/** Message text */
	protected String message = "";

	/** Set the message text */
	public void setMessage(String m) {
		message = m;
	}

	/** Set the message text */
	public void doSetMessage(String m) throws TMSException {
		if(m.equals(message))
			return;
		store.update(this, "message", m);
		setMessage(m);
	}

	/** Get the message text */
	public String getMessage() {
		return message;
	}

	/** Flashing state */
	private transient boolean flashing;

	/** Set the flashing state */
	public void setFlashing(boolean f) {
		BeaconPoller p = getBeaconPoller();
		if(p != null)
			p.setFlashing(this, f);
	}

	/** Check if the beacon is flashing */
	public boolean getFlashing() {
		return flashing;
	}

	/** Set the flashing state and notify clients */
	public void setFlashingNotify(boolean f) {
		if(f != flashing) {
			flashing = f;
			notifyAttribute("flashing");
		}
	}

	/** Get a beacon poller */
	private BeaconPoller getBeaconPoller() {
		if(isActive()) {
			MessagePoller p = getPoller();
			if(p instanceof BeaconPoller)
				return (BeaconPoller)p;
		}
		return null;
	}

	/** Request a device operation */
	public void setDeviceRequest(int r) {
		DeviceRequest dr = DeviceRequest.fromOrdinal(r);
		BeaconPoller p = getBeaconPoller();
		if(p != null)
			p.sendRequest(this, dr);
	}
}