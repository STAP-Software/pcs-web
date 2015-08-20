package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;

// TODO - display i/fs need to change
public class PupilRegistrationProcedureOutput extends ProcedureOutput implements CentroidOffsetsDisplayValues {
	
	private FloatPoint translationFromRefBeam = new FloatPoint(0.0f,0.0f);
	private float rotationFromRefBeam;
	private float scaleChangeFromRefBeam;
	
	private FloatPoint[] ccdCentroidOffsets = new FloatPoint[1];
	private FloatPoint[] cartesianCentroidOffsets = new FloatPoint[1];

	
	private int maxSpotNum;
	private float maxOffset;
	private float rmsOffset;
	private float enclosedEnergy80;
	private float enclosedEnergy50;

	private float scaleError;
	private float slopeError;	
	

	// pupil reg specific
	
	private float regErrorX; // x registration error (m)
	private float regErrorY; // y registration error (m)
	private float regErrorPhi; // phi rotation error (r)
	private float regErrorApproxX; // x registration error using approx calc (m)
	private float regErrorApproxY; // y registration error using approx calc (m)
	private float regErrorApproxPhi; // phi rotation error using approx calc (r)
	private float regScaleError; // scale error

	
	private Point coarseMirrorCommands;
	private Point fineMirrorCommands;
	private boolean offloaded;
	private Point coarseMirrorDeltas;
	private Point fineMirrorDeltas;
	
	
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

	public float getRegErrorX() {
		return regErrorX;
	}
	public void setRegErrorX(float regErrorX) {
		this.regErrorX = regErrorX;
	}
	public float getRegErrorY() {
		return regErrorY;
	}
	public void setRegErrorY(float regErrorY) {
		this.regErrorY = regErrorY;
	}
	public float getRegErrorPhi() {
		return regErrorPhi;
	}
	public void setRegErrorPhi(float regErrorPhi) {
		this.regErrorPhi = regErrorPhi;
	}
	public float getRegErrorApproxX() {
		return regErrorApproxX;
	}
	public void setRegErrorApproxX(float regErrorApproxX) {
		this.regErrorApproxX = regErrorApproxX;
	}
	public float getRegErrorApproxY() {
		return regErrorApproxY;
	}
	public void setRegErrorApproxY(float regErrorApproxY) {
		this.regErrorApproxY = regErrorApproxY;
	}
	public float getRegErrorApproxPhi() {
		return regErrorApproxPhi;
	}
	public void setRegErrorApproxPhi(float regErrorApproxPhi) {
		this.regErrorApproxPhi = regErrorApproxPhi;
	}
	public float getRegScaleError() {
		return regScaleError;
	}
	public void setRegScaleError(float regScaleError) {
		this.regScaleError = regScaleError;
	}
	public Point getCoarseMirrorCommands() {
		return coarseMirrorCommands;
	}
	public void setCoarseMirrorCommands(Point coarseMirrorCommands) {
		this.coarseMirrorCommands = coarseMirrorCommands;
	}
	public Point getFineMirrorCommands() {
		return fineMirrorCommands;
	}
	public void setFineMirrorCommands(Point fineMirrorCommands) {
		this.fineMirrorCommands = fineMirrorCommands;
	}
	public boolean isOffloaded() {
		return offloaded;
	}
	public void setOffloaded(boolean offloaded) {
		this.offloaded = offloaded;
	}
	public Point getCoarseMirrorDeltas() {
		return coarseMirrorDeltas;
	}
	public void setCoarseMirrorDeltas(Point coarseMirrorDeltas) {
		this.coarseMirrorDeltas = coarseMirrorDeltas;
	}
	public Point getFineMirrorDeltas() {
		return fineMirrorDeltas;
	}
	public void setFineMirrorDeltas(Point fineMirrorDeltas) {
		this.fineMirrorDeltas = fineMirrorDeltas;
	}
	
	
	
}
