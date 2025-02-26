package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.FindCentroidsResult;

/**
 * Procedure output data for Create Ref Map procedure
 * @author smichaels
 *
 */
public class CreateRefBeamMapProcedureOutput extends ProcedureOutput {

	FindCentroidsResult findCentroidsResult;
	
	private boolean mapSaved;

	public boolean isMapSaved() {
		return mapSaved;
	}

	public void setMapSaved(boolean mapSaved) {
		this.mapSaved = mapSaved;
	}

	public FindCentroidsResult getFindCentroidsResult() {
		return findCentroidsResult;
	}

	public void setFindCentroidsResult(FindCentroidsResult findCentroidsResult) {
		this.findCentroidsResult = findCentroidsResult;
	}
	
	

	
	
}
