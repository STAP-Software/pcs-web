package org.tmt.aps.peas.computation.model;

public class CorrectOverscanDarkResult {

	short[][] correctedFrame;
	short darkMedianValueLeft;
	short darkMedianValueRight;
	
	public CorrectOverscanDarkResult(short[][] correctedFrame, short darkMedianValueLeft, short darkMedianValueRight) {
		this.correctedFrame = correctedFrame;
		this.darkMedianValueLeft = darkMedianValueLeft;
		this.darkMedianValueRight = darkMedianValueRight;
	}

	public short[][] getCorrectedFrame() {
		return correctedFrame;
	}

	public void setCorrectedFrame(short[][] correctedFrame) {
		this.correctedFrame = correctedFrame;
	}

	public short getDarkMedianValueLeft() {
		return darkMedianValueLeft;
	}

	public void setDarkMedianValueLeft(short darkMedianValueLeft) {
		this.darkMedianValueLeft = darkMedianValueLeft;
	}

	public short getDarkMedianValueRight() {
		return darkMedianValueRight;
	}

	public void setDarkMedianValueRight(short darkMedianValueRight) {
		this.darkMedianValueRight = darkMedianValueRight;
	}
	
}
