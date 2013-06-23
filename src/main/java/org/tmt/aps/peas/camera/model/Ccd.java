package org.tmt.aps.peas.camera.model;

public class Ccd {

	public static final int POWER_STATE_ON = 1;
	public static final int POWER_STATE_OFF = 2;

	private int state;
	private float temperature;

	public Ccd(int state, float temperature) {
		this.state = state;
		this.temperature = temperature;
	}


	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	
	public String getDisplayString() {
		switch (state) {
		case POWER_STATE_ON:
			return "On";
		case POWER_STATE_OFF:
			return "Off";
		}
		return "";
	}

}
