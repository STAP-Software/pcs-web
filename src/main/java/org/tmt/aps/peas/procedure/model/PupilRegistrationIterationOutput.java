package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;

//TODO - display i/fs need to change
public class PupilRegistrationIterationOutput extends ProcedureIterationOutput {

	private boolean telescopeMoved;
	private FloatPoint deltaAzEl;
	
	private FloatPoint translationFromRefBeam = new FloatPoint(0.0f,0.0f);
	private float rotationFromRefBeam;
	private float scaleChangeFromRefBeam;
	
	private FloatPoint[] ccdCentroidOffsets;
	private FloatPoint[] cartesianCentroidOffsets;
	
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
	PupilRegErrorResult pupilRegErrorResult;
	CalcPrCommandsResult calcPrCommandsResult;
	
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
	public PupilRegErrorResult getPupilRegErrorResult() {
		return pupilRegErrorResult;
	}
	public void setPupilRegErrorResult(PupilRegErrorResult pupilRegErrorResult) {
		this.pupilRegErrorResult = pupilRegErrorResult;
	}	
	public CalcPrCommandsResult getCalcPrCommandsResult() {
		return calcPrCommandsResult;
	}
	public void setCalcPrCommandsResult(CalcPrCommandsResult calcPrCommandsResult) {
		this.calcPrCommandsResult = calcPrCommandsResult;
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

	public void addPupilRegErrorResult(PupilRegErrorResult pupilRegErrorResult) {
		
		setRegErrorX(pupilRegErrorResult.getRegErrorX());
		setRegErrorY(pupilRegErrorResult.getRegErrorY());
		setRegErrorPhi(pupilRegErrorResult.getRegErrorPhi());
		setRegErrorApproxX(pupilRegErrorResult.getRegErrorApproxX());
		setRegErrorApproxY(pupilRegErrorResult.getRegErrorApproxY());
		setRegErrorApproxPhi(pupilRegErrorResult.getRegErrorApproxPhi());
		setRegScaleError(pupilRegErrorResult.getRegScaleError());
	}

	public void addCalcPrCommandsResult(CalcPrCommandsResult calcPrCommandsResult) {
		setCoarseMirrorCommands(calcPrCommandsResult.getCoarseMirrorCommands());
		setFineMirrorCommands(calcPrCommandsResult.getFineMirrorCommands());
		setCoarseMirrorDeltas(calcPrCommandsResult.getCoarseMirrorDeltas());
		setFineMirrorDeltas(calcPrCommandsResult.getFineMirrorDeltas());
		setOffloaded(calcPrCommandsResult.isOffloaded());
	}

}
