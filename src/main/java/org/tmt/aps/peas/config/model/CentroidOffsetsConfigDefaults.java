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

import org.tmt.aps.peas.instrument.model.CcdType;
import org.tmt.aps.peas.procedure.model.ProcedureType;

/**
 * Configuration entity class representing the CentroidOffsetsConfigDefaults table.  This table is joined with the CentroidOffsetsConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "CentroidOffsetsConfigDefaults")
@PrimaryKeyJoinColumn(name="centroidOffsetsConfigId")
@NamedQueries({
	@NamedQuery(name = "findByProcedureType", query = "SELECT o from CentroidOffsetsConfigDefaults o "
			+ "INNER JOIN FETCH o.procedureType p INNER JOIN FETCH o.ccdType t "
			+ "where p.procedureTypeId = :procedureTypeId and t.ccdTypeId = :ccdTypeId" )
})
public class CentroidOffsetsConfigDefaults extends CentroidOffsetsConfig {

	
	@ManyToOne
	@JoinColumn(name = "procedureTypeId")
	private ProcedureType procedureType;

	@ManyToOne
	@JoinColumn(name = "ccdTypeId")
	private CcdType ccdType;

	
	public ProcedureType getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(ProcedureType procedureType) {
		this.procedureType = procedureType;
	}


	
}
