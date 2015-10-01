package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;

public class FineScreenIterationOutput extends ProcedureIterationOutput implements CentroidOffsetsDisplayValues, ActuatorDeltasDisplayValues {

	private boolean telescopeMoved;
	private FloatPoint deltaAzEl;
	
	private FloatPoint translationFromRefBeam = new FloatPoint(0.0f,0.0f);
	private float rotationFromRefBeam;
	private float scaleChangeFromRefBeam;

	private float[][] tipTiltActuatorDeltas = new float[36][3];
	private float[][] pistonActuatorDeltas = new float[36][3];

	private float[][] m1ActuatorCmds;
	private float m1ActuatorCmdsRms;
	private FloatPoint[] ccdCentroidOffsets;
	private FloatPoint[] cartesianCentroidOffsets;
	
	private int maxSpotNum;
	private float maxOffset;
	private float rmsOffset;
	private float enclosedEnergy80;
	private float enclosedEnergy50;

	private float scaleError;
	private float slopeError;
	
	private float pistonActuatorDeltasRms;

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

	public FloatPoint[] getCcdCentroidOffsets() {
		return ccdCentroidOffsets;
	}
	public void setCcdCentroidOffsets(FloatPoint[] ccdCentroidOffsets) {
		this.ccdCentroidOffsets = ccdCentroidOffsets;
	}
	public FloatPoint[] getCartesianCentroidOffsets() {
		return cartesianCentroidOffsets;
	}
	public void setCartesianCentroidOffsets(FloatPoint[] cartesianCentroidOffsets) {
		this.cartesianCentroidOffsets = cartesianCentroidOffsets;
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
	public float[][] getTipTiltActuatorDeltas() {
		return tipTiltActuatorDeltas;
	}
	public void setTipTiltActuatorDeltas(float[][] tipTiltActuatorDeltas) {
		this.tipTiltActuatorDeltas = tipTiltActuatorDeltas;
	}
	public float[][] getPistonActuatorDeltas() {
		return pistonActuatorDeltas;
	}
	public void setPistonActuatorDeltas(float[][] pistonActuatorDeltas) {
		this.pistonActuatorDeltas = pistonActuatorDeltas;
	}
	public float getPistonActuatorDeltasRms() {
		return pistonActuatorDeltasRms;
	}
	public void setPistonActuatorDeltasRms(float pistonActuatorDeltasRms) {
		this.pistonActuatorDeltasRms = pistonActuatorDeltasRms;
	}
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
	
	
	// convienience routines to populate
	// TODO: eventually these will supercede the above definitions
	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	CentroidStatsResult centroidStatsResult;
	ScaleErrorResult scaleErrorResult;
	CalcM2M1Result calcM2M1Result;
	
	public CenterTelescopeCalcResult getCenterTelescopeCalcResult() {
		return centerTelescopeCalcResult;
	}
	public void setCenterTelescopeCalcResult(CenterTelescopeCalcResult centerTelescopeCalcResult) {
		this.centerTelescopeCalcResult = centerTelescopeCalcResult;
	}
	public CentroidOffsetsResult getCentroidOffsetsResult() {
		return centroidOffsetsResult;
	}
	public void setCentroidOffsetsResult(CentroidOffsetsResult centroidOffsetsResult) {
		this.centroidOffsetsResult = centroidOffsetsResult;
	}
	public CentroidStatsResult getCentroidStatsResult() {
		return centroidStatsResult;
	}
	public void setCentroidStatsResult(CentroidStatsResult centroidStatsResult) {
		this.centroidStatsResult = centroidStatsResult;
	}
	public ScaleErrorResult getScaleErrorResult() {
		return scaleErrorResult;
	}
	public void setScaleErrorResult(ScaleErrorResult scaleErrorResult) {
		this.scaleErrorResult = scaleErrorResult;
	}
	public CalcM2M1Result getCalcM2M1Result() {
		return calcM2M1Result;
	}
	public void setCalcM2M1Result(CalcM2M1Result calcM2M1Result) {
		this.calcM2M1Result = calcM2M1Result;
	}
	
	
	// TODO: eventually these will be eliminated
	public void addCenterTelescopeCalcResult(CenterTelescopeCalcResult centerTelescopeCalcResult) {
		setDeltaAzEl(centerTelescopeCalcResult.getDeltaAzEl());
	}
	
	public void addCentroidOffsetsResult(CentroidOffsetsResult centroidOffsetsResult) {
		setCcdCentroidOffsets(centroidOffsetsResult.getCcdCentroidOffsets().toArray(new FloatPoint[0]));
		setCartesianCentroidOffsets(centroidOffsetsResult.getCartesianCentroidOffsets().toArray(new FloatPoint[0]));
		
		setRotationFromRefBeam(centroidOffsetsResult.getImageRotation());
		setScaleChangeFromRefBeam(centroidOffsetsResult.getImageScale());
		setTranslationFromRefBeam(centroidOffsetsResult.getImageTranslation());
		
		
	}
	
	public void addCentroidStatsResult(CentroidStatsResult centroidStatsResult) {
		
		setMaxSpotNum(centroidStatsResult.getMaxSpotNum());
		setMaxOffset(centroidStatsResult.getMaxOffset());
		setRmsOffset(centroidStatsResult.getRmsOffset());

		setEnclosedEnergy50(centroidStatsResult.getEnclosedEnergy50());
		setEnclosedEnergy80(centroidStatsResult.getEnclosedEnergy80());
	}
	
	public void addScaleErrorResult(ScaleErrorResult scaleErrorResult) {
		
		setScaleError(scaleErrorResult.getScaleError());
		setSlopeError(scaleErrorResult.getSlopeError());
	}
	

	public void addCalcM2M1Result(CalcM2M1Result calcM2M1Result) {
		System.out.println("YYYYYAAAAAAYYYYY");
	}
}
