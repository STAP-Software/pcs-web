package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;

public interface AvgCentroidOffsetsDisplayValues {

	ScaleErrorResult getAvgScaleErrorResult();
	
	public FloatPoint[] getAvgCentroidOffsets();
	
	public CentroidStatsResult getAvgCentroidStatsResult();

}
