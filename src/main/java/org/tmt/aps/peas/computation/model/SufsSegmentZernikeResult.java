package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Computation data result class for <b>calculateSufsZernikes</b> computation.  Contains Zernikes for each segment in the SUFS group.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculateSufsZernikes(FloatPoint[], FloatPoint[][], float, float, int[][], int[][], int[], int[])
 */
public class SufsSegmentZernikeResult {
	float[][] bestFitZernikes;
	FloatPoint[][] theoreticalOffsets;
	float[] whFactor;
	
	public SufsSegmentZernikeResult() {
		
	}
	/**
	 * Constructor using array of segment sufsZernikeResults
	 * @param sufsZernikeResults array of sufsZernikeResult objects, one for each segment
	 */
	public SufsSegmentZernikeResult(SufsZernikeResult[] sufsZernikeResults) {
				
		int bestFitCount = sufsZernikeResults[0].getBestFitZernikes().length;
		int theoreticalCount = sufsZernikeResults[0].getTheoreticalOffsets().length;
			
		whFactor = new float[sufsZernikeResults.length];
		bestFitZernikes = new float[sufsZernikeResults.length][bestFitCount];
		theoreticalOffsets = new FloatPoint[sufsZernikeResults.length][theoreticalCount];
		
		for (int i=0; i<sufsZernikeResults.length; i++) {
			whFactor[i] = sufsZernikeResults[i].getWhFactor();
			bestFitZernikes[i] = sufsZernikeResults[i].getBestFitZernikes();
			theoreticalOffsets[i] = sufsZernikeResults[i].getTheoreticalOffsets();
		}
	}

	/**
	 * 2-d segment indexed array of best fit array of Zernikes for a segment
	 */
	public float[][] getBestFitZernikes() {
		return bestFitZernikes;
	}

	/**
	 * 2-d segment indexed array of best fit array of Zernikes for a segment
	 */
	public void setBestFitZernikes(float[][] bestFitZernikes) {
		this.bestFitZernikes = bestFitZernikes;
	}

	/**
	 * 2-d segment indexed array of FloatPoint array of theoretical offsets for a segment
	 */
	public FloatPoint[][] getTheoreticalOffsets() {
		return theoreticalOffsets;
	}

	/**
	 * 2-d segment indexed array of FloatPoint array of theoretical offsets for a segment
	 */
	public void setTheoreticalOffsets(FloatPoint[][] theoreticalOffsets) {
		this.theoreticalOffsets = theoreticalOffsets;
	}

	/**
	 * segment indexed array of whFactors
	 */
	public float[] getWhFactor() {
		return whFactor;
	}

	/**
	 * segment indexed array of whFactors
	 */
	public void setWhFactor(float[] whFactor) {
		this.whFactor = whFactor;
	}


}
