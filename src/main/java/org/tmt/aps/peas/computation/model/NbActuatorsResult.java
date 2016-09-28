package org.tmt.aps.peas.computation.model;

public class NbActuatorsResult {

	float[] actCalc;
	float[] resid;
	int constrainedSegmentCount;
	float segmentPistonRms;
	
	public NbActuatorsResult() {
		
	}
	
	public NbActuatorsResult(float[] actCalc, float[] resid, int constrainedSegmentCount, float segmentPistonRms) {
		
		this.actCalc = actCalc;
		this.resid = resid;
		this.constrainedSegmentCount = constrainedSegmentCount;
		this.segmentPistonRms = segmentPistonRms;
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

}
