package org.tmt.aps.peas.visualization.model;
/**
 * Interface for data required to display actuator deltas visual display.
 * Classes that implement this interface can be used to display actuator deltas.
 * @author smichaels
 */
public interface ActuatorDeltasDisplayValues {

	// uses float[][] desiredActDeltas, float desiredActDeltasRms 

	public float[][] getDesiredActDeltas();

	public float getDesiredActDeltasRms();
	
}
