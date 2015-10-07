package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.FineScreenScaleErrorResult;
import org.tmt.aps.peas.computation.model.PassiveTiltScaleErrorResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;


public class FineScreenProcedureOutput extends ProcedureOutput implements CentroidOffsetsDisplayValues, ActuatorDeltasDisplayValues {
	
	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	CentroidStatsResult centroidStatsResult;
	FineScreenScaleErrorResult FineScreenScaleErrorResult;
	PassiveTiltScaleErrorResult PassiveTiltScaleErrorResult;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;
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

	public void addFineScreenIterationOutput(FineScreenIterationOutput pio) {
		
		setCenterTelescopeCalcResult(pio.getCenterTelescopeCalcResult());
		setCentroidOffsetsResult(pio.getCentroidOffsetsResult());
		setCentroidStatsResult(pio.getCentroidStatsResult());
		setPassiveTiltScaleErrorResult(pio.getPassiveTiltScaleErrorResult());
		setFineScreenScaleErrorResult(pio.getFineScreenScaleErrorResult());
		setCalcM2M1Result(pio.getCalcM2M1Result());
		setCalcDesiredActCommandsResult(pio.getCalcDesiredActCommandsResult());

	}

	
}
