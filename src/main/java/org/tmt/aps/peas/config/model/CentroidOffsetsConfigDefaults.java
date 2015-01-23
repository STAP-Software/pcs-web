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
@Table(name = "CentroidOffsetsConfigDefaults")
@PrimaryKeyJoinColumn(name="centroidOffsetsConfigId")
@NamedQueries({
	@NamedQuery(name = "findByProcedureType", query = "SELECT o from CentroidOffsetsConfigDefaults o INNER JOIN FETCH o.procedureType p "
			+ "where p.procedureTypeId = :procedureTypeId" )
})
public class CentroidOffsetsConfigDefaults extends CentroidOffsetsConfig {

	
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
