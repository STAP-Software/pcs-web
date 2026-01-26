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
 * Configuration entity class representing the AutoCenterTelConfigDefaults table.  This table is joined with the AutoCenterTelConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "AutoCenterTelConfigDefaults")
@PrimaryKeyJoinColumn(name="autoCenterTelConfigId")
@NamedQueries({
    @NamedQuery(
        name = "findAutoCenterTelConfigDefaults",
        query = "SELECT o FROM AutoCenterTelConfigDefaults o " +
                "INNER JOIN FETCH o.procedureType " +   // no alias
                "WHERE o.procedureType.procedureTypeId = :procedureTypeId"
    )
})

public class AutoCenterTelConfigDefaults extends AutoCenterTelConfig {
	
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
