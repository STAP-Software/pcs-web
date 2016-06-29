package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for <b>bbAnalyzeFrame</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#bbAnalyzeFrame(float[][], FindCentroidsResult, int[], float[][][][], int)
 */
public class BbAnalyzeFrameResult {

	private float[] coherenceArray;
	private float[] bestCorrelationIndex;

	public BbAnalyzeFrameResult() {
		
	}
	
	public BbAnalyzeFrameResult(float[] coherenceArray, float[] bestCorrelationIndex) {
		this.coherenceArray = coherenceArray;
		this.bestCorrelationIndex = bestCorrelationIndex;
	}

	public float[] getCoherenceArray() {
		return coherenceArray;
	}

	public void setCoherenceArray(float[] coherenceArray) {
		this.coherenceArray = coherenceArray;
	}

	public float[] getBestCorrelationIndex() {
		return bestCorrelationIndex;
	}

	public void setBestCorrelationIndex(float[] bestCorrelationIndex) {
		this.bestCorrelationIndex = bestCorrelationIndex;
	}

	


	
}
