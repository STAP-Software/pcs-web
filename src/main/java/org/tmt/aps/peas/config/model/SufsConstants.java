package org.tmt.aps.peas.config.model;



public class SufsConstants {
	
	int numLocalSufsSpots;
	int numGlobalSufsSpots;
	
	int sufsGroupSegmentToMask[][];
	int sufsGroupToMirror[][];

	
	public int getNumLocalSufsSpots() {
		return numLocalSufsSpots;
	}

	public void setNumLocalSufsSpots(int numLocalSufsSpots) {
		this.numLocalSufsSpots = numLocalSufsSpots;
	}

	public int getNumGlobalSufsSpots() {
		return numGlobalSufsSpots;
	}

	public void setNumGlobalSufsSpots(int numGlobalSufsSpots) {
		this.numGlobalSufsSpots = numGlobalSufsSpots;
	}

	public int[][] getSufsGroupSegmentToMask() {
		return sufsGroupSegmentToMask;
	}

	public void setSufsGroupSegmentToMask(int[][] sufsGroupSegmentToMask) {
		this.sufsGroupSegmentToMask = sufsGroupSegmentToMask;
	}

	public int[][] getSufsGroupToMirror() {
		return sufsGroupToMirror;
	}

	public void setSufsGroupToMirror(int[][] sufsGroupToMirror) {
		this.sufsGroupToMirror = sufsGroupToMirror;
	}

	public String getSufsGroupToMirrorDisplayString(int sufsGroupNumber) {
		StringBuffer buf = new StringBuffer();
		for (int mirrorNumber : sufsGroupToMirror[sufsGroupNumber]) {
			buf.append(mirrorNumber +", ");
		}
		return buf.substring(0, buf.length()-2);
	}
	

	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nnumLocalSufsSpots = " + numLocalSufsSpots);
		buf.append("\nnumGlobalSufsSpots = " + numGlobalSufsSpots);

		
		buf.append("\nsufsGroupSegmentToMask = ");
		if (sufsGroupSegmentToMask != null) {
		for (int i=0; i<sufsGroupSegmentToMask.length; i++) {
			buf.append("[");
			for (int j=0; j<sufsGroupSegmentToMask[0].length; j++) {
				buf.append(sufsGroupSegmentToMask[i][j] + ", ");
			}
			buf.append("],\n");
		}
		}
		
		buf.append("\nsufsGroupToMirror = ");
		if (sufsGroupToMirror != null) {
		for (int i=0; i<sufsGroupToMirror.length; i++) {
			buf.append("[");
			for (int j=0; j<sufsGroupToMirror[0].length; j++) {
				buf.append(sufsGroupToMirror[i][j] + ", ");
			}
			buf.append("],\n");
		}
		}
		
		
		buf.append("\n");
		return buf.toString();
	}

	
	
}
