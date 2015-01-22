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
	private float centroidOffsetsFocus;	
	private float centroidOffsetsRms;
	private float m1PistonCmdsRms;
	private float m1PistonResidualRms;
	
	
	
	
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
	
	
	
}
