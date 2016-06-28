package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for colorStepToActuators computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#colorStepToActuators(float[], int[])
 */
public class ColorStepToActuatorsResult {

	
	float[] m1ActuatorDeltas;

	
	public ColorStepToActuatorsResult(float[] m1ActuatorDeltas) {

		this.m1ActuatorDeltas = m1ActuatorDeltas;
	}


	public ColorStepToActuatorsResult() {}


	public float[] getM1ActuatorDeltas() {
		return m1ActuatorDeltas;
	}


	public void setM1ActuatorDeltas(float[] m1ActuatorDeltas) {
		this.m1ActuatorDeltas = m1ActuatorDeltas;
	}




	

	
}
