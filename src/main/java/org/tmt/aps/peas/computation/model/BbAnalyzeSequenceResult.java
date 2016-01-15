package org.tmt.aps.peas.computation.model;

public class BbAnalyzeSequenceResult {

	float[] stepCorr;
	float[] actCalc;
	float[] resid;
	int[] rowFlagIn;
	int[] rowFlagOut;

	public BbAnalyzeSequenceResult() {
		
	}
	
	public BbAnalyzeSequenceResult(float[] stepCorr, float[] actCalc, float[] resid, int[] rowFlagIn, int[] rowFlagOut) {
		this.stepCorr = stepCorr;
		this.actCalc = actCalc;
		this.resid = resid;
		this.rowFlagIn = rowFlagIn;
		this.rowFlagOut = rowFlagOut;
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




	
}
