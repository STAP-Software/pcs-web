package org.tmt.aps.peas.procedure.exception;

/**
 * Exception thrown when Find and Identify fails due to subimages that are detected to be in the non-linear intensity region
 * @author smichaels
 *
 */
public class NonLinearIntensitiesException extends FandIException {
	
	float max;
	float threshold;
	
	public NonLinearIntensitiesException(float max, float threshold) {
		this.max = max;
		this.threshold = threshold;
	}
	
	public float getMax() {
		return max;
	}

	public float getThreshold() {
		return threshold;
	}

	
	

}
