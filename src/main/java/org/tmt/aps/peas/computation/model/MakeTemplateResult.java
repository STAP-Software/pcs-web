package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for makeTemplate computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#makeTemplate(int, int, org.tmt.aps.peas.config.model.FindCentConfig, org.tmt.aps.peas.instrument.model.PupilMask, org.tmt.aps.peas.instrument.model.Filter)
 */
public class MakeTemplateResult {

	private float[][][][] templateArray;

	public MakeTemplateResult() {
		
	}
	
	public MakeTemplateResult(float[][][][] templateArray) {
		this.templateArray = templateArray;
	}

	public float[][][][] getTemplateArray() {
		return templateArray;
	}

	public void setTemplateArray(float[][][][] templateArray) {
		this.templateArray = templateArray;
	}
	

	
}
