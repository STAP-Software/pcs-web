/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Visualization display Entity class representing a row in the ProcTypeVisualDisplay table
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcTypeVisualDisplay")
@NamedQueries({
    @NamedQuery(
        name = "findProcTypeVisualizationDisplays",
        query = "SELECT p FROM ProcTypeVisualizationDisplay p " +
                "INNER JOIN FETCH p.visualizationDisplay " +  // no alias
                "WHERE p.procedureTypeId = :procedureTypeId"
    )
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
