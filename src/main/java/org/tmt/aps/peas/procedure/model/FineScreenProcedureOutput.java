package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcM2ActuatorsFromPttResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.FineScreenScaleErrorResult;
import org.tmt.aps.peas.computation.model.PassiveTiltScaleErrorResult;
import org.tmt.aps.peas.computation.model.PseudoTipTiltCentroidStatsResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;


public class FineScreenProcedureOutput extends ProcedureOutput implements ActuatorDeltasDisplayValues {
	
	CentroidOffsetsResult centroidOffsetsResult;
	PseudoTipTiltCentroidStatsResult pseudoTipTiltCentroidStatsResult;
	FineScreenScaleErrorResult FineScreenScaleErrorResult;
	PassiveTiltScaleErrorResult PassiveTiltScaleErrorResult;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;
	CalcM2M1Result calcM2M1Result;
	CalcM2ActuatorsFromPttResult calcM2ActuatorsFromPttResult;

	
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
		return FineScreenScaleErrorResult;
	}
	public void setFineScreenScaleErrorResult(FineScreenScaleErrorResult fineScreenScaleErrorResult) {
		FineScreenScaleErrorResult = fineScreenScaleErrorResult;
	}
	public PassiveTiltScaleErrorResult getPassiveTiltScaleErrorResult() {
		return PassiveTiltScaleErrorResult;
	}
	public void setPassiveTiltScaleErrorResult(PassiveTiltScaleErrorResult passiveTiltScaleErrorResult) {
		PassiveTiltScaleErrorResult = passiveTiltScaleErrorResult;
	}
	
	public ScaleErrorResult getScaleErrorResult() {
		return FineScreenScaleErrorResult;
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
