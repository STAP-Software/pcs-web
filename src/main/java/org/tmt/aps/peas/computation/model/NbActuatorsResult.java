package org.tmt.aps.peas.computation.model;

public class NbActuatorsResult {

	float[] actCalc;
	float[] resid;
	int constrainedSegmentCount;
	float segmentPistonRms;
	
	int numberOfIslands;
	int[] segmentIslandNumber;
	int[] islandSegmentCount;
	
	
	public NbActuatorsResult() {
		
	}
	
	public NbActuatorsResult(float[] actCalc, float[] resid, int constrainedSegmentCount, float segmentPistonRms, int numberOfIslands, 
			int[] segmentIslandNumber, int[] islandSegmentCount) {
		
		this.actCalc = actCalc;
		this.resid = resid;
		this.constrainedSegmentCount = constrainedSegmentCount;
		this.segmentPistonRms = segmentPistonRms;
		
		this.numberOfIslands = numberOfIslands;
		this.segmentIslandNumber = segmentIslandNumber;
		this.islandSegmentCount = islandSegmentCount;
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
