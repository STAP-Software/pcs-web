/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
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

/**
 * Configuration entity class representing the FIConfigDefaults table.  This table is joined with the FIConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "FIConfigDefaults")
@PrimaryKeyJoinColumn(name="fiConfigId")
@NamedQueries({ @NamedQuery(name = "findByMaskTypeAndInstrument", query = "SELECT o from FIConfigDefaults o INNER JOIN FETCH o.pupilMaskType p INNER JOIN FETCH o.instrument i "
		+ "where p.pupilMaskTypeId = :pupilMaskTypeId and i.instrumentId = :instrumentId and o.lightSource = :lightSource") })
public class FIConfigDefaults extends FIConfig {

	
	private int lightSource;

	@ManyToOne
	@JoinColumn(name = "pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	@ManyToOne
	@JoinColumn(name = "instrumentId")
	private Instrument instrument;

	public int getLightSource() {
		return lightSource;
	}

	public void setLightSource(int lightSource) {
		this.lightSource = lightSource;
	}


	
}
