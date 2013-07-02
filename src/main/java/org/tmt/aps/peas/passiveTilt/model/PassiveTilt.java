package org.tmt.aps.peas.passiveTilt.model;


public class PassiveTilt {

	PassiveTiltConfig passiveTiltConfig;

	
	public PassiveTilt() {
		passiveTiltConfig = new PassiveTiltConfig();
	}
	
	
	public PassiveTiltConfig getPassiveTiltConfig() {
		return passiveTiltConfig;
	}

	public void setPassiveTiltConfig(PassiveTiltConfig passiveTiltConfig) {
		this.passiveTiltConfig = passiveTiltConfig;
	}

	

}
