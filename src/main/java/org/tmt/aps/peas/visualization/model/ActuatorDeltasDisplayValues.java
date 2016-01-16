package org.tmt.aps.peas.visualization.model;

public interface ActuatorDeltasDisplayValues {

	// uses float[][] desiredActDeltas, float desiredActDeltasRms 
	// TODO: use these to replace instances of M1ActuatorCmds, M1ActuatorCmdsRms
	public float[][] getDesiredActDeltas();

	public float getDesiredActDeltasRms();
	
}
