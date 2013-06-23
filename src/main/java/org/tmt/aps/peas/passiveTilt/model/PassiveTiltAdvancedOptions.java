package org.tmt.aps.peas.passiveTilt.model;


public class PassiveTiltAdvancedOptions {
		
	

	private int calculationOptions;
	private int frameScaleRotationRemoval;
	private int autoCenterTelescope;
	private int autoCenterPupil;
	private int autoCenterPupilMechanism;
	private int autoSendActuatorDeltas;
	private int takeRefBeamAutomatically;
	public int getCalculationOptions() {
		return calculationOptions;
	}
	public void setCalculationOptions(int calculationOptions) {
		this.calculationOptions = calculationOptions;
	}
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
	public int getAutoSendActuatorDeltas() {
		return autoSendActuatorDeltas;
	}
	public void setAutoSendActuatorDeltas(int autoSendActuatorDeltas) {
		this.autoSendActuatorDeltas = autoSendActuatorDeltas;
	}
	public int getTakeRefBeamAutomatically() {
		return takeRefBeamAutomatically;
	}
	public void setTakeRefBeamAutomatically(int takeRefBeamAutomatically) {
		this.takeRefBeamAutomatically = takeRefBeamAutomatically;
	}
	 	


	
	
}
