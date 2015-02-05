package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;

public class PassiveTiltIterationOutput extends ProcedureIterationOutput {

	private boolean telescopeMoved;
	private FloatPoint deltaAzEl;
	private float[][] m1ActuatorCmds;
	private float m1ActuatorCmdsRms;
	private FloatPoint[] centroidOffsets;
	private float m1PistonCmdsRms;
	private float m1PistonResidualRms;
	
	private int maxSpotNum;
	private float maxOffset;
	private float rmsOffset;
	private float enclosedEnergy80;
	private float enclosedEnergy50;

	private float scaleError;
	private float slopeError;

	public boolean isTelescopeMoved() {
		return telescopeMoved;
	}
	public void setTelescopeMoved(boolean telescopeMoved) {
		this.telescopeMoved = telescopeMoved;
	}
	public FloatPoint getDeltaAzEl() {
		return deltaAzEl;
	}
	public void setDeltaAzEl(FloatPoint deltaAzEl) {
		this.deltaAzEl = deltaAzEl;
	}
	public float[][] getM1ActuatorCmds() {
		return m1ActuatorCmds;
	}
	public void setM1ActuatorCmds(float[][] m1ActuatorCmds) {
		this.m1ActuatorCmds = m1ActuatorCmds;
	}
	public float getM1ActuatorCmdsRms() {
		return m1ActuatorCmdsRms;
	}
	public void setM1ActuatorCmdsRms(float m1ActuatorCmdsRms) {
		this.m1ActuatorCmdsRms = m1ActuatorCmdsRms;
	}
	public FloatPoint[] getCentroidOffsets() {
		return centroidOffsets;
	}
	public void setCentroidOffsets(FloatPoint[] centroidOffsets) {
		this.centroidOffsets = centroidOffsets;
	}
	public float getM1PistonCmdsRms() {
		return m1PistonCmdsRms;
	}
	public void setM1PistonCmdsRms(float m1PistonCmdsRms) {
		this.m1PistonCmdsRms = m1PistonCmdsRms;
	}
	public float getM1PistonResidualRms() {
		return m1PistonResidualRms;
	}
	public void setM1PistonResidualRms(float m1PistonResidualRms) {
		this.m1PistonResidualRms = m1PistonResidualRms;
	}
	public int getMaxSpotNum() {
		return maxSpotNum;
	}
	public void setMaxSpotNum(int maxSpotNum) {
		this.maxSpotNum = maxSpotNum;
	}
	public float getMaxOffset() {
		return maxOffset;
	}
	public void setMaxOffset(float maxOffset) {
		this.maxOffset = maxOffset;
	}
	public float getRmsOffset() {
		return rmsOffset;
	}
	public void setRmsOffset(float rmsOffset) {
		this.rmsOffset = rmsOffset;
	}
	public float getEnclosedEnergy80() {
		return enclosedEnergy80;
	}
	public void setEnclosedEnergy80(float enclosedEnergy80) {
		this.enclosedEnergy80 = enclosedEnergy80;
	}
	public float getEnclosedEnergy50() {
		return enclosedEnergy50;
	}
	public void setEnclosedEnergy50(float enclosedEnergy50) {
		this.enclosedEnergy50 = enclosedEnergy50;
	}
	public float getScaleError() {
		return scaleError;
	}
	public void setScaleError(float scaleError) {
		this.scaleError = scaleError;
	}
	public float getSlopeError() {
		return slopeError;
	}
	public void setSlopeError(float slopeError) {
		this.slopeError = slopeError;
	}
	
	
	
}
