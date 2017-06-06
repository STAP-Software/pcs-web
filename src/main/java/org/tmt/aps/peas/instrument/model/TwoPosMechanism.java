/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

/**
 * Data class representing the two position mechanism state
 * @author smichaels
 */
public class TwoPosMechanism {

	public static final int TWO_POS_MECH_STATE_EXTEND = 1;
	public static final int TWO_POS_MECH_STATE_RETRACT = 2;
	public static final int TWO_POS_MECH_STATE_IN_TRANSIT = 3;

	private int state;

	public TwoPosMechanism(int state) {
		this.state = state;
	}

	public TwoPosMechanism() {
	}


	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDisplayString() {
		switch (state) {
		case TWO_POS_MECH_STATE_EXTEND:
			return "Extended";
		case TWO_POS_MECH_STATE_RETRACT:
			return "Retracted";
		case TWO_POS_MECH_STATE_IN_TRANSIT:
			return "In Transit";
		}
		return "";
	}

	public String getRawStateDisplayString() {
		if (state > 3 || state < 1) {
			return "Unknown";
		} else {
			return (state == TWO_POS_MECH_STATE_IN_TRANSIT) ? "In Transit" : "In Position";
		}

	}
}
