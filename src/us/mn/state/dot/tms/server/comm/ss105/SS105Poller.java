/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2004-2014  Minnesota Department of Transportation
 * Copyright (C) 2011-2015  AHMCT, University of California
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
import us.mn.state.dot.sched.DebugLog;
import us.mn.state.dot.tms.CommLinkHelper;
import us.mn.state.dot.tms.server.CommLinkImpl;
import us.mn.state.dot.tms.server.ControllerImpl;
import us.mn.state.dot.tms.server.comm.MessagePoller;
import us.mn.state.dot.tms.server.comm.Messenger;
import us.mn.state.dot.tms.server.comm.PriorityLevel;
import us.mn.state.dot.tms.server.comm.SamplePoller;

/**
 * SS105Poller is a java implementation of the Wavetronix SmartSensor 105
 * serial data communication protocol.
 *
 * @author Douglas Lau
 * @author Michael Darter
 * @author Travis Swanston
 */
public class SS105Poller extends MessagePoller<SS105Property>
	implements SamplePoller
{
	/** SS 105 debug log */
	static protected final DebugLog SS105_LOG = new DebugLog("ss105");

	/** Create a new SS105 poller */
	public SS105Poller(String n, Messenger m) {
		super(n, m);
		CommLinkImpl cli = (CommLinkImpl)CommLinkHelper.lookup(n);
		if (cli == null) {
			SS105_LOG.log("Failed to find CommLink.");
			return;
		}
		int to = cli.getTimeout();
		try {
			m.setTimeout(to);
			SS105_LOG.log("Set Messenger timeout to " + to + ".");
		}
		catch (IOException e) {
			SS105_LOG.log("Failed to set Messenger timeout.");
		}
	}

	/** Check if a drop address is valid */
	@Override
	public boolean isAddressValid(int drop) {
		return drop >= 0 && drop <= 9999;
	}

	/** Perform a controller download */
	@Override
	protected void download(ControllerImpl c, PriorityLevel p) {
		if (c.isActive()) {
			OpSendSensorSettings o =
				new OpSendSensorSettings(c, true);
			o.setPriority(p);
			addOperation(o);
		}
	}

	/** Perform a controller reset */
	@Override
	public void resetController(ControllerImpl c) {
		addOperation(new OpSendSensorSettings(c, true));
	}

	/** Send sample settings to a controller */
	@Override
	public void sendSettings(ControllerImpl c) {
		addOperation(new OpSendSensorSettings(c, false));
	}

	/** Query sample data.
 	 * @param c Controller to poll.
 	 * @param p Sample period in seconds. */
	@Override
	public void querySamples(ControllerImpl c, int p) {
		if (p == 30)
			addOperation(new OpQuerySamples(c, p));
	}

	/** Get the protocol debug log */
	@Override
	protected DebugLog protocolLog() {
		return SS105_LOG;
	}

	/** Query the sample poller. */
	@Override
	public void queryPoller() {
	}

}
