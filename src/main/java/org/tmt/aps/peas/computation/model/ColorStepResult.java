package org.tmt.aps.peas.computation.model;

import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;

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
