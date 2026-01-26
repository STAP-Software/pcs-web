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
    @NamedQuery(
        name = "findByProcedureType",
        query = "SELECT o FROM CentroidOffsetsConfigDefaults o " +
                "INNER JOIN FETCH o.procedureType " +  // no alias
                "INNER JOIN FETCH o.ccdType " +       // no alias
                "WHERE o.procedureType.procedureTypeId = :procedureTypeId " +
                "AND o.ccdType.ccdTypeId = :ccdTypeId"
    )
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
