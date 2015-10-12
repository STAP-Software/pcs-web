package org.tmt.aps.peas.computation.model;

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
	
	
}
