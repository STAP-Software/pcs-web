/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

public class TwoPosMechanism {

	public static final int TWO_POS_MECH_STATE_EXTEND = 1;
	public static final int TWO_POS_MECH_STATE_RETRACT = 2;

	private int state;

	public TwoPosMechanism(int state) {
		this.state = state;
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
		}
		return "";
	}

}
