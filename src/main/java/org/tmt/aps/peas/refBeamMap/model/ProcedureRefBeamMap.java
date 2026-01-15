/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.tmt.aps.peas.procedure.model.Procedure;

/**
 * Database Entity representing a row in the ProcedureRefBeamMap table.
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcedureRefBeamMap")
@NamedQueries({
	@NamedQuery(name = "findRefBeamMapsForProcedure", query = "SELECT prbm from ProcedureRefBeamMap prbm "
			+ "INNER JOIN FETCH prbm.refBeamMap INNER JOIN FETCH prbm.procedure p where p.procedureId = :procedureId" )
})
public class ProcedureRefBeamMap {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long procedureRefBeamMapId;

	@OneToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "refBeamMapId")
	private RefBeamMap refBeamMap;

	@OneToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureId")
	private Procedure procedure;
	
	public Long getProcedureRefBeamMapId() {
		return procedureRefBeamMapId;
	}

	public void setProcedureRefBeamMapId(Long procedureRefBeamMapId) {
		this.procedureRefBeamMapId = procedureRefBeamMapId;
	}

	public RefBeamMap getRefBeamMap() {
		return refBeamMap;
	}

	public void setRefBeamMap(RefBeamMap refBeamMap) {
		this.refBeamMap = refBeamMap;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}


}
