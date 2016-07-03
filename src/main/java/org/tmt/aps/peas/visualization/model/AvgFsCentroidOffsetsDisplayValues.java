package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;

/**
 * Interface for data required to display average fine screen centroid offsets visual display.
 * Classes that implement this interface can be used to display average fine screen centroid offsets.
 * @author smichaels
 */
public interface AvgFsCentroidOffsetsDisplayValues {
	
	public FloatPoint[] getAvgFsCentroidOffsets();
	
	public int[] getGoodSpots();
	
	public CentroidStatsResult getAvgFsCentroidStatsResult();

}
