package org.tmt.aps.peas.procedure.exception;

/**
 * Exception thrown when Find and Identify fails due to subimages that are detected to be in the non-linear intensity region
 * @author smichaels
 *
 */
public class NonLinearIntensitiesException extends FandIException {
	
	private static final long serialVersionUID = -3129681181707153440L;
	float max;
	float threshold;
	int n;
	
	
	public NonLinearIntensitiesException(float max, float threshold, int n) {
		this.max = max;
		this.threshold = threshold;
		this.n = n;
	}
	
	public float getMax() {
		return max;
	}

	public float getThreshold() {
		return threshold;
	}

	public int getN() {
		return n;
	}
	
	

}
