package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.PassiveTiltScaleErrorResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;


public class PassiveTiltProcedureOutput extends ProcedureOutput implements CentroidOffsetsDisplayValues, ActuatorDeltasDisplayValues {
	
	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	CentroidStatsResult centroidStatsResult;
	PassiveTiltScaleErrorResult passiveTiltScaleErrorResult;
	DecomposeActsResult decomposeActsResult;
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
	public DecomposeActsResult getDecomposeActsResult() {
		return decomposeActsResult;
	}
	public void setDecomposeActsResult(DecomposeActsResult decomposeActsResult) {
		this.decomposeActsResult = decomposeActsResult;
	}
	public CalcDesiredActCommandsResult getCalcDesiredActCommandsResult() {
		return calcDesiredActCommandsResult;
	}
	public void setCalcDesiredActCommandsResult(CalcDesiredActCommandsResult calcDesiredActCommandsResult) {
		this.calcDesiredActCommandsResult = calcDesiredActCommandsResult;
	}
	
	public PassiveTiltScaleErrorResult getPassiveTiltScaleErrorResult() {
		return passiveTiltScaleErrorResult;
	}
	public ScaleErrorResult getScaleErrorResult() {
		return passiveTiltScaleErrorResult;
	}

	public void setPassiveTiltScaleErrorResult(PassiveTiltScaleErrorResult passiveTiltScaleErrorResult) {
		this.passiveTiltScaleErrorResult = passiveTiltScaleErrorResult;
	}
	public void addPassiveTiltIterationOutput(PassiveTiltIterationOutput pio) {
		
		setCenterTelescopeCalcResult(pio.getCenterTelescopeCalcResult());
		setCentroidOffsetsResult(pio.getCentroidOffsetsResult());
		setCentroidStatsResult(pio.getCentroidStatsResult());
		setPassiveTiltScaleErrorResult(pio.getPassiveTiltScaleErrorResult());
		setDecomposeActsResult(pio.getDecomposeActsResult());
		setCalcDesiredActCommandsResult(pio.getCalcDesiredActCommandsResult());

	}
	
}
