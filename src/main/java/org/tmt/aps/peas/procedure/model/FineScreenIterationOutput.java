package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.PseudoTipTiltCentroidStatsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;

/**
 * Procedure output data for a single trial during the Fine Screen procedure
 * @author smichaels
 *
 */
public class FineScreenIterationOutput extends ProcedureIterationOutput implements CentroidOffsetsDisplayValues, ActuatorDeltasDisplayValues {


	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	CentroidStatsResult centroidStatsResult;
	CalcM2M1Result calcM2M1Result;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;
	PseudoTipTiltCentroidStatsResult pseudoTipTiltCentroidStatsResult;
	
	PupilRegErrorResult pupilRegErrorResult;
	CalcPrCommandsResult calcPrCommandsResult;
	FindCentroidsResult findCentroidsResult;
	
	
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
	public PseudoTipTiltCentroidStatsResult getPseudoTipTiltCentroidStatsResult() {
		return pseudoTipTiltCentroidStatsResult;
	}
	public void setPseudoTipTiltCentroidStatsResult(PseudoTipTiltCentroidStatsResult pseudoTipTiltCentroidStatsResult) {
		this.pseudoTipTiltCentroidStatsResult = pseudoTipTiltCentroidStatsResult;
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
	public FindCentroidsResult getFindCentroidsResult() {
		return findCentroidsResult;
	}
	public void setFindCentroidsResult(FindCentroidsResult findCentroidsResult) {
		this.findCentroidsResult = findCentroidsResult;
	}
	
	// actuator deltas display values

	public float[][] getDesiredActDeltas() {
		return calcDesiredActCommandsResult.getDesiredActDeltas();
	}
	public float getDesiredActDeltasRms() {
		return calcDesiredActCommandsResult.getDesiredActDeltasRms();
	}
	
	
	
}
