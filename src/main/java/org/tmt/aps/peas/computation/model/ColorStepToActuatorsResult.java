package org.tmt.aps.peas.computation.model;

import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;

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
