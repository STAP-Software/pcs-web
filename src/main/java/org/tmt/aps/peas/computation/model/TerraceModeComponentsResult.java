package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

public class TerraceModeComponentsResult {

	
	FloatPoint terracePiston;
	FloatPoint terraceActuator;
	
	public TerraceModeComponentsResult(FloatPoint terracePiston, FloatPoint terraceActuator) {
		this.terracePiston = terracePiston;
		this.terraceActuator = terraceActuator;
	}
	
	public TerraceModeComponentsResult() {}
	

	public FloatPoint getTerracePiston() {
		return terracePiston;
	}

	public void setTerracePiston(FloatPoint terracePiston) {
		this.terracePiston = terracePiston;
	}

	public FloatPoint getTerraceActuator() {
		return terraceActuator;
	}

	public void setTerraceActuator(FloatPoint terraceActuator) {
		this.terraceActuator = terraceActuator;
	}

	
}
