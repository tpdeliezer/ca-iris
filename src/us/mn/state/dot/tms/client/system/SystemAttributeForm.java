/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.system;

import us.mn.state.dot.tms.SystemAttribute;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyTableForm;
import us.mn.state.dot.tms.client.widget.ZTable;
import us.mn.state.dot.tms.utils.I18N;

/**
 * The system attribute allows administrators to change system-wide policy
 * attributes.
 *
 * @author Douglas Lau
 */
public class SystemAttributeForm extends ProxyTableForm<SystemAttribute> {

	/** Check if the user is permitted to use the form */
	static public boolean isPermitted(Session s) {
		return s.isUpdatePermitted(SystemAttribute.SONAR_TYPE);
	}

	/** Create a new system attribute form */
	public SystemAttributeForm(Session s) {
		super(I18N.get("system.attributes"),
			new SystemAttributeTableModel(s));
		setHelpPageName("help.systemattributeform");
	}

	/** Create the table */
	protected ZTable createTable() {
		return new ZTable() {
			public String getToolTipText(int row, int column) {
				SystemAttribute sa = model.getProxy(row);
				if(sa != null) {
					return SystemAttrEnum.getDesc(
						sa.getName());
				} else
					return null;
			}
		};
	}

	/** Get the row height */
	protected int getRowHeight() {
		return 20;
	}

	/** Get the visible row count */
	protected int getVisibleRowCount() {
		return 12;
	}
}
