package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.computation.model.PhasingStatsResult;

/**
 * Interface for data required to display edge heights visual display.
 * Classes that implement this interface can be used to display edge heights.
 * @author smichaels
 */
public interface EdgeHeightsDisplayValues {

	public BbAnalyzeSequenceResult getBbAnalyzeSequenceResult();
	public PhasingStatsResult getPhasingStatsResult();

}
