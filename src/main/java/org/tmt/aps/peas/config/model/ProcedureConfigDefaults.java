/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

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
	@NamedQuery(name = "findAllProcedureConfigs", query = "SELECT p from ProcedureConfigDefaults p INNER JOIN FETCH p.telescope INNER JOIN FETCH p.instrument "
			+ "INNER JOIN FETCH p.procedureType INNER JOIN FETCH p.filterType "),
	@NamedQuery(name = "findDefaultProcedureConfig", query = "SELECT p from ProcedureConfigDefaults p INNER JOIN FETCH p.telescope tel INNER JOIN FETCH p.instrument inst "
			+ "INNER JOIN FETCH p.procedureType pt  INNER JOIN FETCH p.filterType  INNER JOIN FETCH p.pupilMaskType "
			+ "WHERE tel.telescopeId = :telescopeId AND inst.instrumentId = :instrumentId AND pt.procedureTypeId = :procedureTypeId ")
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
