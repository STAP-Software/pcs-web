package org.tmt.aps.peas.computation.model;

public class BbAnalyzeFrameResult {

	private float[] coherenceArray;

	public BbAnalyzeFrameResult() {
		
	}
	
	public BbAnalyzeFrameResult(float[] coherenceArray) {
		this.coherenceArray = coherenceArray;
	}

	public float[] getCoherenceArray() {
		return coherenceArray;
	}

	public void setCoherenceArray(float[] coherenceArray) {
		this.coherenceArray = coherenceArray;
	}



	
}
