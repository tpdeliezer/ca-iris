/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2010  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.dms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JComboBox;
import us.mn.state.dot.tms.QuickMessage;
import us.mn.state.dot.tms.QuickMessageHelper;
import us.mn.state.dot.tms.SystemAttrEnum;

/**
 * The quick message combobox is a widget which allows the user to select
 * a precomposed "quick" message. When the user changes a quick message
 * selection via this combobox, the dispatcher is flagged that it should update
 * its widgets with the newly selected message.
 *
 * @see DMSDispatcher, QuickMessage
 * @author Michael Darter
 * @author Douglas Lau
 */
public class QuickMessageCBox extends JComboBox {

	/** Given a QuickMessage or String, return the cooresponding quick 
	 * message name or an empty string if none exists. */
	static protected String getQuickLibMsgName(Object obj) {
		if(obj instanceof String)
			return (String)obj;
		else if(obj instanceof QuickMessage)
			return ((QuickMessage)obj).getName();
		else
			return "";
	}

	/** DMS dispatcher */
	protected final DMSDispatcher dispatcher;

	/** Focus listener for editor component */
	private final FocusListener focus_listener;

	/** Action listener for combo box */
	private final ActionListener action_listener;

	/** Counter to indicate we're adjusting widgets.  This needs to be
	 * incremented before calling dispatcher methods which might cause
	 * callbacks to this class.  This prevents infinite loops. */
	protected int adjusting = 0;

	/** Create a new quick message combo box */
	public QuickMessageCBox(DMSDispatcher d) {
		dispatcher = d;
		setEditable(true);
		focus_listener = new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				getEditor().selectAll();
			}
			public void focusLost(FocusEvent e) {
				handleEditorFocusLost(e);
			}
		};
		getEditor().getEditorComponent().addFocusListener(
			focus_listener);
		action_listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateDispatcher();
			}
		};
		addActionListener(action_listener);
	}

	/** Handle editor focus lost */
	protected void handleEditorFocusLost(FocusEvent e) {
		Object item = getEditor().getItem();
		if(item instanceof String)
			handleEditorFocusLost((String)item);
	}

	/** Handle editor focus lost */
	protected void handleEditorFocusLost(String item) {
		String name = item.replace(" ", "");
		getEditor().setItem(name);
		QuickMessage qm = QuickMessageHelper.lookup(name);
		if(qm != null) {
			getModel().setSelectedItem(qm);
			updateDispatcher(qm);
		}
	}

	/** Update the dispatcher with the selected quick message */
	protected void updateDispatcher() {
		QuickMessage qm = getSelectedProxy();
		if(qm != null)
			updateDispatcher(qm);
	}

	/** Get the currently selected proxy */
	protected QuickMessage getSelectedProxy() {
		Object obj = getSelectedItem();
		if(obj instanceof QuickMessage)
			return (QuickMessage)obj;
		else
			return null;
	}

	/** Update the dispatcher with the specified quick message */
	protected void updateDispatcher(QuickMessage qm) {
		String ms = qm.getMulti();
		if(!ms.isEmpty()) {
			adjusting++;
			dispatcher.setMessage(ms);
			dispatcher.selectPreview(true);
			adjusting--;
		}
	}

	/** Set the current message MULTI string */
	public void setMessage(String ms) {
		if(adjusting == 0) {
			if(ms.isEmpty())
				setSelectedItem(null);
			else
				setSelectedItem(QuickMessageHelper.find(ms));
		}
	}

	/** Set selected item, but only if it is different from the 
	 * currently selected item. Triggers a call to actionPerformed().
	 * @param obj May be a String, or QuickMessage. */
	public void setSelectedItem(Object obj) {
		String nametoset = getQuickLibMsgName(obj);
		String namecur = getSelectedName();
		if(!namecur.equals(nametoset)) {
			if(nametoset.isEmpty())
				super.setSelectedItem(null);
			else {
				QuickMessage qm = QuickMessageHelper.lookup(
					nametoset);
				super.setSelectedItem(qm);
			}
		}
	}

	/** Get the name of the currently selected quick message */
	protected String getSelectedName() {
		return getQuickLibMsgName(getSelectedItem());
	}

	/** Dispose */
	public void dispose() {
		removeActionListener(action_listener);
		getEditor().getEditorComponent().
			removeFocusListener(focus_listener);
	}

	/** Is this control IRIS enabled? */
	static public boolean getIEnabled() {
		return SystemAttrEnum.DMS_QLIB_ENABLE.getBoolean();
	}
}
