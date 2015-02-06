package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;


public class PassiveTiltProcedureOutput extends ProcedureOutput implements CentroidOffsetsDisplayValues {
	
	private FloatPoint translationFromRefBeam = new FloatPoint(0.0f,0.0f);
	private float rotationFromRefBeam;
	private float scaleChangeFromRefBeam;
	private float[][] m1ActuatorCmds = new float[36][3];
	private float m1ActuatorCmdsRms;
	private boolean m1CmdsSent;
	private FloatPoint[] centroidOffsets = new FloatPoint[1];

	
	private int maxSpotNum;
	private float maxOffset;
	private float rmsOffset;
	private float enclosedEnergy80;
	private float enclosedEnergy50;

	private float scaleError;
	private float slopeError;	
	
	public FloatPoint getTranslationFromRefBeam() {
		return translationFromRefBeam;
	}
	public void setTranslationFromRefBeam(FloatPoint translationFromRefBeam) {
		this.translationFromRefBeam = translationFromRefBeam;
	}
	public float getRotationFromRefBeam() {
		return rotationFromRefBeam;
	}
	public void setRotationFromRefBeam(float rotationFromRefBeam) {
		this.rotationFromRefBeam = rotationFromRefBeam;
	}
	public float getScaleChangeFromRefBeam() {
		return scaleChangeFromRefBeam;
	}
	public void setScaleChangeFromRefBeam(float scaleChangeFromRefBeam) {
		this.scaleChangeFromRefBeam = scaleChangeFromRefBeam;
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
	public boolean isM1CmdsSent() {
		return m1CmdsSent;
	}
	public void setM1CmdsSent(boolean m1CmdsSent) {
		this.m1CmdsSent = m1CmdsSent;
	}

	public FloatPoint[] getCentroidOffsets() {
		return centroidOffsets;
	}
	public void setCentroidOffsets(FloatPoint[] centroidOffsets) {
		this.centroidOffsets = centroidOffsets;
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
