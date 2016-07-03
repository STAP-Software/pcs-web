/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Visualization display Entity class representing a row in the ProcTypeVisualDisplay table
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcTypeVisualDisplay")
@NamedQueries({
	@NamedQuery(name = "findProcTypeVisualizationDisplays", query = "SELECT p from ProcTypeVisualizationDisplay p "
			+ "INNER JOIN FETCH p.visualizationDisplay vd "
			+ "where p.procedureTypeId = :procedureTypeId " )
})
public class ProcTypeVisualizationDisplay {

		
	@Id
	private Long ProcTypeVisualDisplayId;
	
	private Long procedureTypeId;
	  
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "visualizationDisplayId")
	VisualizationDisplay visualizationDisplay;

	public Long getProcTypeVisualDisplayId() {
		return ProcTypeVisualDisplayId;
	}

	public void setProcTypeVisualDisplayId(Long procTypeVisualDisplayId) {
		ProcTypeVisualDisplayId = procTypeVisualDisplayId;
	}

	public Long getProcedureTypeId() {
		return procedureTypeId;
	}

	public void setProcedureTypeId(Long procedureTypeId) {
		this.procedureTypeId = procedureTypeId;
	}

	public VisualizationDisplay getVisualizationDisplay() {
		return visualizationDisplay;
	}

	public void setVisualizationDisplay(VisualizationDisplay visualizationDisplay) {
		this.visualizationDisplay = visualizationDisplay;
	}



	
}
