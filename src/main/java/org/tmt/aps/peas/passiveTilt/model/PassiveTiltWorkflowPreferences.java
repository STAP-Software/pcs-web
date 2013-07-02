package org.tmt.aps.peas.passiveTilt.model;

public class PassiveTiltWorkflowPreferences {

	private int frameScaleRotationRemoval;
	private int autoCenterTelescope;
	private int autoCenterPupil;
	private int autoCenterPupilMechanism;
	private int autoSendActuatorCmds;
	private int takeRefBeamAutomatically;
	private boolean autoSaveFrames;

	public int getFrameScaleRotationRemoval() {
		return frameScaleRotationRemoval;
	}

	public void setFrameScaleRotationRemoval(int frameScaleRotationRemoval) {
		this.frameScaleRotationRemoval = frameScaleRotationRemoval;
	}

	public int getAutoCenterTelescope() {
		return autoCenterTelescope;
	}

	public void setAutoCenterTelescope(int autoCenterTelescope) {
		this.autoCenterTelescope = autoCenterTelescope;
	}

	public int getAutoCenterPupil() {
		return autoCenterPupil;
	}

	public void setAutoCenterPupil(int autoCenterPupil) {
		this.autoCenterPupil = autoCenterPupil;
	}

	public int getAutoCenterPupilMechanism() {
		return autoCenterPupilMechanism;
	}

	public void setAutoCenterPupilMechanism(int autoCenterPupilMechanism) {
		this.autoCenterPupilMechanism = autoCenterPupilMechanism;
	}


	public int getAutoSendActuatorCmds() {
		return autoSendActuatorCmds;
	}

	public void setAutoSendActuatorCmds(int autoSendActuatorCmds) {
		this.autoSendActuatorCmds = autoSendActuatorCmds;
	}

	public int getTakeRefBeamAutomatically() {
		return takeRefBeamAutomatically;
	}

	public void setTakeRefBeamAutomatically(int takeRefBeamAutomatically) {
		this.takeRefBeamAutomatically = takeRefBeamAutomatically;
	}

	public boolean isAutoSaveFrames() {
		return autoSaveFrames;
	}

	public void setAutoSaveFrames(boolean autoSaveFrames) {
		this.autoSaveFrames = autoSaveFrames;
	}

}
