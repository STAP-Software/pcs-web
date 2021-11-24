package org.tmt.aps.peas.computation.model;


/**
 * Computation data result class for <b>coherenceAnalyzer</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#coherenceAnalyzer(int, float, float)
 */
public class CoherenceAnalyzerResult {

	int numberSeeingEdges;
	float coherenceMean;
	float coherenceSeeing;
	float coherenceAsymmetry;
	float coherenceAsymmetryUncertainty;
	
	
	public CoherenceAnalyzerResult(int numberSeeingEdges, float coherenceMean, float coherenceSeeing, float coherenceAsymmetry, float coherenceAsymmetryUncertainty) {
		
		this.numberSeeingEdges = numberSeeingEdges;
		this.coherenceMean = coherenceMean;
		this.coherenceSeeing = coherenceSeeing;
		this.coherenceAsymmetry = coherenceAsymmetry;
		this.coherenceAsymmetryUncertainty = coherenceAsymmetryUncertainty;

	}

	public CoherenceAnalyzerResult() {}

	public int getNumberSeeingEdges() {
		return numberSeeingEdges;
	}

	public void setNumberSeeingEdges(int numberSeeingEdges) {
		this.numberSeeingEdges = numberSeeingEdges;
	}

	public float getCoherenceMean() {
		return coherenceMean;
	}

	public void setCoherenceMean(float coherenceMean) {
		this.coherenceMean = coherenceMean;
	}

	public float getCoherenceSeeing() {
		return coherenceSeeing;
	}

	public void setCoherenceSeeing(float coherenceSeeing) {
		this.coherenceSeeing = coherenceSeeing;
	}

	public float getCoherenceAsymmetry() {
		return coherenceAsymmetry;
	}

	public void setCoherenceAsymmetry(float coherenceAsymmetry) {
		this.coherenceAsymmetry = coherenceAsymmetry;
	}

	public float getCoherenceAsymmetryUncertainty() {
		return coherenceAsymmetryUncertainty;
	}

	public void setCoherenceAsymmetryUncertainty(float coherenceAsymmetryUncertainty) {
		this.coherenceAsymmetryUncertainty = coherenceAsymmetryUncertainty;
	}

	@Override
	public String toString() {
		return "CoherenceAnalyzerResult [numberSeeingEdges=" + numberSeeingEdges + ", coherenceMean=" + coherenceMean + ", coherenceSeeing="
				+ coherenceSeeing + ", coherenceAsymmetry=" + coherenceAsymmetry + ", coherenceAsymmetryUncertainty="
				+ coherenceAsymmetryUncertainty + "]";
	};

	
	
	
}
