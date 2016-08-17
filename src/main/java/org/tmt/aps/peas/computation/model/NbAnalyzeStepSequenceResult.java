package org.tmt.aps.peas.computation.model;

public class NbAnalyzeStepSequenceResult {

	
	int[] rowFlagOut;
	float[] stepTable;
	float[][][] indexTable;
	
	float edgeErrorSteps;
	float edgeErrorMicrons;
	float lineSlopeAvg;

	public NbAnalyzeStepSequenceResult(int[] rowFlagOut, float[] stepTable, float[][][] indexTable, 
			float edgeErrorSteps, float edgeErrorMicrons, float lineSlopeAvg) {
		
		this.rowFlagOut = rowFlagOut;
		this.stepTable = stepTable;
		this.indexTable = indexTable;
		this.edgeErrorSteps = edgeErrorSteps;
		this.edgeErrorMicrons = edgeErrorMicrons;
		this.lineSlopeAvg = lineSlopeAvg;
				
	}

	public int[] getRowFlagOut() {
		return rowFlagOut;
	}

	public void setRowFlagOut(int[] rowFlagOut) {
		this.rowFlagOut = rowFlagOut;
	}

	public float[] getStepTable() {
		return stepTable;
	}

	public void setStepTable(float[] stepTable) {
		this.stepTable = stepTable;
	}

	public float[][][] getIndexTable() {
		return indexTable;
	}

	public void setIndexTable(float[][][] indexTable) {
		this.indexTable = indexTable;
	}

	public float getEdgeErrorSteps() {
		return edgeErrorSteps;
	}

	public void setEdgeErrorSteps(float edgeErrorSteps) {
		this.edgeErrorSteps = edgeErrorSteps;
	}

	public float getEdgeErrorMicrons() {
		return edgeErrorMicrons;
	}

	public void setEdgeErrorMicrons(float edgeErrorMicrons) {
		this.edgeErrorMicrons = edgeErrorMicrons;
	}

	public float getLineSlopeAvg() {
		return lineSlopeAvg;
	}

	public void setLineSlopeAvg(float lineSlopeAvg) {
		this.lineSlopeAvg = lineSlopeAvg;
	}



}
