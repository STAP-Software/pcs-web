/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.telescope.model.Telescope;

@Entity
@Table(name = "GlobalConfigDefaults")
@PrimaryKeyJoinColumn(name="globalConfigId")
@NamedQueries({
	@NamedQuery(name = "findDefaultConfig", query = "SELECT g from GlobalConfigDefaults g INNER JOIN FETCH g.telescope tel INNER JOIN FETCH g.instrument inst "
			+ "WHERE tel.telescopeId = :telescopeId AND inst.instrumentId = :instrumentId ")

})public class GlobalConfigDefaults extends GlobalConfig {

	
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
