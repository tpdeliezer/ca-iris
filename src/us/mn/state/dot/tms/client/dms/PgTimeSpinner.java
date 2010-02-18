/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2009-2010  Minnesota Department of Transportation
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

import javax.swing.AbstractSpinnerModel;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import us.mn.state.dot.tms.DmsPgTime;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.utils.I18N;
import us.mn.state.dot.tms.utils.Log;
import us.mn.state.dot.tms.utils.SString;

/**
 * A spinner for the DMS message page time. This control is used to specify
 * page on-time (and in the future, perhaps page off-time). Page on-time is
 * a function of the current number of pages. For a single page message, zero
 * should be the default, with non-zero values possible, which indicates
 * a single page flashing message. For multi-page messages, the page on-time
 * must be non-zero. System attributes are used to specify the minimum,
 * default,and maximum values for page on-time. This spinner enforces these
 * values. Because the valid value for page on-time is a function of the
 * current number of pages (in the composer), those controls notify the
 * spinner of the current number of pages, so the spinner can adjust the
 * current value--e.g. if it's 0, and the user has just entered a 2nd page,
 * the spinner sets a non-default page on-time.
 *
 * @see DmsPgTime, DMSDispatcher, SystemAttributeForm
 * @author Michael Darter
 * @author Douglas Lau
 */
public class PgTimeSpinner extends JSpinner implements ChangeListener {

	/** Page on-time increment value */
	static public final float INC_ONTIME_SECS = .1f;

	/** Round to a single decimal point */
	static protected double roundSingle(double v) {
		return (double)Math.round(v * 10.0) / 10.0;
	}

	/** Is this control IRIS enabled? */
	static public boolean getIEnabled() {
		return SystemAttrEnum.DMS_PAGE_ON_SELECTION_ENABLE.getBoolean();
	}

	/** Does the current message have single or multiple pages.
	 *  This determines if zero is an acceptable value. */
	protected boolean m_singlepg = true;

	/** Page time spinner model, which allows for an closed range of
	 *  values. Single page messages also allow a value of zero. */
	protected class PgTimeSpinnerModel extends AbstractSpinnerModel {

		/** Inclusive minimum value allowed */
		private final double m_min;

		/** Inclusive maximum value allowed */
		private final double m_max;

		/** Increment value */
		private final double m_inc;

		/** Current model value */
		private double m_value = 0;

		/** Constructor.
		 *  @param def Initial value.
		 *  @param max Maximum (inclusive) allowed value.
		 *  @param min Minimum (inclusive) allowed value.
		 *  @param inc Increment value. */
		public PgTimeSpinnerModel(double def, double min, double max,
			double inc)
		{
			m_min = Math.min(min, max);
			m_max = Math.max(min, max);
			m_value = validate(def);
			m_inc = roundSingle(inc);
		}

		/** Return a validated spinner value in seconds. A value of
		 *  zero is valid for single page messages only. */
		private double validate(double value) {
			DmsPgTime t = new DmsPgTime(value).validateValue(
				m_singlepg, new DmsPgTime(m_min),
				new DmsPgTime(m_max));
			return t.toSecs();
		}

		/** Get the next value, or null if the next value would be
		 *  out of range. */
		public Object getNextValue() {
			if(m_singlepg && m_value == 0)
				return m_min;
			return validate(m_value + m_inc);
		}

		/** Get previous value, or null if the previous value is
		 *  out of range. */
		public Object getPreviousValue() {
			if(m_singlepg && m_value == 0)
				return null;
			return validate(m_value - m_inc);
		}

		/** Get current value */
		public Object getValue() {
			return m_value;
		}

		/** Set current value */
		public void setValue(Object value) {
			if(value == null) {
				m_value = 0;
			} else if(value instanceof DmsPgTime) {
				DmsPgTime pt = (DmsPgTime)value;
				m_value = pt.toSecs();
			} else if(value instanceof Number) {
				m_value = roundSingle(
					((Number)value).doubleValue());
			} else {
				m_value = SString.stringToDouble(
					value.toString());
			}
			fireStateChanged();
		}
	}

	/** DMS dispatcher */
	protected final DMSDispatcher dispatcher;

	/** Create a new page time spinner.
	 * @param d DMS dispatcher */
	public PgTimeSpinner(DMSDispatcher d) {
		dispatcher = d;
		setModel(new PgTimeSpinnerModel(
			DmsPgTime.getDefaultOn(true).toSecs(),
			DmsPgTime.getMinOnTime().toSecs(),
			DmsPgTime.getMaxOnTime().toSecs(), INC_ONTIME_SECS));
		setToolTipText(I18N.get("PgOnTimeSpinner.ToolTip"));
		addChangeListener(this);

		// force the spinner to be editable
		JFormattedTextField tf = ((JSpinner.DefaultEditor)
			this.getEditor()).getTextField();
    		tf.setEditable(true);
	}

	/** Enable or disable */
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		// if disabled, reset value to default
		if(!b)
			setValue(DmsPgTime.getDefaultOn(m_singlepg).toSecs());
	}

	/** Set value using seconds. */
	public void setValue(float secs) {
		super.setValue(new DmsPgTime(secs).toSecs());
	}

	/** Set value. */
	public void setValue(DmsPgTime t) {
		super.setValue(t.toSecs());
	}

	/** Counter to indicate we're adjusting widgets.  This needs to be
	 * incremented before calling dispatcher methods which might cause
	 * callbacks to this class.  This prevents infinite loops. */
	protected int adjusting = 0;

	/** Set the selected item and ignore any actionPerformed
	 *  events that are generated.
	 *  @param ms MULTI string containing page on time. */
	public void setValueNoAction(String ms) {
		adjusting++;
		setValue(ms);
		adjusting--;
	}

	/** If the spinner is IRIS enabled, return the current value,
	 *  otherwise return the system default. */
	public DmsPgTime getValuePgTime() {
		DmsPgTime ret = DmsPgTime.getDefaultOn(m_singlepg);
		// return current value
		if(getIEnabled()) {
			Object v = super.getValue();
			if(v instanceof Number)
				ret = new DmsPgTime(((Number)v).floatValue());
		}
		return ret;
	}

	/** Set value using the page on-time specified in the 1st page
	 *  of the MULTI string. If no value is specified in the MULTI,
	 *  the default value is used for multi-page messages else 0
	 *  for single page messages.
	 *  @param smulti A MULTI string, containing possible page times. */
	public void setValue(String ms) {
		setValue(new MultiString(ms).getPageOnTime());
	}

	/** Catch state change events. Defined in interface ChangeListener. */
	public void stateChanged(ChangeEvent e) {
		if(adjusting == 0)
			dispatcher.selectPreview(true);
	}

	/** Set number of pages in current message. */
	private void setSinglePage(boolean sp) {
		m_singlepg = sp;
	}

	/** Validate the current value using the current multistring. */
	public void updateValidation(String multi) {
		setSinglePage(new MultiString(multi).singlePage());
		DmsPgTime pt = getValuePgTime();
		if(!m_singlepg) {
			if(pt.isZero())
				setValue(DmsPgTime.getDefaultOn(m_singlepg));
		}
	}

	/** Dispose */
	public void dispose() {
		removeChangeListener(this);
	}
}
