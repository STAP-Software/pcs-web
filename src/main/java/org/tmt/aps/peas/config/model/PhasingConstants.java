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
	int phasingSubimageFftSize611;
	int phasingSubimageFftSize651;
	int phasingSubimageFftSize852;
	int phasingSubimageFftSize870;
	int phasingSubimageFftSize891;
	
	
	
	
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


	public int getPhasingSubimageFftSize611() {
		return phasingSubimageFftSize611;
	}

	public void setPhasingSubimageFftSize611(int phasingSubimageFftSize611) {
		this.phasingSubimageFftSize611 = phasingSubimageFftSize611;
	}

	public int getPhasingSubimageFftSize651() {
		return phasingSubimageFftSize651;
	}

	public void setPhasingSubimageFftSize651(int phasingSubimageFftSize651) {
		this.phasingSubimageFftSize651 = phasingSubimageFftSize651;
	}

	public int getPhasingSubimageFftSize852() {
		return phasingSubimageFftSize852;
	}

	public void setPhasingSubimageFftSize852(int phasingSubimageFftSize852) {
		this.phasingSubimageFftSize852 = phasingSubimageFftSize852;
	}

	public int getPhasingSubimageFftSize870() {
		return phasingSubimageFftSize870;
	}

	public void setPhasingSubimageFftSize870(int phasingSubimageFftSize870) {
		this.phasingSubimageFftSize870 = phasingSubimageFftSize870;
	}

	public int getPhasingSubimageFftSize891() {
		return phasingSubimageFftSize891;
	}

	public void setPhasingSubimageFftSize891(int phasingSubimageFftSize891) {
		this.phasingSubimageFftSize891 = phasingSubimageFftSize891;
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

	/**
	 * Convenience method returning the phasing subimage fft size for a given filter
	 * @param filterType the filter type to get the fft size for
	 * @return the phasing subimage fft size for the passed filter type
	 */
	public int getPhasingSubimageFftSize(FilterType filterType) {
		int filterTypeWaveLength = new Integer(filterType.getFilterTypeName());
		switch (filterTypeWaveLength) {
		case 611: 
			return phasingSubimageFftSize611;
		case 651: 
			return phasingSubimageFftSize651;
		case 852: 
			return phasingSubimageFftSize852;
		case 870: 
			return phasingSubimageFftSize870;
		case 891: 
			return phasingSubimageFftSize891;
			
		default: 
			return -1;
		}
	}
	
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nringMode = ");
		for (int i=0; i<ringMode.length; i++) {
			buf.append(ringMode[i] + ", ");
		}
		
		buf.append("\nphasingTemplateCount = " + phasingTemplateCount);
		buf.append("\nphasingSubimageFftSize611 = " + phasingSubimageFftSize611);
		buf.append("\nphasingSubimageFftSize651 = " + phasingSubimageFftSize651);
		buf.append("\nphasingSubimageFftSize852 = " + phasingSubimageFftSize852);
		buf.append("\nphasingSubimageFftSize870 = " + phasingSubimageFftSize870);
		buf.append("\nphasingSubimageFftSize891 = " + phasingSubimageFftSize891);
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
