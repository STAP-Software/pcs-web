/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Configuration entity class representing the ProcedureConfigDefaults table.  This table is joined with the ProcedureConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "ProcedureConfigDefaults")
@PrimaryKeyJoinColumn(name="procedureConfigId")
@NamedQueries({
    @NamedQuery(
        name = "findAllProcedureConfigs",
        query = "SELECT p FROM ProcedureConfigDefaults p " +
                "INNER JOIN FETCH p.telescope " + 
                "INNER JOIN FETCH p.instrument " +
                "INNER JOIN FETCH p.procedureType " +
                "INNER JOIN FETCH p.filterType"
    ),
    @NamedQuery(
        name = "findDefaultProcedureConfig",
        query = "SELECT p FROM ProcedureConfigDefaults p " +
                "INNER JOIN FETCH p.telescope " +   // no alias
                "INNER JOIN FETCH p.instrument " +  // no alias
                "INNER JOIN FETCH p.procedureType " + 
                "INNER JOIN FETCH p.filterType " + 
                "INNER JOIN FETCH p.pupilMaskType " +
                "WHERE p.telescope.telescopeId = :telescopeId " +
                "AND p.instrument.instrumentId = :instrumentId " +
                "AND p.procedureType.procedureTypeId = :procedureTypeId"
    )
})

public class ProcedureConfigDefaults extends ProcedureConfig {

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "telescopeId")
	Telescope telescope;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instrumentId")
	Instrument instrument;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureTypeId")
	ProcedureType procedureType;

	
	public Telescope getTelescope() {
		return telescope;
	}

	public void setTelescope(Telescope telescope) {
		this.telescope = telescope;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public ProcedureType getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(ProcedureType procedureType) {
		this.procedureType = procedureType;
	}
	



	
}
