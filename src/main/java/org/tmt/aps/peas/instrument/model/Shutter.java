/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

/**
 * Data class representing the shutter state
 * @author smichaels
 */
public class Shutter {

	public static final int STATE_OPEN = 1;
	public static final int STATE_CLOSE = 2;
	public static final int STATE_TIMED_EXPOSURE = 3;
	public static final int STATE_IN_TRANSIT = 4;

	private int state;

	
	public Shutter() {
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getShutterDisplayString() {
		switch (state) {
		case STATE_OPEN:
			return "Open";
		case STATE_CLOSE:
			return "Closed";
		case STATE_TIMED_EXPOSURE:
			return "Timed Exposure";
		case STATE_IN_TRANSIT:
			return "In Transit";
		}
		return "";
	}

}
