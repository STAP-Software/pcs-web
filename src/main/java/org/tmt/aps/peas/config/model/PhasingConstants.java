package org.tmt.aps.peas.config.model;


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

	
	
	float bbPhasingFracInterval;
	float fracSeeing;
	float ringModeCorrectionFactor;
	float stepSize30;
	float stepSize100;
	float stepSize300;
	float stepSize1000;
	
	int goodEdgeCountThreshold;
	
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

	public float getFracSeeing() {
		return fracSeeing;
	}

	public void setFracSeeing(float fracSeeing) {
		this.fracSeeing = fracSeeing;
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
	

	public float getNbSingleFilterCoherenceThreshold() {
		return nbSingleFilterCoherenceThreshold;
	}

	public void setNbSingleFilterCoherenceThreshold(float nbSingleFilterCoherenceThreshold) {
		this.nbSingleFilterCoherenceThreshold = nbSingleFilterCoherenceThreshold;
	}

	
	public int getGoodEdgeCountThreshold() {
		return goodEdgeCountThreshold;
	}

	public void setGoodEdgeCountThreshold(int goodEdgeCountThreshold) {
		this.goodEdgeCountThreshold = goodEdgeCountThreshold;
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
		buf.append("\nfracSeeing = " + fracSeeing);
		buf.append("\nringModeCorrectionFactor = " + ringModeCorrectionFactor);
		buf.append("\nstepSize30 = " + stepSize30);
		buf.append("\nstepSize100 = " + stepSize100);
		buf.append("\nstepSize300 = " + stepSize300);
		buf.append("\nstepSize1000 = " + stepSize1000);
		buf.append("\ngoodEdgeCountThreshold = " + goodEdgeCountThreshold);
		

		
		
		buf.append("\n");
		return buf.toString();
	}

	
	
}
