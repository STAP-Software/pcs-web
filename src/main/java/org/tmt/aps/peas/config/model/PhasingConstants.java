package org.tmt.aps.peas.config.model;



public class PhasingConstants {
	
	
	float ringMode[];

	int phasingTemplateCount;
	int phasingSubimageFftSize;
	float bbPhasingFracInterval;
	float ringModeCorrectionFactor;
	
	
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
		
		buf.append("\n");
		return buf.toString();
	}

	
	
}
