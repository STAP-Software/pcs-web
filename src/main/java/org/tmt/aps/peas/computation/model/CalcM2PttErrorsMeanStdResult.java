package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;

public class CalcM2PttErrorsMeanStdResult {

	float meanM2PistonError;
	FloatPoint meanM2TipTiltError;
	
	float stdM2PistonError;
	FloatPoint stdM2TipTiltError;
	
	public CalcM2PttErrorsMeanStdResult(float meanM2PistonError, FloatPoint meanM2TipTiltError, float stdM2PistonError, FloatPoint stdM2TipTiltError) {
		this.meanM2PistonError = meanM2PistonError;
		this.meanM2TipTiltError = meanM2TipTiltError;
		this.stdM2PistonError = stdM2PistonError; 
		this.stdM2TipTiltError = stdM2TipTiltError;
	}
	
	public CalcM2PttErrorsMeanStdResult() {}

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

	public float getStdM2PistonError() {
		return stdM2PistonError;
	}

	public void setStdM2PistonError(float stdM2PistonError) {
		this.stdM2PistonError = stdM2PistonError;
	}

	public FloatPoint getStdM2TipTiltError() {
		return stdM2TipTiltError;
	}

	public void setStdM2TipTiltError(FloatPoint stdM2TipTiltError) {
		this.stdM2TipTiltError = stdM2TipTiltError;
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

	public float getStdM2PistonErrorUm() {
		return stdM2PistonError  * Constants.METERS_TO_UM;
	}

	public void setStdM2PistonErrorUm(float stdM2PistonErrorUm) {
		this.stdM2PistonError = stdM2PistonErrorUm / Constants.METERS_TO_UM;
	}

	public FloatPoint getStdM2TipTiltErrorArcsec() {
		return new FloatPoint(stdM2TipTiltError.x * Constants.RADIANS_TO_ARCSEC, stdM2TipTiltError.y * Constants.RADIANS_TO_ARCSEC);
	}

	public void setStdM2TipTiltErrorArcsec(FloatPoint stdM2TipTiltErrorArcsec) {
		this.stdM2TipTiltError = new FloatPoint(stdM2TipTiltErrorArcsec.x / Constants.RADIANS_TO_ARCSEC, stdM2TipTiltErrorArcsec.y / Constants.RADIANS_TO_ARCSEC);
	}
	
	
	
	
	
}
