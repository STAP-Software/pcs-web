package org.tmt.aps.peas.procedure.exception;

import java.util.List;

public class UserAssistRequiredException extends Exception {
	
	private boolean ndetectNotAllSingle;
	private boolean fracThreshExceeded;
	private boolean fourierThreshExceeded;
	
	
	public boolean isNdetectNotAllSingle() {
		return ndetectNotAllSingle;
	}
	public void setNdetectNotAllSingle(boolean ndetectNotAllSingle) {
		this.ndetectNotAllSingle = ndetectNotAllSingle;
	}
	public boolean isFracThreshExceeded() {
		return fracThreshExceeded;
	}
	public void setFracThreshExceeded(boolean fracThreshExceeded) {
		this.fracThreshExceeded = fracThreshExceeded;
	}
	public boolean isFourierThreshExceeded() {
		return fourierThreshExceeded;
	}
	public void setFourierThreshExceeded(boolean fourierThreshExceeded) {
		this.fourierThreshExceeded = fourierThreshExceeded;
	}
			
	public boolean shouldThrow() {
		return ndetectNotAllSingle || fracThreshExceeded || fourierThreshExceeded;
	}

}
