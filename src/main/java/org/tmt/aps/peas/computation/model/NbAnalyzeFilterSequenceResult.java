package org.tmt.aps.peas.computation.model;

public class NbAnalyzeFilterSequenceResult {

	float[] chi2nm;
	float[] nbStep;
	int[] rowFlagOut;
	
	public NbAnalyzeFilterSequenceResult() {
		
	}

	public NbAnalyzeFilterSequenceResult(float[] chi2nm, float[] nbStep, int[] rowFlagOut) {
		this.chi2nm = chi2nm;
		this.nbStep = nbStep;
		this.rowFlagOut = rowFlagOut;
	}

	public float[] getChi2nm() {
		return chi2nm;
	}

	public void setChi2nm(float[] chi2nm) {
		this.chi2nm = chi2nm;
	}

	public float[] getNbStep() {
		return nbStep;
	}

	public void setNbStep(float[] nbStep) {
		this.nbStep = nbStep;
	}

	public int[] getRowFlagOut() {
		return rowFlagOut;
	}

	public void setRowFlagOut(int[] rowFlagOut) {
		this.rowFlagOut = rowFlagOut;
	}

	
	
}
