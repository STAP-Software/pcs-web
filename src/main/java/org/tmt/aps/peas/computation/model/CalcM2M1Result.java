package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Computation data result class for both calculateM2M1Analytical and calculateM2M1RayTrace computations.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculateM2M1Analytical(FindCentroidsResult, CentroidOffsetsResult, int[], FloatPoint[][], int[], org.tmt.aps.peas.config.model.TelescopeConstants, float, float, float, int, int, org.tmt.aps.peas.instrument.model.PupilMaskType)
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculateM2M1RayTrace(FindCentroidsResult, CentroidOffsetsResult, int[], float, float, FloatPoint[][], int[], org.tmt.aps.peas.config.model.TelescopeConstants, float, float, org.tmt.aps.peas.instrument.model.PupilMaskType)
 */
public class CalcM2M1Result {

	
	float m2Piston;
	FloatPoint m2TipTilt;
	FloatPoint m2TipTiltTelescopeCoords;
	float centroidResidual;
	float pistonErrorMultiplier;
	FloatPoint tipTiltErrorMulitplier;
	FloatPoint[] m1OffsetsCorrectedForM2;
	FloatPoint[] m1OffsetsCorrectedForM2PixelsCartesian;
	FloatPoint[] m1OffsetsCorrectedForM2PixelsCcd;
	

	public CalcM2M1Result(float m2Piston, FloatPoint m2TipTilt, FloatPoint m2TipTiltTelescopeCoords, float centroidResidual,
			FloatPoint[] m1OffsetsCorrectedForM2, FloatPoint[] m1OffsetsCorrectedForM2PixelsCartesian, FloatPoint[] m1OffsetsCorrectedForM2PixelsCcd) {

			this.m2Piston = m2Piston;
			this.m2TipTilt = m2TipTilt;
			this.m2TipTiltTelescopeCoords = m2TipTiltTelescopeCoords;
			this.centroidResidual = centroidResidual;
			this.pistonErrorMultiplier = 0.0f;
			this.tipTiltErrorMulitplier = new FloatPoint();
			this.m1OffsetsCorrectedForM2 = m1OffsetsCorrectedForM2;
			this.m1OffsetsCorrectedForM2PixelsCartesian = m1OffsetsCorrectedForM2PixelsCartesian;
			this.m1OffsetsCorrectedForM2PixelsCcd = m1OffsetsCorrectedForM2PixelsCcd;
	}

	public CalcM2M1Result(float m2Piston, FloatPoint m2TipTilt, FloatPoint m2TipTiltTelescopeCoords, float centroidResidual, float pistonErrorMultiplier, FloatPoint tipTiltErrorMulitplier,
			FloatPoint[] m1OffsetsCorrectedForM2, FloatPoint[] m1OffsetsCorrectedForM2PixelsCartesian, FloatPoint[] m1OffsetsCorrectedForM2PixelsCcd) {

			this.m2Piston = m2Piston;
			this.m2TipTilt = m2TipTilt;
			this.m2TipTiltTelescopeCoords = m2TipTiltTelescopeCoords;
			this.centroidResidual = centroidResidual;
			this.pistonErrorMultiplier = pistonErrorMultiplier;
			this.tipTiltErrorMulitplier = tipTiltErrorMulitplier;
			this.m1OffsetsCorrectedForM2 = m1OffsetsCorrectedForM2;
			this.m1OffsetsCorrectedForM2PixelsCartesian = m1OffsetsCorrectedForM2PixelsCartesian;
			this.m1OffsetsCorrectedForM2PixelsCcd = m1OffsetsCorrectedForM2PixelsCcd;
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
	
	public FloatPoint getM2TipTiltTelescopeCoords() {
		return m2TipTiltTelescopeCoords;
	}

	public void setM2TipTiltTelescopeCoords(FloatPoint m2TipTiltTelescopeCoords) {
		this.m2TipTiltTelescopeCoords = m2TipTiltTelescopeCoords;
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

	public FloatPoint[] getM1OffsetsCorrectedForM2() {
		return m1OffsetsCorrectedForM2;
	}

	public void setM1OffsetsCorrectedForM2(FloatPoint[] m1OffsetsCorrectedForM2) {
		this.m1OffsetsCorrectedForM2 = m1OffsetsCorrectedForM2;
	}

	public FloatPoint[] getM1OffsetsCorrectedForM2PixelsCartesian() {
		return m1OffsetsCorrectedForM2PixelsCartesian;
	}

	public void setM1OffsetsCorrectedForM2PixelsCartesian(FloatPoint[] m1OffsetsCorrectedForM2PixelsCartesian) {
		this.m1OffsetsCorrectedForM2PixelsCartesian = m1OffsetsCorrectedForM2PixelsCartesian;
	}

	public FloatPoint[] getM1OffsetsCorrectedForM2PixelsCcd() {
		return m1OffsetsCorrectedForM2PixelsCcd;
	}

	public void setM1OffsetsCorrectedForM2PixelsCcd(FloatPoint[] m1OffsetsCorrectedForM2PixelsCcd) {
		this.m1OffsetsCorrectedForM2PixelsCcd = m1OffsetsCorrectedForM2PixelsCcd;
	}

	
	
}
