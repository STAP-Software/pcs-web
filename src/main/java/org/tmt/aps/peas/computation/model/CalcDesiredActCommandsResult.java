package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.Constants;

public class CalcDesiredActCommandsResult {

	
	float[][] pistonActs;
	float pistonActsRms;
	float[][] desiredActDeltas;
	float desiredActDeltasRms;
	float desiredActDeltasFmRms;
	float desiredActDeltasNoFmRms;
	
	
	public CalcDesiredActCommandsResult(float[][] pistonActs, float pistonActsRms, float[][] desiredActDeltas, float desiredActDeltasRms, float desiredActDeltasFmRms,
	float desiredActDeltasNoFmRms) {

		this.pistonActs = pistonActs;
		this.pistonActsRms = pistonActsRms;
		this.desiredActDeltas = desiredActDeltas;
		this.desiredActDeltasRms = desiredActDeltasRms;
		this.desiredActDeltasFmRms = desiredActDeltasFmRms;
		this.desiredActDeltasNoFmRms = desiredActDeltasNoFmRms;

	}

	public CalcDesiredActCommandsResult(FixPistonsResult fixPistonsResult) {

		this.pistonActs = new float[36][3];
		this.desiredActDeltas = new float[36][3];

		if (fixPistonsResult != null && fixPistonsResult.getActFixed() != null) {
			for (int i=0; i<36; i++) {
				this.desiredActDeltas[i][0] = Constants.MICRONS_TO_NM * fixPistonsResult.getActFixed()[i*3];  
				this.desiredActDeltas[i][1] = Constants.MICRONS_TO_NM * fixPistonsResult.getActFixed()[i*3 + 1];  
				this.desiredActDeltas[i][2] = Constants.MICRONS_TO_NM * fixPistonsResult.getActFixed()[i*3 + 2];  
			}
		}
		this.desiredActDeltasRms = fixPistonsResult.getActRms();
	}

	public CalcDesiredActCommandsResult() {
		
		this.pistonActs = new float[36][3];
		this.desiredActDeltas = new float[36][3];

	};

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

	public float getDesiredActDeltasFmRms() {
		return desiredActDeltasFmRms;
	}

	public void setDesiredActDeltasFmRms(float desiredActDeltasFmRms) {
		this.desiredActDeltasFmRms = desiredActDeltasFmRms;
	}

	public float getDesiredActDeltasNoFmRms() {
		return desiredActDeltasNoFmRms;
	}

	public void setDesiredActDeltasNoFmRms(float desiredActDeltasNoFmRms) {
		this.desiredActDeltasNoFmRms = desiredActDeltasNoFmRms;
	}


}
