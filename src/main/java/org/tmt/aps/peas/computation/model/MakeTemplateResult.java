package org.tmt.aps.peas.computation.model;

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
