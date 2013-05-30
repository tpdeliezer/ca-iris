/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2005-2013  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.camera;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import us.mn.state.dot.sched.ActionJob;
import us.mn.state.dot.sonar.Connection;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.Camera;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.VideoMonitor;
import static us.mn.state.dot.tms.client.IrisClient.WORKER;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.SonarState;
import us.mn.state.dot.tms.client.proxy.ProxySelectionListener;
import us.mn.state.dot.tms.client.proxy.ProxySelectionModel;
import us.mn.state.dot.tms.client.widget.IPanel;
import us.mn.state.dot.tms.client.widget.WrapperComboBoxModel;
import us.mn.state.dot.tms.utils.I18N;

/**
 * GUI for viewing camera images
 *
 * @author Douglas Lau
 * @author Tim Johnson
 */
public class CameraViewer extends IPanel
	implements ProxySelectionListener<Camera>
{
	/** The system attribute for the number of button presets */
	static private final int NUMBER_BUTTON_PRESETS =
		SystemAttrEnum.CAMERA_NUM_PRESET_BTNS.getInt();

	/** Button number to select previous camera */
	static private final int BUTTON_PREVIOUS = 10;

	/** Button number to select next camera */
	static private final int BUTTON_NEXT = 11;

	/** Video size */
	static private final VideoRequest.Size SIZE = VideoRequest.Size.MEDIUM;

	/** User session */
	private final Session session;

	/** Sonar state */
	private final SonarState state;

	/** Logged in user */
	private final User user;

	/** Camera name label */
	private final JLabel name_lbl = createValueLabel();

	/** Camera location label */
	private final JLabel location_lbl = createValueLabel();

	/** Video output selection ComboBox */
	private final JComboBox output_cbx;

	/** Selected video monitor output */
	private VideoMonitor video_monitor;

	/** Camera PTZ control */
	private final CameraPTZ cam_ptz;

	/** Streaming video panel */
	private final StreamPanel stream_pnl;

	/** Panel for camera presets */
	private final PresetPanel preset_pnl;

	/** Proxy manager for camera devices */
	private final CameraManager manager;

	/** Currently selected camera */
	private Camera selected = null;

	/** Joystick PTZ handler */
	private final JoystickPTZ joy_ptz;

	/** Create a new camera viewer */
	public CameraViewer(Session s, CameraManager man) {
		manager = man;
		manager.getSelectionModel().addProxySelectionListener(this);
		session = s;
		cam_ptz = new CameraPTZ(s);
		joy_ptz = new JoystickPTZ(cam_ptz);
		preset_pnl = new PresetPanel();
		state = session.getSonarState();
		user = session.getUser();
		stream_pnl = createStreamPanel();
		output_cbx = createOutputCombo();
		output_cbx.addActionListener(new ActionJob(WORKER) {
			public void perform() {
				monitorSelected();
			}
		});
		joy_ptz.addJoystickListener(new JoystickListener() {
			public void buttonChanged(JoystickButtonEvent ev) {
				if(ev.pressed)
					doJoyButton(ev);
			}
		});
		setTitle(I18N.get("camera.selected"));
		add("device.name");
		add(name_lbl);
		add("camera.output");
		add(output_cbx, Stretch.LAST);
		add("location");
		add(location_lbl, Stretch.LAST);
		add(stream_pnl, Stretch.FULL);
		add(preset_pnl, Stretch.CENTER);
		clear();
	}

	/** Create the stream panel */
	private StreamPanel createStreamPanel() {
		VideoRequest vr = new VideoRequest(session.getProperties(),
			SIZE);
		Connection c = state.lookupConnection(state.getConnection());
		vr.setSonarSessionId(c.getSessionId());
		vr.setRate(30);
		return new StreamPanel(cam_ptz, vr);
	}

	/** Create the video output selection combo box */
	private JComboBox createOutputCombo() {
		JComboBox box = new JComboBox();
		FilteredMonitorModel m = new FilteredMonitorModel(user, state);
		box.setModel(new WrapperComboBoxModel(m));
		if(m.getSize() > 1)
			box.setSelectedIndex(1);
		return box;
	}

	/** Process a joystick button event */
	private void doJoyButton(JoystickButtonEvent ev) {
		if(ev.button == BUTTON_NEXT)
			selectNextCamera();
		else if(ev.button == BUTTON_PREVIOUS)
			selectPreviousCamera();
		else if(ev.button >= 0 && ev.button < NUMBER_BUTTON_PRESETS)
			selectCameraPreset(ev.button + 1);
	}

	/** Select the next camera */
	private void selectNextCamera() {
		Camera cam = state.getCamCache().getCameraModel().higher(
			selected);
		if(cam != null)
			manager.getSelectionModel().setSelected(cam);
	}

	/** Select the previous camera */
	private void selectPreviousCamera() {
		Camera cam = state.getCamCache().getCameraModel().lower(
			selected);
		if(cam != null)
			manager.getSelectionModel().setSelected(cam);
	}

	/** Command current camera to goto preset location */
	private void selectCameraPreset(int preset) {
		Camera proxy = selected;	// Avoid race
		if(proxy != null)
			proxy.setRecallPreset(preset);
	}

	/** Dispose of the camera viewer */
	public void dispose() {
		removeAll();
		joy_ptz.dispose();
		cam_ptz.setCamera(null);
		stream_pnl.dispose();
		selected = null;
	}

	/** Set the selected camera */
	public void setSelected(final Camera camera) {
		if(camera == selected)
			return;
		cam_ptz.setCamera(camera);
		selected = camera;
		if(camera != null) {
			name_lbl.setText(camera.getName());
			location_lbl.setText(GeoLocHelper.getDescription(
				camera.getGeoLoc()));
			stream_pnl.setCamera(camera);
			if(video_monitor != null)
				video_monitor.setCamera(camera);
			preset_pnl.setCamera(camera);
			preset_pnl.setEnabled(cam_ptz.canControlPtz());
		} else
			clear();
	}

	/** Called whenever a camera is added to the selection */
	public void selectionAdded(Camera c) {
		if(manager.getSelectionModel().getSelectedCount() <= 1)
			setSelected(c);
	}

	/** Called whenever a camera is removed from the selection */
	public void selectionRemoved(Camera c) {
		ProxySelectionModel<Camera> model = manager.getSelectionModel();
		if(model.getSelectedCount() == 1) {
			for(Camera cam: model.getSelected())
				setSelected(cam);
		} else if(c == selected)
			setSelected(null);
	}

	/** Called when a video monitor is selected */
	private void monitorSelected() {
		Camera camera = selected;
		Object o = output_cbx.getSelectedItem();
		if(o instanceof VideoMonitor) {
			video_monitor = (VideoMonitor)o;
			video_monitor.setCamera(camera);
		} else
			video_monitor = null;
	}

	/** Clear all of the fields */
	private void clear() {
		name_lbl.setText("");
		location_lbl.setText("");
		stream_pnl.setCamera(null);
		preset_pnl.setEnabled(false);
	}
}
