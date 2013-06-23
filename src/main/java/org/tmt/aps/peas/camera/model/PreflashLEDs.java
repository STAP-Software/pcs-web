package org.tmt.aps.peas.camera.model;

public class PreflashLEDs {

	public static final int STATE_ON = 1;
	public static final int STATE_OFF = 2;
	public static final int STATE_TIMED_FLASH = 3;

	private int state;
	private float flashDuration;

	public PreflashLEDs(int state, float flashDuration) {
		this.state = state;
		this.flashDuration = flashDuration;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public float getFlashDuration() {
		return flashDuration;
	}

	public void setFlashDuration(float flashDuration) {
		this.flashDuration = flashDuration;
	}

	public String getDisplayString() {
		switch (state) {
		case STATE_ON:
			return "On";
		case STATE_OFF:
			return "Off";
		case STATE_TIMED_FLASH:
			return "Timed Flash";
		}
		return "";
	}

}
