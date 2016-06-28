package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for colorStep computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#colorStep(int, float)
 */
public class ColorStepResult {

	
	float[][] colorSteps;

	
	public ColorStepResult(float[][] colorSteps) {

		this.colorSteps = colorSteps;
	}


	public ColorStepResult() {}



	public float[][] getColorSteps() {
		return colorSteps;
	}



	public void setColorSteps(float[][] colorSteps) {
		this.colorSteps = colorSteps;
	};
	

	
}
