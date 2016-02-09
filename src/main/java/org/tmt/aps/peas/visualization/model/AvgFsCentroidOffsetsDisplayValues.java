package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;

public interface AvgFsCentroidOffsetsDisplayValues {
	
	public FloatPoint[] getAvgFsCentroidOffsets();
	
	public CentroidStatsResult getAvgFsCentroidStatsResult();

}
