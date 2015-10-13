package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActDeltasRmsStdResult;
import org.tmt.aps.peas.computation.model.CalcM2ActuatorsFromPttResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CalcM2PttErrorsMeanStdResult;
import org.tmt.aps.peas.computation.model.CalcSegmentMeanTipTiltsResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.FineScreenScaleErrorResult;
import org.tmt.aps.peas.computation.model.PassiveTiltScaleErrorResult;
import org.tmt.aps.peas.computation.model.PseudoTipTiltCentroidStatsResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.AvgCentroidOffsetsDisplayValues;


public class FineScreenProcedureOutput extends ProcedureOutput implements AvgCentroidOffsetsDisplayValues, ActuatorDeltasDisplayValues {
	
	CentroidOffsetsResult centroidOffsetsResult;
	PseudoTipTiltCentroidStatsResult pseudoTipTiltCentroidStatsResult;
	FineScreenScaleErrorResult fineScreenScaleErrorResult;
	PassiveTiltScaleErrorResult passiveTiltScaleErrorResult;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;
	CalcM2M1Result calcM2M1Result;
	CalcM2ActuatorsFromPttResult calcM2ActuatorsFromPttResult;
	CalcDesiredActDeltasRmsStdResult calcDesiredActDeltasRmsStdResult;
	CalcM2PttErrorsMeanStdResult calcM2PttErrorsMeanStdResult;
	CalcSegmentMeanTipTiltsResult calcSegmentMeanTipTiltsResult;
	
	
	public CentroidOffsetsResult getCentroidOffsetsResult() {
		return centroidOffsetsResult;
	}
	public void setCentroidOffsetsResult(CentroidOffsetsResult centroidOffsetsResult) {
		this.centroidOffsetsResult = centroidOffsetsResult;
	}
	public PseudoTipTiltCentroidStatsResult getPseudoTipTiltCentroidStatsResult() {
		return pseudoTipTiltCentroidStatsResult;
	}
	public void setPseudoTipTiltCentroidStatsResult(PseudoTipTiltCentroidStatsResult pseudoTipTiltCentroidStatsResult) {
		this.pseudoTipTiltCentroidStatsResult = pseudoTipTiltCentroidStatsResult;
	}
	public FineScreenScaleErrorResult getFineScreenScaleErrorResult() {
		return fineScreenScaleErrorResult;
	}
	public void setFineScreenScaleErrorResult(FineScreenScaleErrorResult fineScreenScaleErrorResult) {
		this.fineScreenScaleErrorResult = fineScreenScaleErrorResult;
	}
	public PassiveTiltScaleErrorResult getPassiveTiltScaleErrorResult() {
		return passiveTiltScaleErrorResult;
	}
	public void setPassiveTiltScaleErrorResult(PassiveTiltScaleErrorResult passiveTiltScaleErrorResult) {
		this.passiveTiltScaleErrorResult = passiveTiltScaleErrorResult;
	}
	
	public ScaleErrorResult getScaleErrorResult() {
		return fineScreenScaleErrorResult;
	}
	
	
	public CalcDesiredActCommandsResult getCalcDesiredActCommandsResult() {
		return calcDesiredActCommandsResult;
	}
	public void setCalcDesiredActCommandsResult(CalcDesiredActCommandsResult calcDesiredActCommandsResult) {
		this.calcDesiredActCommandsResult = calcDesiredActCommandsResult;
	}
	public CalcM2M1Result getCalcM2M1Result() {
		return calcM2M1Result;
	}
	public void setCalcM2M1Result(CalcM2M1Result calcM2M1Result) {
		this.calcM2M1Result = calcM2M1Result;
	}
	public CalcM2ActuatorsFromPttResult getCalcM2ActuatorsFromPttResult() {
		return calcM2ActuatorsFromPttResult;
	}
	public void setCalcM2ActuatorsFromPttResult(CalcM2ActuatorsFromPttResult calcM2ActuatorsFromPttResult) {
		this.calcM2ActuatorsFromPttResult = calcM2ActuatorsFromPttResult;
	}
	public CalcDesiredActDeltasRmsStdResult getCalcDesiredActDeltasRmsStdResult() {
		return calcDesiredActDeltasRmsStdResult;
	}
	public void setCalcDesiredActDeltasRmsStdResult(CalcDesiredActDeltasRmsStdResult calcDesiredActDeltasRmsStdResult) {
		this.calcDesiredActDeltasRmsStdResult = calcDesiredActDeltasRmsStdResult;
	}
	public CalcM2PttErrorsMeanStdResult getCalcM2PttErrorsMeanStdResult() {
		return calcM2PttErrorsMeanStdResult;
	}
	public void setCalcM2PttErrorsMeanStdResult(CalcM2PttErrorsMeanStdResult calcM2PttErrorsMeanStdResult) {
		this.calcM2PttErrorsMeanStdResult = calcM2PttErrorsMeanStdResult;
	}
	public CalcSegmentMeanTipTiltsResult getCalcSegmentMeanTipTiltsResult() {
		return calcSegmentMeanTipTiltsResult;
	}
	public void setCalcSegmentMeanTipTiltsResult(CalcSegmentMeanTipTiltsResult calcSegmentMeanTipTiltsResult) {
		this.calcSegmentMeanTipTiltsResult = calcSegmentMeanTipTiltsResult;
	}
	
	
	
	//  AvgCentroidOffsetsDisplayValues interface
	public FloatPoint[] getAvgCentroidOffsets() {
		return calcSegmentMeanTipTiltsResult.getSegmentMeanTipTiltErrors();
	}
	
	public CentroidStatsResult getAvgCentroidStatsResult() {
		return pseudoTipTiltCentroidStatsResult;
	}
	
	public ScaleErrorResult getAvgScaleErrorResult() {
		return passiveTiltScaleErrorResult;
	}
	
	/*
	public void addFineScreenIterationOutput(FineScreenIterationOutput pio) {
		
		setCenterTelescopeCalcResult(pio.getCenterTelescopeCalcResult());
		setCentroidOffsetsResult(pio.getCentroidOffsetsResult());
		setCentroidStatsResult(pio.getCentroidStatsResult());
		setPassiveTiltScaleErrorResult(pio.getPassiveTiltScaleErrorResult());
		setFineScreenScaleErrorResult(pio.getFineScreenScaleErrorResult());
		setCalcM2M1Result(pio.getCalcM2M1Result());
		setCalcDesiredActCommandsResult(pio.getCalcDesiredActCommandsResult());

	}
	*/

	
}
