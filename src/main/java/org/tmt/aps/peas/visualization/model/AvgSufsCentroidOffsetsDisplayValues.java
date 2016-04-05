package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.computation.model.SufsCentroidStatsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentOffsetsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeStatsResult;

public interface AvgSufsCentroidOffsetsDisplayValues {
		
	public SufsSegmentOffsetsResult getSufsSegmentOffsetsResult();
	public SufsCentroidStatsResult getSufsCentroidStatsResult();
	public SufsSegmentZernikeStatsResult getSufsSegmentZernikeStatsResult();

}
