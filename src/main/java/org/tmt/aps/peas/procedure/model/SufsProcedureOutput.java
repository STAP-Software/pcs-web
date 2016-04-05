package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.SufsCentroidStatsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentOffsetsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeStatsResult;
import org.tmt.aps.peas.visualization.model.SufsCentroidOffsetsDisplayValues;


public class SufsProcedureOutput extends ProcedureOutput implements SufsCentroidOffsetsDisplayValues {
	
	SufsSegmentOffsetsResult sufsSegmentOffsetsResult;
	SufsCentroidStatsResult sufsCentroidStatsResult;
	SufsSegmentZernikeStatsResult sufsSegmentZernikeStatsResult;
	
	
	public SufsSegmentOffsetsResult getSufsSegmentOffsetsResult() {
		return sufsSegmentOffsetsResult;
	}

	public void setSufsSegmentOffsetsResult(SufsSegmentOffsetsResult sufsSegmentOffsetsResult) {
		this.sufsSegmentOffsetsResult = sufsSegmentOffsetsResult;
	}

	public void setSufsCentroidStatsResult(SufsCentroidStatsResult sufsCentroidStatsResult) {
		this.sufsCentroidStatsResult = sufsCentroidStatsResult;
	}

	public SufsCentroidStatsResult getSufsCentroidStatsResult() {
		return sufsCentroidStatsResult;
	}

	public SufsSegmentZernikeStatsResult getSufsSegmentZernikeStatsResult() {
		return sufsSegmentZernikeStatsResult;
	}

	public void setSufsSegmentZernikeStatsResult(SufsSegmentZernikeStatsResult sufsSegmentZernikeStatsResult) {
		this.sufsSegmentZernikeStatsResult = sufsSegmentZernikeStatsResult;
	}
	

	
}
