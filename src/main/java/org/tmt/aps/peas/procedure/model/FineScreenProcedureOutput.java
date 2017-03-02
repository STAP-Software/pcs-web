package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.AvgCentroidStatsResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActDeltasRmsEomResult;
import org.tmt.aps.peas.computation.model.CalcM2ActuatorsFromPttResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CalcM2PttErrorsMeanEomResult;
import org.tmt.aps.peas.computation.model.CalcSegmentMeanTipTiltsResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.PseudoTipTiltCentroidStatsResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.AvgFsCentroidOffsetsDisplayValues;
import org.tmt.aps.peas.visualization.model.AvgPtCentroidOffsetsDisplayValues;

/**
 * Procedure output data (not including trial specific data) for Fine Screen procedure
 * @author smichaels
 *
 */
public class FineScreenProcedureOutput extends ProcedureOutput implements AvgPtCentroidOffsetsDisplayValues, AvgFsCentroidOffsetsDisplayValues, ActuatorDeltasDisplayValues {
	
	CentroidOffsetsResult centroidOffsetsResult;
	PseudoTipTiltCentroidStatsResult pseudoTipTiltCentroidStatsResult;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;
	CalcM2M1Result calcM2M1Result;
	CalcM2ActuatorsFromPttResult calcM2ActuatorsFromPttResult;
	CalcDesiredActDeltasRmsEomResult calcDesiredActDeltasRmsEomResult;
	CalcM2PttErrorsMeanEomResult calcM2PttErrorsMeanEomResult;
	CalcSegmentMeanTipTiltsResult calcSegmentMeanTipTiltsResult;
	AvgCentroidStatsResult avgCentroidStatsResult;
	DecomposeActsResult  decomposeActsResult;
	
	
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
	public CalcDesiredActDeltasRmsEomResult getCalcDesiredActDeltasRmsEomResult() {
		return calcDesiredActDeltasRmsEomResult;
	}
	public void setCalcDesiredActDeltasRmsEomResult(CalcDesiredActDeltasRmsEomResult calcDesiredActDeltasRmsEomResult) {
		this.calcDesiredActDeltasRmsEomResult = calcDesiredActDeltasRmsEomResult;
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
	
	public DecomposeActsResult getDecomposeActsResult() {
		return decomposeActsResult;
	}
	public void setDecomposeActsResult(DecomposeActsResult decomposeActsResult) {
		this.decomposeActsResult = decomposeActsResult;
	}

	// actuator deltas display values
	
	public float[][] getDesiredActDeltas() {
		return calcDesiredActCommandsResult.getDesiredActDeltas();
	}
	public float getDesiredActDeltasRms() {
		return calcDesiredActCommandsResult.getDesiredActDeltasRms();
	}
	
	//  AvgPtCentroidOffsetsDisplayValues interface
	public FloatPoint[] getAvgPtCentroidOffsets() {
		return calcSegmentMeanTipTiltsResult.getSegmentMeanTipTiltErrors();
	}
	
	public CentroidStatsResult getAvgPtCentroidStatsResult() {
		return pseudoTipTiltCentroidStatsResult;
	}
	
	public int[] getGoodSpots() {
		return centroidOffsetsResult.getGoodSpots();
	}
		
	//  AvgFsCentroidOffsetsDisplayValues interface
	public FloatPoint[] getAvgFsCentroidOffsets() {
		return centroidOffsetsResult.getCartesianCentroidOffsets();
	}
	
	public CentroidStatsResult getAvgFsCentroidStatsResult() {
		return avgCentroidStatsResult;
	}
	

	
}
