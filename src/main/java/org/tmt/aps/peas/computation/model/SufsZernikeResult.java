package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

public class SufsZernikeResult {
	
	float[] bestFitZernikes;
	FloatPoint[] theoreticalOffsets;
	float whFactor;
	
	public SufsZernikeResult() {
		
	}
	
	public SufsZernikeResult(float[] bestFitZernikes, FloatPoint[] theoreticalOffsets, float whFactor) {
		this.bestFitZernikes = bestFitZernikes;
		this.theoreticalOffsets = theoreticalOffsets;
		this.whFactor = whFactor;
	}

	public float[] getBestFitZernikes() {
		return bestFitZernikes;
	}

	public void setBestFitZernikes(float[] bestFitZernikes) {
		this.bestFitZernikes = bestFitZernikes;
	}

	public FloatPoint[] getTheoreticalOffsets() {
		return theoreticalOffsets;
	}

	public void setTheoreticalOffsets(FloatPoint[] theoreticalOffsets) {
		this.theoreticalOffsets = theoreticalOffsets;
	}

	public float getWhFactor() {
		return whFactor;
	}

	public void setWhFactor(float whFactor) {
		this.whFactor = whFactor;
	}

	
}
