package org.tmt.aps.peas.session.model;

public class ExtInfConnectConfig {

	
	private boolean camera;
	private boolean ccd;
	private boolean acs;
	private boolean dcs;
	
	
	public ExtInfConnectConfig() {
		reset();
	}
	
	public void reset() {
		
		this.camera = false;
		this.ccd = false;
		this.acs = false;
		this.dcs = false;
	}
	
	
	public boolean isCamera() {
		return camera;
	}
	public void setCamera(boolean camera) {
		this.camera = camera;
	}
	public boolean isCcd() {
		return ccd;
	}
	public void setCcd(boolean ccd) {
		this.ccd = ccd;
	}
	public boolean isAcs() {
		return acs;
	}
	public void setAcs(boolean acs) {
		this.acs = acs;
	}
	public boolean isDcs() {
		return dcs;
	}
	public void setDcs(boolean dcs) {
		this.dcs = dcs;
	}
	
	
}
