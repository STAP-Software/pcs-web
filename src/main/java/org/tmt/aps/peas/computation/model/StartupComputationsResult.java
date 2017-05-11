package org.tmt.aps.peas.computation.model;

public class StartupComputationsResult {

	float arcsecPerPixel;
	
	public StartupComputationsResult(float arcsecPerPixel) {
		this.arcsecPerPixel = arcsecPerPixel;
	}
	
	public StartupComputationsResult() {}
	
	public float getArcsecPerPixel() {
		return arcsecPerPixel;
	}
	
	public void setArcsecPerPixel(float arcsecPerPixel) {
		this.arcsecPerPixel = arcsecPerPixel;
	}
	
	

}
