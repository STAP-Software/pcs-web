package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.computation.model.SufsCentroidStatsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentOffsetsResult;

public interface SufsCentroidOffsetsDisplayValues {

	public SufsSegmentOffsetsResult getSufsSegmentOffsetsResult();
	public SufsCentroidStatsResult getSufsCentroidStatsResult();

}
