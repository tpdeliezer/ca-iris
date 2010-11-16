/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2010  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.roads;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.R_Node;
import us.mn.state.dot.tms.R_NodeTransition;
import us.mn.state.dot.tms.R_NodeType;

/**
 * Renderer for roadway nodes
 *
 * @author Douglas Lau
 */
public class R_NodeRenderer extends JPanel {

	/** Background color for nodes with GPS points */
	static public final Color COLOR_GPS = Color.GREEN;

	/** Background color for nodes with bad locations */
	static public final Color COLOR_NO_LOC = Color.RED;

	/** Width of one lane */
	static protected final int LANE_WIDTH = 20;

	/** Height of one lane */
	static protected final int LANE_HEIGHT = 18;

	/** Total width of roadway node renderers */
	static protected final int WIDTH = LANE_WIDTH * 19;

	/** Width of a detector */
	static protected final int DET_WIDTH = LANE_WIDTH - 9;

	/** Height of a detector */
	static protected final int DET_HEIGHT = LANE_HEIGHT - 6;

	/** Solid stroke line */
	static protected final BasicStroke LINE_SOLID = new BasicStroke(8,
		BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	/** Dashed stroke line */
	static protected final BasicStroke LINE_DASHED = new BasicStroke(4,
		BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1,
		new float[]{ LANE_HEIGHT / 3, 2 * LANE_HEIGHT / 3 },
		2 * LANE_HEIGHT / 3
	);

	/** Basic stroke line */
	static protected final BasicStroke LINE_BASIC = new BasicStroke(1);
	static protected final BasicStroke LINE_BASIC2 = new BasicStroke(2);

	/** Font for cross-street labels */
	static protected final Font FONT_XSTREET =
		new Font("Arial", Font.BOLD, 12);

	/** Renderer component width */
	protected int width = 0;

	/** Renderer component height */
	protected int height = 0;

	/** R_node model */
	protected final R_NodeModel model;

	/** Get the roadway node proxy */
	public R_Node getProxy() {
		return model.r_node;
	}

	/** Create a new roadway node renderer */
	public R_NodeRenderer(R_NodeModel m) {
		model = m;
	}

	/** Set the selected status of the component */
	public void setSelected(boolean sel) {
		if(sel)
			setBackground(Color.LIGHT_GRAY);
		else {
			GeoLoc loc = getProxy().getGeoLoc();
			if(GeoLocHelper.isNull(loc))
				setBackground(COLOR_NO_LOC);
			else
				setBackground(COLOR_GPS);
		}
	}

	/** Get the fog line for the given lane */
	static protected int getFogLine(int lane) {
		return LANE_WIDTH * (2 + lane);
	}

	/** Get the upstream fog line on the given side of the road */
	protected int getUpstreamLine(boolean side) {
		return getFogLine(model.getUpstreamLane(side));
	}

	/** Get the downstream fog line on the given side of the road */
	protected int getDownstreamLine(boolean side) {
		return getFogLine(model.getDownstreamLane(side));
	}

	/** Allow for subclasses to modify cross-street label */
	protected String streetString(String street) {
		return street;
	}

	/** Paint the renderer */
	public void paintComponent(Graphics g) {
		Dimension d = (Dimension)getSize();
		width = (int)d.getWidth();
		height = (int)d.getHeight();
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		fillBackground(width, height, g2);
		g2.setStroke(LINE_SOLID);
		drawYellowLines(g2);
		drawWhiteLines(g2);
		fillRoadway(g2);
		drawSkipStripes(g2);
		drawDetectors(g2);
		String xStreet = GeoLocHelper.getCrossDescription(
			getProxy().getGeoLoc());
		if(xStreet != null)
			drawCrossStreet(g2, xStreet);
	}

	/** Fill the background */
	protected void fillBackground(int width, int height, Graphics2D g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);
		g.setColor(Color.BLACK);
		g.drawLine(0, 0, width, 0);
	}

	/** Draw the yellow lines */
	protected void drawYellowLines(Graphics2D g) {
		R_NodeType nt =R_NodeType.fromOrdinal(getProxy().getNodeType());
		g.setColor(Color.YELLOW);
		if(model.hasMainline())
			g.draw(createYellowMainLine());
		if(nt == R_NodeType.ENTRANCE)
			g.draw(createEntranceYellow());
		else if(nt == R_NodeType.EXIT)
			g.draw(createExitYellow());
	}

	/** Create the yellow main line */
	protected Shape createYellowMainLine() {
		int y0 = getDownstreamLine(true);
		int y1 = getUpstreamLine(true);
		return new Line2D.Double(y0, 0, y1, height);
	}

	/** Draw the white lines */
	protected void drawWhiteLines(Graphics2D g) {
		R_NodeType nt =R_NodeType.fromOrdinal(getProxy().getNodeType());
		g.setColor(Color.WHITE);
		if(model.hasMainline())
			g.draw(createWhiteMainLine());
		if(nt == R_NodeType.ENTRANCE)
			g.draw(createEntranceWhite());
		else if(nt == R_NodeType.EXIT)
			g.draw(createExitWhite());
	}

	/** Create the white main line */
	protected Shape createWhiteMainLine() {
		int w0 = getDownstreamLine(false);
		int w1 = getUpstreamLine(false);
		return new Line2D.Double(w1, height, w0, 0);
	}

	/** Fill the roadway area */
	protected void fillRoadway(Graphics2D g) {
		R_NodeType nt =R_NodeType.fromOrdinal(getProxy().getNodeType());
		g.setColor(Color.BLACK);
		if(model.hasMainline())
			g.fill(createMainRoadway());
		if(nt == R_NodeType.ENTRANCE)
			g.fill(createEntranceRoadway());
		else if(nt == R_NodeType.EXIT)
			g.fill(createExitRoadway());
	}

	/** Create the mainline roadway area */
	protected Shape createMainRoadway() {
		GeneralPath path = new GeneralPath(createYellowMainLine());
		path.append(createWhiteMainLine(), true);
		path.closePath();
		return path;
	}

	/** Draw the skip stripes */
	protected void drawSkipStripes(Graphics2D g) {
		R_NodeType nt =R_NodeType.fromOrdinal(getProxy().getNodeType());
		g.setColor(Color.WHITE);
		g.setStroke(LINE_DASHED);
		if(model.hasMainline())
			drawMainlineSkipStripes(g);
		if(nt == R_NodeType.ENTRANCE) {
			for(int lane = 1; lane < getProxy().getLanes(); lane++)
				g.draw(createEntranceRamp(lane, true));
		}
		if(nt == R_NodeType.EXIT) {
			for(int lane = 1; lane < getProxy().getLanes(); lane++)
				g.draw(createExitRamp(lane, true));
		}
	}

	/** Draw the mainline skip stripes */
	protected void drawMainlineSkipStripes(Graphics2D g) {
		int left0 = getDownstreamLine(true);
		int left1 = getUpstreamLine(true);
		int right0 = getDownstreamLine(false);
		int right1 = getUpstreamLine(false);
		int left = Math.max(left0, left1);
		if(left0 == left1)
			left += LANE_WIDTH;
		int right = Math.min(right0, right1);
		if(right0 == right1)
			right -= LANE_WIDTH;
		for(int i = left; i <= right; i += LANE_WIDTH)
			g.draw(new Line2D.Double(i, 0, i, height));
	}

	/** Create a ramp curve for the specified lane
	 * @param lane Number of lanes from the outside lane */
	protected Shape createRamp(int lane, boolean reverse, int x, int y0,
		int y1, int y2, int y3)
	{
		int x1, x2, x3;
		if(getProxy().getAttachSide()) {
			x1 = x - LANE_WIDTH * 3;
			x2 = x - LANE_WIDTH;
			x3 = x + LANE_WIDTH * lane;
		} else {
			x1 = x + LANE_WIDTH * 3;
			x2 = x + LANE_WIDTH;
			x3 = x - LANE_WIDTH * lane;
		}
		R_NodeTransition nt = R_NodeTransition.fromOrdinal(
			getProxy().getTransition());
		GeneralPath path = new GeneralPath();
		if(reverse) {
			if(nt == R_NodeTransition.LOOP) {
				path.moveTo(x3, y3);
				path.curveTo(x3, y0, x1, y1, x1, y2);
			} else {
				path.moveTo(x3, y3);
				path.curveTo(x3, y1, x2, y1, x1, y1);
			}
		} else {
			if(nt == R_NodeTransition.LOOP) {
				path.moveTo(x1, y2);
				path.curveTo(x1, y1, x3, y0, x3, y3);
			} else {
				path.moveTo(x1, y1);
				path.curveTo(x2, y1, x3, y1, x3, y3);
			}
		}
		return path;
	}

	/** Get the y-position of the specified ramp lane */
	protected int getRampLaneY(int lane) {
		return LANE_HEIGHT / 2 + LANE_HEIGHT * (getProxy().getLanes()
			- lane);
	}

	/** Create a ramp curve for the specified lane
	 * @param lane Number of lanes from the outside lane */
	protected Shape createEntranceRamp(int lane, boolean reverse) {
		int x = getDownstreamLine(getProxy().getAttachSide());
		int y = getPreferredHeight() - getRampLaneY(lane);
		int y0 = y + LANE_HEIGHT;
		int y1 = y;
		int y2 = y - LANE_HEIGHT / 2;
		int y3 = 0;
		return createRamp(lane, reverse, x, y0, y1, y2, y3);
	}

	/** Create the yellow (left side) fog line for an entrance ramp */
	protected Shape createEntranceYellow() {
		if(getProxy().getAttachSide())
			return createEntranceRamp(0, false);
		else
			return createEntranceRamp(getProxy().getLanes(), false);
	}

	/** Create the white (right side) fog line for an entrance ramp */
	protected Shape createEntranceWhite() {
		if(getProxy().getAttachSide())
			return createEntranceRamp(getProxy().getLanes(), true);
		else
			return createEntranceRamp(0, true);
	}

	/** Create an entrance roadway area */
	protected Shape createEntranceRoadway() {
		GeneralPath path = new GeneralPath(createEntranceYellow());
		path.append(createEntranceWhite(), true);
		path.closePath();
		return path;
	}

	/** Create a ramp curve for the specified lane
	 * @param lane Number of lanes from the outside lane */
	protected Shape createExitRamp(int lane, boolean reverse) {
		int x = getUpstreamLine(getProxy().getAttachSide());
		int y = getRampLaneY(lane);
		int y0 = y - LANE_HEIGHT;
		int y1 = y;
		int y2 = y + LANE_HEIGHT / 2;
		int y3 = getPreferredHeight();
		return createRamp(lane, reverse, x, y0, y1, y2, y3);
	}

	/** Create the yellow (left side) fog line for an exit ramp */
	protected Shape createExitYellow() {
		if(getProxy().getAttachSide())
			return createExitRamp(0, false);
		else
			return createExitRamp(getProxy().getLanes(), false);
	}

	/** Create the white (right side) fog line for an exit ramp */
	protected Shape createExitWhite() {
		if(getProxy().getAttachSide())
			return createExitRamp(getProxy().getLanes(), true);
		else
			return createExitRamp(0, true);
	}

	/** Create an exit roadway area */
	protected Shape createExitRoadway() {
		GeneralPath path = new GeneralPath(createExitRamp(0, false));
		path.append(createExitRamp(getProxy().getLanes(), true), true);
		path.closePath();
		return path;
	}

	/** Draw the detector locations */
	protected void drawDetectors(Graphics2D g) {
		g.setStroke(LINE_BASIC);
		switch(R_NodeType.fromOrdinal(getProxy().getNodeType())) {
			case STATION:
				drawStationDetectors(g);
				break;
			case ENTRANCE:
				drawEntranceDetectors(g);
				break;
		}
	}

	/** Draw station detector locations */
	protected void drawStationDetectors(Graphics2D g) {
		final int y = 2;
		int r = getDownstreamLine(false) - LANE_WIDTH + 4;
		for(int i = 0; i < getProxy().getLanes(); i++) {
			int x = r - LANE_WIDTH * i;
			drawDetector(g, x, y, Integer.toString(i + 1));
		}
	}

	/** Get X position to draw an HOV diamond */
	protected int getHovDiamondX() {
		boolean side = getProxy().getAttachSide();
		int x = getDownstreamLine(side);
		if(side)
			return x - LANE_WIDTH * 2;
		else
			return x + LANE_WIDTH;
	}

	/** Draw entrance detectors stuff */
	protected void drawEntranceDetectors(Graphics2D g) {
		R_NodeTransition nt = R_NodeTransition.fromOrdinal(
			getProxy().getTransition());
		if(nt == R_NodeTransition.HOV) {
			int x = getHovDiamondX();
			int y = height - LANE_HEIGHT - 1;
			GeneralPath path = new GeneralPath();
			path.moveTo(x, y);
			path.lineTo(x + LANE_WIDTH / 2, y + LANE_HEIGHT / 3);
			path.lineTo(x + LANE_WIDTH, y);
			path.lineTo(x + LANE_WIDTH / 2, y - LANE_HEIGHT / 3);
			path.closePath();
			g.setStroke(LINE_BASIC2);
			g.draw(path);
		}
	}

	/** Draw a detector */
	protected void drawDetector(Graphics2D g, int x, int y, String label) {
		Rectangle2D detector = new Rectangle2D.Double(x, y,
			DET_WIDTH, DET_HEIGHT);
		g.setColor(Color.DARK_GRAY);
		g.fill(detector);
		g.setColor(Color.WHITE);
		g.draw(detector);
		GlyphVector gv = FONT_XSTREET.createGlyphVector(
			g.getFontRenderContext(), label);
		Rectangle2D rect = gv.getVisualBounds();
		int tx = (DET_WIDTH - (int)rect.getWidth()) / 2;
		int ty = 1 + (DET_HEIGHT + (int)rect.getHeight()) / 2;
		g.drawGlyphVector(gv, x + tx, y + ty);
	}

	/** Draw the cross-street label */
	protected void drawCrossStreet(Graphics2D g, String xStreet) {
		GlyphVector gv = FONT_XSTREET.createGlyphVector(
			g.getFontRenderContext(), xStreet);
		Rectangle2D rect = gv.getVisualBounds();
		int x = WIDTH - (int)rect.getWidth() - 4;
		int y = (height + (int)rect.getHeight()) / 2;
		g.setColor(Color.BLACK);
		g.drawGlyphVector(gv, x, y);
	}

	/** Get the absolute change in the fog line lane for the given side */
	protected int getFogLaneDelta(boolean side) {
		int up = model.getUpstreamLane(side);
		int down = model.getDownstreamLane(side);
		return Math.abs(up - down);
	}

	/** Get the preferred height of a station node */
	protected int getPreferredStationHeight() {
		int delta = Math.max(getFogLaneDelta(false),
			getFogLaneDelta(true));
		return LANE_HEIGHT * (delta + 1);
	}

	/** Get the preferred height */
	protected int getPreferredHeight() {
		switch(R_NodeType.fromOrdinal(getProxy().getNodeType())) {
			case ENTRANCE:
			case EXIT:
				return LANE_HEIGHT * (getProxy().getLanes() +2);
			case STATION:
				return getPreferredStationHeight();
		}
		return LANE_HEIGHT;
	}

	/** Get the preferred renderer size */
	public Dimension getPreferredSize() {
		return new Dimension(WIDTH, getPreferredHeight());
	}
}
