package org.tmt.aps.peas.computation.model;

public class NbAnalyzeFilterSequenceResult {

	float[] chi2nm;
	float[] nbStep;
	
	public NbAnalyzeFilterSequenceResult(float[] chi2nm, float[] nbStep) {
		this.chi2nm = chi2nm;
		this.nbStep = nbStep;
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

	
	
}
