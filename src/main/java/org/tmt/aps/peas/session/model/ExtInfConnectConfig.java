package org.tmt.aps.peas.session.model;

/**
 * Model class containing the current state of external connection configurations
 * @author smichaels
 *
 */
public class ExtInfConnectConfig {

	
	private boolean cameraEnabled;
	private boolean ccdEnabled;
	private boolean acsEnabled;
	private boolean dcsEnabled;
	
	private boolean cameraHeartbeatStatus;
	private boolean ccdHeartbeatStatus;
	
	public ExtInfConnectConfig() {
		reset();
	}
	
	public void reset() {
		
		this.cameraEnabled = false;
		this.ccdEnabled = false;
		this.acsEnabled = false;
		this.dcsEnabled = false;
	}

	public boolean isCameraEnabled() {
		return cameraEnabled;
	}
	public void setCameraEnabled(boolean cameraEnabled) {
		this.cameraEnabled = cameraEnabled;
	}

	public boolean isCcdEnabled() {
		return ccdEnabled;
	}
	public void setCcdEnabled(boolean ccdEnabled) {
		this.ccdEnabled = ccdEnabled;
	}

	public boolean isAcsEnabled() {
		return acsEnabled;
	}
	public void setAcsEnabled(boolean acsEnabled) {
		this.acsEnabled = acsEnabled;
	}

	public boolean isDcsEnabled() {
		return dcsEnabled;
	}
	public void setDcsEnabled(boolean dcsEnabled) {
		this.dcsEnabled = dcsEnabled;
	}

	public void setCameraHeartbeatStatus(boolean cameraHeartbeatStatus) {
		this.cameraHeartbeatStatus = cameraHeartbeatStatus;
	}
	public boolean isCameraHeartbeatStatus() {
		return cameraHeartbeatStatus;
	}
	
	public boolean isCcdHeartbeatStatus() {
		return ccdHeartbeatStatus;
	}

	public void setCcdHeartbeatStatus(boolean ccdHeartbeatStatus) {
		this.ccdHeartbeatStatus = ccdHeartbeatStatus;
	}

	public String getCameraStatus() {
		if (cameraEnabled) {
			return cameraHeartbeatStatus ? "Connected" : "Communication Failure";
		}
		return cameraHeartbeatStatus ? "Disconnected" : "Disconnecting";
	}
	
	public String getCcdStatus() {
		if (ccdEnabled) {
			return ccdHeartbeatStatus ? "Connected" : "Communication Failure";
		}
		return ccdHeartbeatStatus ? "Disconnected" : "Disconnecting";

	}
	
	public String getAcsStatus() {
		return acsEnabled ? "Connected" : "Disconnected";
	}
	
	public String getDcsStatus() {
		return dcsEnabled ? "Connected" : "Disconnected";
	}


	
}
