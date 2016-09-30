package org.tmt.aps.peas.config.model;

import java.util.List;

import org.tmt.aps.peas.instrument.model.Filter;

/**
 * Constants data class containing phasing constants.  This class is populated from database data in the {@link Constant} class and is made available to executors and
 * the user interface controllers in the {@link org.tmt.aps.peas.config.business.ConstantsCache}.
 * @author smichaels
 */
public class PhasingConstants {
	
	
	float ringMode[];

	int phasingTemplateCount;
	int phasingSubimageFftSize;
	float nbSingleFilterCoherenceThreshold;
	
	float edgeHeightSearchInterval;
	float[] edgeHeightSearchRange;
	String[] edgeHeightSearchRangeFilters;
	
	
	float bbPhasingFracInterval;
	float ringModeCorrectionFactor;
	float stepSize30;
	float stepSize100;
	float stepSize300;
	float stepSize1000;
	
	public float[] getRingMode() {
		return ringMode;
	}
	
	public void setRingMode(float[] ringMode) {
		this.ringMode = ringMode;
	}
	
	
	public int getPhasingTemplateCount() {
		return phasingTemplateCount;
	}

	public void setPhasingTemplateCount(int phasingTemplateCount) {
		this.phasingTemplateCount = phasingTemplateCount;
	}



	public int getPhasingSubimageFftSize() {
		return phasingSubimageFftSize;
	}

	public void setPhasingSubimageFftSize(int phasingSubimageFftSize) {
		this.phasingSubimageFftSize = phasingSubimageFftSize;
	}

	public float getBbPhasingFracInterval() {
		return bbPhasingFracInterval;
	}

	public void setBbPhasingFracInterval(float bbPhasingFracInterval) {
		this.bbPhasingFracInterval = bbPhasingFracInterval;
	}

	public float getRingModeCorrectionFactor() {
		return ringModeCorrectionFactor;
	}

	public void setRingModeCorrectionFactor(float ringModeCorrectionFactor) {
		this.ringModeCorrectionFactor = ringModeCorrectionFactor;
	}

	public float getStepSize30() {
		return stepSize30;
	}

	public void setStepSize30(float stepSize30) {
		this.stepSize30 = stepSize30;
	}

	public float getStepSize100() {
		return stepSize100;
	}

	public void setStepSize100(float stepSize100) {
		this.stepSize100 = stepSize100;
	}

	public float getStepSize300() {
		return stepSize300;
	}

	public void setStepSize300(float stepSize300) {
		this.stepSize300 = stepSize300;
	}

	public float getStepSize1000() {
		return stepSize1000;
	}

	public void setStepSize1000(float stepSize1000) {
		this.stepSize1000 = stepSize1000;
	}

	public float getEdgeHeightSearchInterval() {
		return edgeHeightSearchInterval;
	}

	public void setEdgeHeightSearchInterval(float edgeHeightSearchInterval) {
		this.edgeHeightSearchInterval = edgeHeightSearchInterval;
	}
	
	public float[] getEdgeHeightSearchRange() {
		return edgeHeightSearchRange;
	}

	public void setEdgeHeightSearchRange(float[] edgeHeightSearchRange) {
		this.edgeHeightSearchRange = edgeHeightSearchRange;
	}

	public String[] getEdgeHeightSearchRangeFilters() {
		return edgeHeightSearchRangeFilters;
	}

	public void setEdgeHeightSearchRangeFilters(String[] edgeHeightSearchRangeFilters) {
		this.edgeHeightSearchRangeFilters = edgeHeightSearchRangeFilters;		
	}

	public float getNbSingleFilterCoherenceThreshold() {
		return nbSingleFilterCoherenceThreshold;
	}

	public void setNbSingleFilterCoherenceThreshold(float nbSingleFilterCoherenceThreshold) {
		this.nbSingleFilterCoherenceThreshold = nbSingleFilterCoherenceThreshold;
	}

	/**
	 * Convenience method returning the phasing edgeHeightSearchRange for a given number of filters
	 * @param filterType the filter type to get the fft size for
	 * @return the phasing subimage fft size for the passed filter type
	 */
	public float getEdgeHeightSearchRange(String[] filterNames) throws Exception {
		
		// we don't care if only one filter
		if (filterNames.length < 2) {
			return 0.0f;
		}
		
		// create key
		StringBuffer buf = new StringBuffer();
		for (String name : filterNames) {
			buf.append("_" + name);
		}
		buf.deleteCharAt(0);
		String key = buf.toString();
		for (int i=0; i<edgeHeightSearchRangeFilters.length; i++) {
			String candidate = edgeHeightSearchRangeFilters[i];
			if (candidate.equals(key)) {
				return edgeHeightSearchRange[i];
			}
		}
		throw new Exception("Edge Height Search Range not found");
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nringMode = ");
		for (int i=0; i<ringMode.length; i++) {
			buf.append(ringMode[i] + ", ");
		}
		
		buf.append("\nphasingTemplateCount = " + phasingTemplateCount);
		buf.append("\nphasingSubimageFftSize = " + phasingSubimageFftSize);
		buf.append("\nnbSingleFilterCoherenceThreshold = " + nbSingleFilterCoherenceThreshold);		
		buf.append("\nbbPhasingFracInterval = " + bbPhasingFracInterval);
		buf.append("\nringModeCorrectionFactor = " + ringModeCorrectionFactor);
		buf.append("\nstepSize30 = " + stepSize30);
		buf.append("\nstepSize100 = " + stepSize100);
		buf.append("\nstepSize300 = " + stepSize300);
		buf.append("\nstepSize1000 = " + stepSize1000);
		

		
		
		buf.append("\n");
		return buf.toString();
	}

	
	
}
