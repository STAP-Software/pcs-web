package org.tmt.aps.peas.instrument.model;

public class Shutter {

	public static final int STATE_OPEN = 1;
	public static final int STATE_CLOSE = 2;
	public static final int STATE_TIMED_EXPOSURE = 3;

	private int state;
	private float exposureTime;

	public Shutter(int state, float exposureTime) {
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

	public String getShutterDisplayString() {
		switch (state) {
		case STATE_OPEN:
			return "Open";
		case STATE_CLOSE:
			return "Closed";
		case STATE_TIMED_EXPOSURE:
			return "Timed Exposure";
		}
		return "";
	}

}
