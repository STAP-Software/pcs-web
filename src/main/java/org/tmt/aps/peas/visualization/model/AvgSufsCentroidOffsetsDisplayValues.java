package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.computation.model.SufsCentroidStatsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentOffsetsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeStatsResult;

/**
 * Interface for data required to display average SUFS centroid offsets visual display.
 * Classes that implement this interface can be used to display average SUFS centroid offsets.
 * @author smichaels
 */
public interface AvgSufsCentroidOffsetsDisplayValues {
		
	public SufsSegmentOffsetsResult getSufsSegmentOffsetsResult();
	public SufsCentroidStatsResult getSufsCentroidStatsResult();
	public SufsSegmentZernikeStatsResult getSufsSegmentZernikeStatsResult();

}
