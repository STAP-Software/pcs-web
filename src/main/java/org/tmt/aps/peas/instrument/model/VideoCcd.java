package org.tmt.aps.peas.instrument.model;

public class VideoCcd {

	public static final int POWER_STATE_ON = 1;
	public static final int POWER_STATE_OFF = 2;

	private int state;
	private float exposureTime;

	public VideoCcd(int state, float exposureTime) {
		this.state = state;
		this.exposureTime = exposureTime;
	}



	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public float getExposureTime() {
		return exposureTime;
	}

	public void setExposureTime(float exposureTime) {
		this.exposureTime = exposureTime;
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
