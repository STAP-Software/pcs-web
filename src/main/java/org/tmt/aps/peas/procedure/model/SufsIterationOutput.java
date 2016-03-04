package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.SufsCentroidStatsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentOffsetsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeResult;
import org.tmt.aps.peas.visualization.model.SufsCentroidOffsetsDisplayValues;

public class SufsIterationOutput extends ProcedureIterationOutput implements SufsCentroidOffsetsDisplayValues {


	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	CentroidStatsResult centroidStatsResult;;
	FindCentroidsResult findCentroidsResult;
	SufsSegmentOffsetsResult sufsSegmentOffsetsResult;
	SufsCentroidStatsResult sufsCentroidStatsResult;
	SufsSegmentZernikeResult sufsSegmentZernikeResult;
	
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
	
	
	
	public SufsSegmentOffsetsResult getSufsSegmentOffsetsResult() {
		return sufsSegmentOffsetsResult;
	}
	public void setSufsSegmentOffsetsResult(SufsSegmentOffsetsResult sufsSegmentOffsetsResult) {
		this.sufsSegmentOffsetsResult = sufsSegmentOffsetsResult;
	}
	
	public SufsCentroidStatsResult getSufsCentroidStatsResult() {
		return sufsCentroidStatsResult;
	}
	public void setSufsCentroidStatsResult(SufsCentroidStatsResult sufsCentroidStatsResult) {
		this.sufsCentroidStatsResult = sufsCentroidStatsResult;
	}
	
	
	public SufsSegmentZernikeResult getSufsSegmentZernikeResult() {
		return sufsSegmentZernikeResult;
	}
	public void setSufsSegmentZernikeResult(SufsSegmentZernikeResult sufsSegmentZernikeResult) {
		this.sufsSegmentZernikeResult = sufsSegmentZernikeResult;
	}
	

	
	
	
}
