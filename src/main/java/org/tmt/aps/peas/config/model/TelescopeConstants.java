package org.tmt.aps.peas.config.model;



public class TelescopeConstants {
	
	
	float backFocalDistance;
	float m1FocalLength;
	float telescopeFocalLength;
	float m1CurvatureRadius;
	float m2ActuatorRadius;
	float m2TtCorrectionFactor;
	int numberOfSegments;
	float m1OuterDiameter;
	
	
	public float getBackFocalDistance() {
		return backFocalDistance;
	}
	public void setBackFocalDistance(float backFocalDistance) {
		this.backFocalDistance = backFocalDistance;
	}
	public float getM1FocalLength() {
		return m1FocalLength;
	}
	public void setM1FocalLength(float m1FocalLength) {
		this.m1FocalLength = m1FocalLength;
	}
	public float getTelescopeFocalLength() {
		return telescopeFocalLength;
	}
	public void setTelescopeFocalLength(float telescopeFocalLength) {
		this.telescopeFocalLength = telescopeFocalLength;
	}
	public float getM1CurvatureRadius() {
		return m1CurvatureRadius;
	}
	public void setM1CurvatureRadius(float m1CurvatureRadius) {
		this.m1CurvatureRadius = m1CurvatureRadius;
	}
	public float getM2ActuatorRadius() {
		return m2ActuatorRadius;
	}
	public void setM2ActuatorRadius(float m2ActuatorRadius) {
		this.m2ActuatorRadius = m2ActuatorRadius;
	}
	public float getM2TtCorrectionFactor() {
		return m2TtCorrectionFactor;
	}
	public void setM2TtCorrectionFactor(float m2TtCorrectionFactor) {
		this.m2TtCorrectionFactor = m2TtCorrectionFactor;
	}
	public float getM1OuterDiameter() {
		return m1OuterDiameter;
	}
	public void setM1OuterDiameter(float m1OuterDiameter) {
		this.m1OuterDiameter = m1OuterDiameter;
	}
	public int getNumberOfSegments() {
		return numberOfSegments;
	}
	public void setNumberOfSegments(int numberOfSegments) {
		this.numberOfSegments = numberOfSegments;
	}
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nbackFocalDistance = " + backFocalDistance);
		buf.append("\nm1FocalLength = " + m1FocalLength);
		buf.append("\ntelescopeFocalLength = " + telescopeFocalLength);
		buf.append("\nm1CurvatureRadius = " + m1CurvatureRadius);
		buf.append("\nm2ActuatorRadius = " + m2ActuatorRadius);
		buf.append("\nm2TtCorrectionFactor = " + m2TtCorrectionFactor);
		buf.append("\nnumberOfSegments = " + numberOfSegments);
		buf.append("\nm1OuterDiameter = " + m1OuterDiameter);

		buf.append("\n");
		return buf.toString();
	}

	
}
