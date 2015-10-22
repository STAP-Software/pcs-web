package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;

public interface AvgFsCentroidOffsetsDisplayValues {

	ScaleErrorResult getAvgFsScaleErrorResult();
	
	public FloatPoint[] getAvgFsCentroidOffsets();
	
	public CentroidStatsResult getAvgFsCentroidStatsResult();

}
