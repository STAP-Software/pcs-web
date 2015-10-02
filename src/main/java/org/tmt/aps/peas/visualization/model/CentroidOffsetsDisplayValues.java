package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.ScaleErrorResult;

public interface CentroidOffsetsDisplayValues {

	public CentroidOffsetsResult getCentroidOffsetsResult();
	public ScaleErrorResult getScaleErrorResult();
	public CentroidStatsResult getCentroidStatsResult();

}
