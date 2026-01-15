/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import org.tmt.aps.peas.procedure.model.ProcedureType;

/**
 * Configuration entity class representing the CalcM2M1ConfigDefaults table.  This table is joined with the CalcM2M1Config table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "CalcM2M1ConfigDefaults")
@PrimaryKeyJoinColumn(name="calcM2M1ConfigId")
@NamedQueries({
	@NamedQuery(name = "calcM2M1Config.findByProcedureType", query = "SELECT o from CalcM2M1ConfigDefaults o INNER JOIN FETCH o.procedureType p "
			+ "where p.procedureTypeId = :procedureTypeId" )
})
public class CalcM2M1ConfigDefaults extends CalcM2M1Config {

	
	@ManyToOne
	@JoinColumn(name = "procedureTypeId")
	private ProcedureType procedureType;

	public ProcedureType getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(ProcedureType procedureType) {
		this.procedureType = procedureType;
	}
	
}
