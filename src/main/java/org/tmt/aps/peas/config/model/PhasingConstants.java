package org.tmt.aps.peas.config.model;



public class PhasingConstants {
	
	
	float ringMode[];
	int phTmplSize;
	
	
	public float[] getRingMode() {
		return ringMode;
	}
	
	public void setRingMode(float[] ringMode) {
		this.ringMode = ringMode;
	}
	
	public int getPhTmplSize() {
		return phTmplSize;
	}
	
	public void setPhTmplSize(int phTmplSize) {
		this.phTmplSize = phTmplSize;
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nringMode = ");
		for (int i=0; i<ringMode.length; i++) {
			buf.append(ringMode[i] + ", ");
		}
		
		buf.append("\nphTmplSize = " + phTmplSize);
		
		buf.append("\n");
		return buf.toString();
	}

	
	
}
