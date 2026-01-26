/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Configuration entity class representing the GlobalConfigDefaults table.  This table is joined with the GlobalConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "GlobalConfigDefaults")
@PrimaryKeyJoinColumn(name="globalConfigId")
@NamedQueries({
    @NamedQuery(
        name = "findDefaultConfig",
        query = "SELECT g FROM GlobalConfigDefaults g " +
                "INNER JOIN FETCH g.telescope " +   // no alias
                "INNER JOIN FETCH g.instrument " +  // no alias
                "WHERE g.telescope.telescopeId = :telescopeId " +
                "AND g.instrument.instrumentId = :instrumentId"
    )
})

public class GlobalConfigDefaults extends GlobalConfig {

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "telescopeId")
	Telescope telescope;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instrumentId")
	Instrument instrument;

	public GlobalConfigDefaults() {
		
	}
	
	public GlobalConfigDefaults(GlobalConfig globalConfig) {
		super(globalConfig);
	}
	
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

	
}
