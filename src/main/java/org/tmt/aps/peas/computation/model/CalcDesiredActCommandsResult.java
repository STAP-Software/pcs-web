package org.tmt.aps.peas.computation.model;

public class CalcDesiredActCommandsResult {

	
	float[][] pistonActs;
	float pistonActsRms;
	float[][] desiredActDeltas;
	float desiredActDeltasRms;
	
	
	public CalcDesiredActCommandsResult(float[][] pistonActs, float pistonActsRms, float[][] desiredActDeltas, float desiredActDeltasRms) {

		this.pistonActs = pistonActs;
		this.pistonActsRms = pistonActsRms;
		this.desiredActDeltas = desiredActDeltas;
		this.desiredActDeltasRms = desiredActDeltasRms;
	}


	public float[][] getPistonActs() {
		return pistonActs;
	}


	public void setPistonActs(float[][] pistonActs) {
		this.pistonActs = pistonActs;
	}


	public float getPistonActsRms() {
		return pistonActsRms;
	}


	public void setPistonActsRms(float pistonActsRms) {
		this.pistonActsRms = pistonActsRms;
	}


	public float[][] getDesiredActDeltas() {
		return desiredActDeltas;
	}


	public void setDesiredActDeltas(float[][] desiredActDeltas) {
		this.desiredActDeltas = desiredActDeltas;
	}


	public float getDesiredActDeltasRms() {
		return desiredActDeltasRms;
	}


	public void setDesiredActDeltasRms(float desiredActDeltasRms) {
		this.desiredActDeltasRms = desiredActDeltasRms;
	}


}
