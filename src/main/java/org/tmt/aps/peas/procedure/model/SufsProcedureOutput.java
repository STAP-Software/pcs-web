package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.AvgCentroidStatsResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.visualization.model.AvgSufsCentroidOffsetsDisplayValues;


public class SufsProcedureOutput extends ProcedureOutput implements AvgSufsCentroidOffsetsDisplayValues {
	
	CentroidOffsetsResult centroidOffsetsResult;
	AvgCentroidStatsResult avgCentroidStatsResult;
	
	
	public CentroidOffsetsResult getCentroidOffsetsResult() {
		return centroidOffsetsResult;
	}
	public void setCentroidOffsetsResult(CentroidOffsetsResult centroidOffsetsResult) {
		this.centroidOffsetsResult = centroidOffsetsResult;
	}

	public AvgCentroidStatsResult getAvgCentroidStatsResult() {
		return avgCentroidStatsResult;
	}
	public void setAvgCentroidStatsResult(AvgCentroidStatsResult avgCentroidStatsResult) {
		this.avgCentroidStatsResult = avgCentroidStatsResult;
	}	
	

	//  AvgSufsCentroidOffsetsDisplayValues interface
	public FloatPoint[] getAvgSufsCentroidOffsets() {
		return centroidOffsetsResult.getCartesianCentroidOffsets();
	}
	
	public CentroidStatsResult getAvgSufsCentroidStatsResult() {
		return avgCentroidStatsResult;
	}
	

	
}
