/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.detector;

import us.mn.state.dot.tms.Detector;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyTableForm;
import us.mn.state.dot.tms.client.widget.IPanel;
import us.mn.state.dot.tms.client.widget.IPanel.Stretch;
import us.mn.state.dot.tms.utils.I18N;

/**
 * A form for displaying and editing detectors
 *
 * @author Douglas Lau
 */
public class DetectorForm extends ProxyTableForm<Detector> {

	/** Check if the user is permitted to use the form */
	static public boolean isPermitted(Session s) {
		return s.canRead(Detector.SONAR_TYPE);
	}

	/** Detector panel */
	private final DetectorPanel det_pnl;

	/** Create a new detector form */
	public DetectorForm(Session s) {
		super(I18N.get("detector.plural"), new DetectorModel(s));
		det_pnl = new DetectorPanel(s, true);
		det_pnl.initialize();
	}

	/** Dispose of the form */
	@Override protected void dispose() {
		det_pnl.dispose();
		super.dispose();
	}

	/** Add the table to the panel */
	@Override protected void addTable(IPanel p) {
		p.add(table, Stretch.HALF);
		p.add(det_pnl, Stretch.FULL);
	}

	/** Get the row height */
	@Override protected int getRowHeight() {
		return 20;
	}

	/** Select a new proxy */
	@Override protected void selectProxy() {
		super.selectProxy();
		det_pnl.setDetector(getSelectedProxy());
	}
}
