package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;

public class CalcM2PttErrorsMeanEomResult {

	float meanM2PistonError;
	FloatPoint meanM2TipTiltError;
	
	float eomM2PistonError;
	FloatPoint eomM2TipTiltError;
	
	public CalcM2PttErrorsMeanEomResult(float meanM2PistonError, FloatPoint meanM2TipTiltError, float eomM2PistonError, FloatPoint eomM2TipTiltError) {
		this.meanM2PistonError = meanM2PistonError;
		this.meanM2TipTiltError = meanM2TipTiltError;
		this.eomM2PistonError = eomM2PistonError; 
		this.eomM2TipTiltError = eomM2TipTiltError;
	}
	
	public CalcM2PttErrorsMeanEomResult() {}

	public float getMeanM2PistonError() {
		return meanM2PistonError;
	}

	public void setMeanM2PistonError(float meanM2PistonError) {
		this.meanM2PistonError = meanM2PistonError;
	}

	public FloatPoint getMeanM2TipTiltError() {
		return meanM2TipTiltError;
	}

	public void setMeanM2TipTiltError(FloatPoint meanM2TipTiltError) {
		this.meanM2TipTiltError = meanM2TipTiltError;
	}

	public float getEomM2PistonError() {
		return eomM2PistonError;
	}

	public void setEomM2PistonError(float eomM2PistonError) {
		this.eomM2PistonError = eomM2PistonError;
	}

	public FloatPoint getEomM2TipTiltError() {
		return eomM2TipTiltError;
	}

	public void setEomM2TipTiltError(FloatPoint eomM2TipTiltError) {
		this.eomM2TipTiltError = eomM2TipTiltError;
	}
	
	
	// Display/Storage methods
	
	public float getMeanM2PistonErrorUm() {
		return meanM2PistonError * Constants.METERS_TO_UM;
	}

	public void setMeanM2PistonErrorUm(float meanM2PistonErrorUm) {
		this.meanM2PistonError = meanM2PistonErrorUm/Constants.METERS_TO_UM;
	}

	public FloatPoint getMeanM2TipTiltErrorArcsec() {
		
		return new FloatPoint(meanM2TipTiltError.x * Constants.RADIANS_TO_ARCSEC, meanM2TipTiltError.y * Constants.RADIANS_TO_ARCSEC);
	}

	public void setMeanM2TipTiltErrorArcsec(FloatPoint meanM2TipTiltErrorArcsec) {
		this.meanM2TipTiltError = new FloatPoint(meanM2TipTiltErrorArcsec.x / Constants.RADIANS_TO_ARCSEC, meanM2TipTiltErrorArcsec.y / Constants.RADIANS_TO_ARCSEC);
	}

	public float getEomM2PistonErrorUm() {
		return eomM2PistonError  * Constants.METERS_TO_UM;
	}

	public void setEomM2PistonErrorUm(float eomM2PistonErrorUm) {
		this.eomM2PistonError = eomM2PistonErrorUm / Constants.METERS_TO_UM;
	}

	public FloatPoint getEomM2TipTiltErrorArcsec() {
		return new FloatPoint(eomM2TipTiltError.x * Constants.RADIANS_TO_ARCSEC, eomM2TipTiltError.y * Constants.RADIANS_TO_ARCSEC);
	}

	public void setEomM2TipTiltErrorArcsec(FloatPoint eomM2TipTiltErrorArcsec) {
		this.eomM2TipTiltError = new FloatPoint(eomM2TipTiltErrorArcsec.x / Constants.RADIANS_TO_ARCSEC, eomM2TipTiltErrorArcsec.y / Constants.RADIANS_TO_ARCSEC);
	}
	
	
	
	
	
}
