package org.tmt.aps.peas.computation.model;

public class CorrectOverscanDarkResult {

	int[][] correctedFrame;
	int darkMedianValueLeft;
	int darkMedianValueRight;
	
	public CorrectOverscanDarkResult(int[][] correctedFrame, int darkMedianValueLeft, int darkMedianValueRight) {
		this.correctedFrame = correctedFrame;
		this.darkMedianValueLeft = darkMedianValueLeft;
		this.darkMedianValueRight = darkMedianValueRight;
	}

	public int[][] getCorrectedFrame() {
		return correctedFrame;
	}

	public void setCorrectedFrame(int[][] correctedFrame) {
		this.correctedFrame = correctedFrame;
	}

	public int getDarkMedianValueLeft() {
		return darkMedianValueLeft;
	}

	public void setDarkMedianValueLeft(int darkMedianValueLeft) {
		this.darkMedianValueLeft = darkMedianValueLeft;
	}

	public int getDarkMedianValueRight() {
		return darkMedianValueRight;
	}

	public void setDarkMedianValueRight(int darkMedianValueRight) {
		this.darkMedianValueRight = darkMedianValueRight;
	}


}
