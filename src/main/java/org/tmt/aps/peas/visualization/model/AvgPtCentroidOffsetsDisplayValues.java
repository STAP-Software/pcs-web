package org.tmt.aps.peas.visualization.model;

/**
 * Interface for data required to display average passive tilt centroid offsets visual display.
 * Classes that implement this interface can be used to display average passive tilt centroid offsets.
 * @author smichaels
 */
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;

public interface AvgPtCentroidOffsetsDisplayValues {
	
	public FloatPoint[] getAvgPtCentroidOffsets();
	
	public CentroidStatsResult getAvgPtCentroidStatsResult();

}
