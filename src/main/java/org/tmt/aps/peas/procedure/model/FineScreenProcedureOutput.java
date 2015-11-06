package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.AvgCentroidStatsResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActDeltasRmsStdResult;
import org.tmt.aps.peas.computation.model.CalcM2ActuatorsFromPttResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CalcM2PttErrorsMeanEomResult;
import org.tmt.aps.peas.computation.model.CalcSegmentMeanTipTiltsResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.FineScreenScaleErrorResult;
import org.tmt.aps.peas.computation.model.PassiveTiltScaleErrorResult;
import org.tmt.aps.peas.computation.model.PseudoTipTiltCentroidStatsResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.AvgFsCentroidOffsetsDisplayValues;
import org.tmt.aps.peas.visualization.model.AvgPtCentroidOffsetsDisplayValues;


public class FineScreenProcedureOutput extends ProcedureOutput implements AvgPtCentroidOffsetsDisplayValues, AvgFsCentroidOffsetsDisplayValues, ActuatorDeltasDisplayValues {
	
	CentroidOffsetsResult centroidOffsetsResult;
	PseudoTipTiltCentroidStatsResult pseudoTipTiltCentroidStatsResult;
	FineScreenScaleErrorResult fineScreenScaleErrorResult;
	PassiveTiltScaleErrorResult passiveTiltScaleErrorResult;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;
	CalcM2M1Result calcM2M1Result;
	CalcM2ActuatorsFromPttResult calcM2ActuatorsFromPttResult;
	CalcDesiredActDeltasRmsStdResult calcDesiredActDeltasRmsStdResult;
	CalcM2PttErrorsMeanEomResult calcM2PttErrorsMeanEomResult;
	CalcSegmentMeanTipTiltsResult calcSegmentMeanTipTiltsResult;
	AvgCentroidStatsResult avgCentroidStatsResult;
	
	
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
	public AvgCentroidStatsResult getAvgCentroidStatsResult() {
		return avgCentroidStatsResult;
	}
	public void setAvgCentroidStatsResult(AvgCentroidStatsResult avgCentroidStatsResult) {
		this.avgCentroidStatsResult = avgCentroidStatsResult;
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
	
	public CalcM2PttErrorsMeanEomResult getCalcM2PttErrorsMeanEomResult() {
		return calcM2PttErrorsMeanEomResult;
	}
	public void setCalcM2PttErrorsMeanEomResult(CalcM2PttErrorsMeanEomResult calcM2PttErrorsMeanEomResult) {
		this.calcM2PttErrorsMeanEomResult = calcM2PttErrorsMeanEomResult;
	}
	public CalcSegmentMeanTipTiltsResult getCalcSegmentMeanTipTiltsResult() {
		return calcSegmentMeanTipTiltsResult;
	}
	public void setCalcSegmentMeanTipTiltsResult(CalcSegmentMeanTipTiltsResult calcSegmentMeanTipTiltsResult) {
		this.calcSegmentMeanTipTiltsResult = calcSegmentMeanTipTiltsResult;
	}
	
	
	
	//  AvgPtCentroidOffsetsDisplayValues interface
	public FloatPoint[] getAvgPtCentroidOffsets() {
		return calcSegmentMeanTipTiltsResult.getSegmentMeanTipTiltErrors();
	}
	
	public CentroidStatsResult getAvgPtCentroidStatsResult() {
		return pseudoTipTiltCentroidStatsResult;
	}
	
	public ScaleErrorResult getAvgPtScaleErrorResult() {
		return passiveTiltScaleErrorResult;
	}
	
	//  AvgFsCentroidOffsetsDisplayValues interface
	public FloatPoint[] getAvgFsCentroidOffsets() {
		return centroidOffsetsResult.getCartesianCentroidOffsets();
	}
	
	public CentroidStatsResult getAvgFsCentroidStatsResult() {
		return avgCentroidStatsResult;
	}
	
	public ScaleErrorResult getAvgFsScaleErrorResult() {
		return fineScreenScaleErrorResult;
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
