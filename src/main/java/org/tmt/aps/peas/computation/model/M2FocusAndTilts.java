package org.tmt.aps.peas.computation.model;

public class M2FocusAndTilts {
	float m2TelescopeFocus;
	float m2ThetaX;
	float m2ThetaY;
	
	public M2FocusAndTilts() {}
	
	public M2FocusAndTilts(double[] m2FocusAndTilts) {
		m2TelescopeFocus = (float)m2FocusAndTilts[0];
		m2ThetaX = (float)m2FocusAndTilts[1];
		m2ThetaY = (float)m2FocusAndTilts[2];
	}

	public float getM2TelescopeFocus() {
		return m2TelescopeFocus;
	}

	public void setM2TelescopeFocus(float m2TelescopeFocus) {
		this.m2TelescopeFocus = m2TelescopeFocus;
	}

	public float getM2ThetaX() {
		return m2ThetaX;
	}

	public void setM2ThetaX(float m2ThetaX) {
		this.m2ThetaX = m2ThetaX;
	}

	public float getM2ThetaY() {
		return m2ThetaY;
	}

	public void setM2ThetaY(float m2ThetaY) {
		this.m2ThetaY = m2ThetaY;
	}


}
