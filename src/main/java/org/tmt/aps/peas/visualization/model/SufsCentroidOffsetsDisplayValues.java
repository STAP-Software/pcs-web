package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.computation.model.SufsCentroidStatsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentOffsetsResult;

/**
 * Interface for data required to display SUFS centroid offsets visual display.
 * Classes that implement this interface can be used to display SUFS centroid offsets.
 * @author smichaels
 */
public interface SufsCentroidOffsetsDisplayValues {

	public SufsSegmentOffsetsResult getSufsSegmentOffsetsResult();
	public SufsCentroidStatsResult getSufsCentroidStatsResult();

}
