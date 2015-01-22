package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;

public class PassiveTiltIterationOutput extends ProcedureIterationOutput {

	private boolean telescopeMoved;
	private FloatPoint deltaAzEl;
	private float[][] m1ActuatorCmds;
	private float m1ActuatorCmdsRms;
	private FloatPoint[] centroidOffsets;
	private float centroidOffsetsFocus;
	private float centroidOffsetsRms;
	private float m1PistonCmdsRms;
	private float m1PistonResidualRms;
	
	
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
	public float getCentroidOffsetsFocus() {
		return centroidOffsetsFocus;
	}
	public void setCentroidOffsetsFocus(float centroidOffsetsFocus) {
		this.centroidOffsetsFocus = centroidOffsetsFocus;
	}
	public float getCentroidOffsetsRms() {
		return centroidOffsetsRms;
	}
	public void setCentroidOffsetsRms(float centroidOffsetsRms) {
		this.centroidOffsetsRms = centroidOffsetsRms;
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
	
	
	
}
