package org.tmt.aps.peas.config.model;

import org.tmt.aps.peas.instrument.model.FilterType;

/**
 * Constants data class containing phasing constants.  This class is populated from database data in the {@link Constant} class and is made available to executors and
 * the user interface controllers in the {@link org.tmt.aps.peas.config.business.ConstantsCache}.
 * @author smichaels
 */
public class PhasingConstants {
	
	
	float ringMode[];

	int phasingTemplateCount;
	int phasingSubimageFftSize;
	
	float edgeHeightSearchInterval;
	float edgeHeightSearchRange1Filter;
	float edgeHeightSearchRange2Filter;
	float edgeHeightSearchRange3Filter;
	float edgeHeightSearchRange4Filter;
	
	
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

	public float getEdgeHeightSearchRange1Filter() {
		return edgeHeightSearchRange1Filter;
	}

	public void setEdgeHeightSearchRange1Filter(float edgeHeightSearchRange1Filter) {
		this.edgeHeightSearchRange1Filter = edgeHeightSearchRange1Filter;
	}

	public float getEdgeHeightSearchRange2Filter() {
		return edgeHeightSearchRange2Filter;
	}

	public void setEdgeHeightSearchRange2Filter(float edgeHeightSearchRange2Filter) {
		this.edgeHeightSearchRange2Filter = edgeHeightSearchRange2Filter;
	}

	public float getEdgeHeightSearchRange3Filter() {
		return edgeHeightSearchRange3Filter;
	}

	public void setEdgeHeightSearchRange3Filter(float edgeHeightSearchRange3Filter) {
		this.edgeHeightSearchRange3Filter = edgeHeightSearchRange3Filter;
	}

	public float getEdgeHeightSearchRange4Filter() {
		return edgeHeightSearchRange4Filter;
	}

	public void setEdgeHeightSearchRange4Filter(float edgeHeightSearchRange4Filter) {
		this.edgeHeightSearchRange4Filter = edgeHeightSearchRange4Filter;
	}


	
	/**
	 * Convenience method returning the phasing edgeHeightSearchRange for a given number of filters
	 * @param filterType the filter type to get the fft size for
	 * @return the phasing subimage fft size for the passed filter type
	 */
	public float getEdgeHeightSearchRange(int filterCount) {
		
		switch (filterCount) {
		case 1: 
			return edgeHeightSearchRange1Filter;
		case 2: 
			return edgeHeightSearchRange2Filter;
		case 3: 
			return edgeHeightSearchRange3Filter;
		case 4: 
			return edgeHeightSearchRange4Filter;
			
		default: 
			return 0.0f;
		}
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nringMode = ");
		for (int i=0; i<ringMode.length; i++) {
			buf.append(ringMode[i] + ", ");
		}
		
		buf.append("\nphasingTemplateCount = " + phasingTemplateCount);
		buf.append("\nphasingSubimageFftSize = " + phasingSubimageFftSize);
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
