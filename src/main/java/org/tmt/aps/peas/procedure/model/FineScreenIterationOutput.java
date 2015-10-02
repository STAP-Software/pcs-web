package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;

public class FineScreenIterationOutput extends ProcedureIterationOutput implements CentroidOffsetsDisplayValues, ActuatorDeltasDisplayValues {


	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	CentroidStatsResult centroidStatsResult;
	ScaleErrorResult scaleErrorResult;
	CalcM2M1Result calcM2M1Result;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;
	
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
	public CalcDesiredActCommandsResult getCalcDesiredActCommandsResult() {
		return calcDesiredActCommandsResult;
	}
	public void setCalcDesiredActCommandsResult(CalcDesiredActCommandsResult calcDesiredActCommandsResult) {
		this.calcDesiredActCommandsResult = calcDesiredActCommandsResult;
	}
	
}
