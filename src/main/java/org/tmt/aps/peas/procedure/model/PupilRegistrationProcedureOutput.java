package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;

// TODO - display i/fs need to change
public class PupilRegistrationProcedureOutput extends ProcedureOutput implements CentroidOffsetsDisplayValues {
	
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
	

	public void addPupilRegistrationIterationOutput(PupilRegistrationIterationOutput pio) {
		
		setCenterTelescopeCalcResult(pio.getCenterTelescopeCalcResult());
		setCentroidOffsetsResult(pio.getCentroidOffsetsResult());
		setCentroidStatsResult(pio.getCentroidStatsResult());
		setScaleErrorResult(pio.getScaleErrorResult());
		
		setPupilRegErrorResult(pio.getPupilRegErrorResult());
		setCalcPrCommandsResult(pio.getCalcPrCommandsResult());
	}
	
}
