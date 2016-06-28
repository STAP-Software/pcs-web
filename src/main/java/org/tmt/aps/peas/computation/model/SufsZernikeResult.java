package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Computation data intermediate result class for SufsSegmentZernikeResult computation.  Contains Zernike calculation results for a single segment.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.model.SufsSegmentZernikeResult
 */
public class SufsZernikeResult {
	
	
	private static final int MAX_NUMBER_OF_ZERNIKES = 45;

	float[] bestFitZernikes;
	FloatPoint[] theoreticalOffsets;
	float whFactor;
	
	public SufsZernikeResult() {
		
	}
	
	public SufsZernikeResult(float[] bestFitZernikes, FloatPoint[] theoreticalOffsets, float whFactor) {
		this.bestFitZernikes = new float[MAX_NUMBER_OF_ZERNIKES];
		System.arraycopy(bestFitZernikes, 0, this.bestFitZernikes, 0, bestFitZernikes.length);
		
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
