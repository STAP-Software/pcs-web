/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.tmt.aps.peas.procedure.model.Procedure;

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
