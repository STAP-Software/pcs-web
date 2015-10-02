package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;

public interface ActuatorDeltasDisplayValues {

	// uses float[][] desiredActDeltas, float desiredActDeltasRms 
	// TODO: use these to replace instances of M1ActuatorCmds, M1ActuatorCmdsRms
	public CalcDesiredActCommandsResult getCalcDesiredActCommandsResult();
	
}
