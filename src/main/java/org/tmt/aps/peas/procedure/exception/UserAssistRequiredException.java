package org.tmt.aps.peas.procedure.exception;

/**
 * Exception thrown when Find and Identify fails to meet criteria.
 * The criteria that are missed are contained in the exception
 * The exception is used to print messages to the screen and ask the user for assistance about what to do next. 
 * @author smichaels
 *
 */
public class UserAssistRequiredException extends FandIException {
	
	private boolean ndetectNotAllSingle;
	private boolean fracThreshExceeded;
	private float fracThreshExceededFindCent = -1.0f;
	private boolean fracThreshExceededPT;
	private boolean fourierThreshExceeded;
	private boolean badNSolution;
	
	
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
			
	public boolean isBadNSolution() {
		return badNSolution;
	}
	public void setBadNSolution(boolean badNSolution) {
		this.badNSolution = badNSolution;
	}
	public boolean shouldThrow() {
		return ndetectNotAllSingle || fracThreshExceeded ||fracThreshExceededPT || fourierThreshExceeded || badNSolution;
	}
	public boolean isFracThreshExceededPT() {
		return fracThreshExceededPT;
	}
	public void setFracThreshExceededPT(boolean fracThreshExceededPT) {
		this.fracThreshExceededPT = fracThreshExceededPT;
	}
	public boolean isFracThreshExceededFindCent() {
		return fracThreshExceededFindCent != -1.0f;
	}
	public float getFracThreshExceededFindCent() {
		return fracThreshExceededFindCent;
	}
	public void setFracThreshExceededFindCent(float findCentFracFilled) {
		this.fracThreshExceededFindCent = findCentFracFilled;
	}

}
