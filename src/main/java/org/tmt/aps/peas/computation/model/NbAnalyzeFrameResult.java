package org.tmt.aps.peas.computation.model;

public class NbAnalyzeFrameResult {

    float[] coherenceOut;
    float[] bestCorrelationIndex;
    float[] aFit;
    float[] bFit;
    float[] phiFit;
    float[] chisqF;

    public NbAnalyzeFrameResult() {
    	
    }
    
	public NbAnalyzeFrameResult(float[] coherenceOut, float[] bestCorrelationIndex, float[] aFit, float[] bFit, float[] phiFit, float[] chisqF) {
		this.coherenceOut = coherenceOut;
		this.bestCorrelationIndex = bestCorrelationIndex;
		this.aFit = aFit;
		this.bFit = bFit;
		this.phiFit = phiFit;
		this.chisqF = chisqF;
	}

	public float[] getCoherenceOut() {
		return coherenceOut;
	}

	public void setCoherenceOut(float[] coherenceOut) {
		this.coherenceOut = coherenceOut;
	}

	public float[] getBestCorrelationIndex() {
		return bestCorrelationIndex;
	}

	public void setBestCorrelationIndex(float[] bestCorrelationIndex) {
		this.bestCorrelationIndex = bestCorrelationIndex;
	}

	public float[] getaFit() {
		return aFit;
	}

	public void setaFit(float[] aFit) {
		this.aFit = aFit;
	}

	public float[] getbFit() {
		return bFit;
	}

	public void setbFit(float[] bFit) {
		this.bFit = bFit;
	}

	public float[] getPhiFit() {
		return phiFit;
	}

	public void setPhiFit(float[] phiFit) {
		this.phiFit = phiFit;
	}

	public float[] getChisqF() {
		return chisqF;
	}

	public void setChisqF(float[] chisqF) {
		this.chisqF = chisqF;
	}
	
	
}
