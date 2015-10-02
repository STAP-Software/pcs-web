package org.tmt.aps.peas.computation.model;

import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;

public class CalcM2M1Result {

	
	float m2Piston;
	FloatPoint m2TipTilt;
	float centroidResidual;
	float pistonErrorMultiplier;
	FloatPoint tipTiltErrorMulitplier;
	List<FloatPoint> m1OffsetsCorrectedForM2;
	
	
	public CalcM2M1Result(float m2Piston, FloatPoint m2TipTilt, float centroidResidual, float pistonErrorMultiplier, FloatPoint tipTiltErrorMulitplier,
			List<FloatPoint> m1OffsetsCorrectedForM2) {

			this.m2Piston = m2Piston;
			this.m2TipTilt = m2TipTilt;
			this.centroidResidual = centroidResidual;
			this.pistonErrorMultiplier = pistonErrorMultiplier;
			this.tipTiltErrorMulitplier = tipTiltErrorMulitplier;
			this.m1OffsetsCorrectedForM2 = m1OffsetsCorrectedForM2;
	}
	
	public CalcM2M1Result() {};
	

	public float getM2Piston() {
		return m2Piston;
	}
	public void setM2Piston(float m2Piston) {
		this.m2Piston = m2Piston;
	}
	public FloatPoint getM2TipTilt() {
		return m2TipTilt;
	}
	public void setM2TipTilt(FloatPoint m2TipTilt) {
		this.m2TipTilt = m2TipTilt;
	}
	public float getCentroidResidual() {
		return centroidResidual;
	}
	public void setCentroidResidual(float centroidResidual) {
		this.centroidResidual = centroidResidual;
	}
	public float getPistonErrorMultiplier() {
		return pistonErrorMultiplier;
	}
	public void setPistonErrorMultiplier(float pistonErrorMultiplier) {
		this.pistonErrorMultiplier = pistonErrorMultiplier;
	}
	public FloatPoint getTipTiltErrorMulitplier() {
		return tipTiltErrorMulitplier;
	}
	public void setTipTiltErrorMulitplier(FloatPoint tipTiltErrorMulitplier) {
		this.tipTiltErrorMulitplier = tipTiltErrorMulitplier;
	}
	public List<FloatPoint> getM1OffsetsCorrectedForM2() {
		return m1OffsetsCorrectedForM2;
	}
	public void setM1OffsetsCorrectedForM2(List<FloatPoint> m1OffsetsCorrectedForM2) {
		this.m1OffsetsCorrectedForM2 = m1OffsetsCorrectedForM2;
	}
	
	
}
