/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011  Minnesota Department of Transportation
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import us.mn.state.dot.sched.TimeSteward;

/**
 * This class writes out the TMS configuration data to an XML file.
 *
 * @author Douglas Lau
 */
public class TmsConfigXmlWriter extends XmlWriter {

	/** TMS config XML file */
	static protected final String CONFIG_XML = "tms_config.xml";

	/** R_Node XML writer */
	protected final R_NodeXmlWriter node_writer = new R_NodeXmlWriter();

	/** Ramp meter XML writer */
	protected final RampMeterXmlWriter meter_writer =
		new RampMeterXmlWriter();

	/** Camera XML writer */
	protected final CameraXmlWriter cam_writer = new CameraXmlWriter();

	/** Geo loc XML writer */
	protected final GeoLocXmlWriter loc_writer = new GeoLocXmlWriter();

	/** Create a new TMS config XML writer */
	public TmsConfigXmlWriter() {
		super(CONFIG_XML, true);
	}

	/** Print the TMS config XML file */
	public void print(final PrintWriter out) {
		printHead(out);
		printBody(out);
		printTail(out);
	}

	/** Print the head of the TMS config XML file */
	protected void printHead(PrintWriter out) {
		out.println(XML_DECLARATION);
		printDtd(out);
		out.println("<tms_config system='RTMC' time_stamp='" +
			TimeSteward.getDateInstance() + "'>");
	}

	/** Print the DTD */
	protected void printDtd(PrintWriter out) {
		out.println("<!DOCTYPE tms_config [");
		out.println("<!ELEMENT tms_config (corridor | meter)*>");
		out.println("<!ATTLIST tms_config system CDATA #REQUIRED>");
		out.println("<!ATTLIST tms_config time_stamp CDATA #REQUIRED>");
		node_writer.printDtd(out);
		meter_writer.printDtd(out);
		out.println("]>");
	}

	/** Print the body of the TMS config XML file */
	protected void printBody(PrintWriter out) {
		node_writer.print(out);
		meter_writer.print(out);
	}

	/** Print the tail of the TMS config XML file */
	protected void printTail(PrintWriter out) {
		out.println("</tms_config>");
	}

	/** Write individual XML fragments */
	public void writeFragments() throws IOException {
		node_writer.write();
		meter_writer.write();
		cam_writer.write();
		loc_writer.write();
	}
}
