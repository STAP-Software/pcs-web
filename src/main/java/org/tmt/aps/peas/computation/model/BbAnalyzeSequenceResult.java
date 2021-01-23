package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for <b>bbAnalyzeSequence</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#bbAnalyzeSequence(int[], int[], float[][], float, int, int[], int[], float, org.tmt.aps.peas.instrument.model.Filter, float, float[], int, int[], int[])
 */
public class BbAnalyzeSequenceResult {

	float[] stepCorr;
	float[] actCalc;
	float[] resid;
	int[] rowFlagIn;
	int[] rowFlagOut;
	float[] bestFitCoherences;
	
	int  constrainedSegmentCount;
	float segmentPistonRms; 
	float meanBestFitCoherence;
	int numberOfIslands;
	int[] segmentIslandNumber;
	int[] islandSegmentCount;


	
	public BbAnalyzeSequenceResult() {
		
	}
	
	public BbAnalyzeSequenceResult(float[] stepCorr, float[] actCalc, float[] resid, int[] rowFlagIn, int[] rowFlagOut, int constrainedSegmentCount, float segmentPistonRms, 
			float[] bestFitCoherences, float meanBestFitCoherence, int numberOfIslands, int[] segmentIslandNumber, int[] islandSegmentCount) {
		this.stepCorr = stepCorr;
		this.actCalc = actCalc;
		this.resid = resid;
		this.rowFlagIn = rowFlagIn;
		this.rowFlagOut = rowFlagOut;
		this.constrainedSegmentCount = constrainedSegmentCount;
		this.segmentPistonRms = segmentPistonRms;
		this.bestFitCoherences = bestFitCoherences;
		this.meanBestFitCoherence = meanBestFitCoherence;
		this.numberOfIslands = numberOfIslands;
		this.segmentIslandNumber = segmentIslandNumber;
		this.islandSegmentCount = islandSegmentCount;

	}

	public float[] getStepCorr() {
		return stepCorr;
	}

	public void setStepCorr(float[] stepCorr) {
		this.stepCorr = stepCorr;
	}

	public float[] getActCalc() {
		return actCalc;
	}

	public void setActCalc(float[] actCalc) {
		this.actCalc = actCalc;
	}

	public float[] getResid() {
		return resid;
	}

	public void setResid(float[] resid) {
		this.resid = resid;
	}

	public int[] getRowFlagIn() {
		return rowFlagIn;
	}

	public void setRowFlagIn(int[] rowFlagIn) {
		this.rowFlagIn = rowFlagIn;
	}

	public int[] getRowFlagOut() {
		return rowFlagOut;
	}

	public void setRowFlagOut(int[] rowFlagOut) {
		this.rowFlagOut = rowFlagOut;
	}

	public int getConstrainedSegmentCount() {
		return constrainedSegmentCount;
	}

	public void setConstrainedSegmentCount(int constrainedSegmentCount) {
		this.constrainedSegmentCount = constrainedSegmentCount;
	}

	public float getSegmentPistonRms() {
		return segmentPistonRms;
	}

	public void setSegmentPistonRms(float segmentPistonRms) {
		this.segmentPistonRms = segmentPistonRms;
	}

	public float[] getBestFitCoherences() {
		return bestFitCoherences;
	}

	public void setBestFitCoherences(float[] bestFitCoherences) {
		this.bestFitCoherences = bestFitCoherences;
	}

	public float getMeanBestFitCoherence() {
		return meanBestFitCoherence;
	}

	public void setMeanBestFitCoherence(float meanBestFitCoherence) {
		this.meanBestFitCoherence = meanBestFitCoherence;
	}

	public int getNumberOfIslands() {
		return numberOfIslands;
	}

	public void setNumberOfIslands(int numberOfIslands) {
		this.numberOfIslands = numberOfIslands;
	}

	public int[] getSegmentIslandNumber() {
		return segmentIslandNumber;
	}

	public void setSegmentIslandNumber(int[] segmentIslandNumber) {
		this.segmentIslandNumber = segmentIslandNumber;
	}

	public int[] getIslandSegmentCount() {
		return islandSegmentCount;
	}

	public void setIslandSegmentCount(int[] islandSegmentCount) {
		this.islandSegmentCount = islandSegmentCount;
	}


	
}
