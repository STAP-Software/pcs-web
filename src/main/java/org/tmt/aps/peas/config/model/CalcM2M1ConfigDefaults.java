/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.tmt.aps.peas.procedure.model.ProcedureType;

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
