package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;

public class SufsIterationOutput extends ProcedureIterationOutput implements CentroidOffsetsDisplayValues {


	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	CentroidStatsResult centroidStatsResult;;
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
	

	public FindCentroidsResult getFindCentroidsResult() {
		return findCentroidsResult;
	}
	public void setFindCentroidsResult(FindCentroidsResult findCentroidsResult) {
		this.findCentroidsResult = findCentroidsResult;
	}
	
	
}
