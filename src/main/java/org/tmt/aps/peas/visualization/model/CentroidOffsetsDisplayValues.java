package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.StartupComputationsResult;

/**
 * Interface for data required to display centroid offsets visual display.
 * Classes that implement this interface can be used to display centroid offsets.
 * @author smichaels
 */
public interface CentroidOffsetsDisplayValues {

	public CentroidOffsetsResult getCentroidOffsetsResult();
	public CentroidStatsResult getCentroidStatsResult();

}
