package org.tmt.aps.peas.passiveTilt.model;

public class PassiveTiltWorkflowPreferences {

	private boolean frameScaleRotationRemoval;
	private boolean autoCenterTelescope;
	private boolean autoCenterPupil;
	private boolean autoCenterPupilMechanism;
	private boolean autoSendActuatorDeltas;
	private boolean takeRefBeamAutomatically;
	private boolean autoSaveFrames;

	public boolean isFrameScaleRotationRemoval() {
		return frameScaleRotationRemoval;
	}

	public void setFrameScaleRotationRemoval(boolean frameScaleRotationRemoval) {
		this.frameScaleRotationRemoval = frameScaleRotationRemoval;
	}

	public boolean isAutoCenterTelescope() {
		return autoCenterTelescope;
	}

	public void setAutoCenterTelescope(boolean autoCenterTelescope) {
		this.autoCenterTelescope = autoCenterTelescope;
	}

	public boolean isAutoCenterPupil() {
		return autoCenterPupil;
	}

	public void setAutoCenterPupil(boolean autoCenterPupil) {
		this.autoCenterPupil = autoCenterPupil;
	}

	public boolean isAutoCenterPupilMechanism() {
		return autoCenterPupilMechanism;
	}

	public void setAutoCenterPupilMechanism(boolean autoCenterPupilMechanism) {
		this.autoCenterPupilMechanism = autoCenterPupilMechanism;
	}

	public boolean isAutoSendActuatorDeltas() {
		return autoSendActuatorDeltas;
	}

	public void setAutoSendActuatorDeltas(boolean autoSendActuatorDeltas) {
		this.autoSendActuatorDeltas = autoSendActuatorDeltas;
	}

	public boolean isTakeRefBeamAutomatically() {
		return takeRefBeamAutomatically;
	}

	public void setTakeRefBeamAutomatically(boolean takeRefBeamAutomatically) {
		this.takeRefBeamAutomatically = takeRefBeamAutomatically;
	}

	public boolean isAutoSaveFrames() {
		return autoSaveFrames;
	}

	public void setAutoSaveFrames(boolean autoSaveFrames) {
		this.autoSaveFrames = autoSaveFrames;
	}

}
