/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

/**
 * @Deprecated
 * @author smichaels
 */
public class KnifeEdge {

	public static final int POSITION_COMMAND_TYPE_HOME = 1;
	public static final int POSITION_COMMAND_TYPE_POSITION = 2;

	public static final int RATE_COMMAND_TYPE_STOP = 1;
	public static final int RATE_COMMAND_TYPE_RATE = 2;

	private int positionCommand;
	private int rateCommand;
	private float position;
	private float rate;

	public KnifeEdge(int positionCommand, float position, int rateCommand, float rate) {
		this.positionCommand = positionCommand;
		this.position = position;
		this.rateCommand = rateCommand;
		this.rate = rate;
	}



	public int getPositionCommand() {
		return positionCommand;
	}

	public void setPositionCommand(int positionCommand) {
		this.positionCommand = positionCommand;
	}

	public int getRateCommand() {
		return rateCommand;
	}

	public void setRateCommand(int rateCommand) {
		this.rateCommand = rateCommand;
	}

	public float getPosition() {
		return position;
	}

	public void setPosition(float position) {
		this.position = position;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}



	public String getPositionDisplayString() {
		switch (positionCommand) {
		case POSITION_COMMAND_TYPE_HOME:
			return "Home Knife";
		case POSITION_COMMAND_TYPE_POSITION:
			return "Position Knife";
		}
		return "";
	}

	public String getRateDisplayString() {
		switch (rateCommand) {
		case RATE_COMMAND_TYPE_STOP:
			return "Stop Knife";
		case RATE_COMMAND_TYPE_RATE:
			return "Set to Rate";
		}
		return "";
	}

}
