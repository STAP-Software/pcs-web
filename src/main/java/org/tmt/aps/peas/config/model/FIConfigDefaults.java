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
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

/**
 * Configuration entity class representing the FIConfigDefaults table.  This table is joined with the FIConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "FIConfigDefaults")
@PrimaryKeyJoinColumn(name="fiConfigId")
@NamedQueries({
    @NamedQuery(
        name = "findByMaskTypeAndInstrument",
        query = "SELECT o FROM FIConfigDefaults o " +
                "INNER JOIN FETCH o.pupilMaskType " +   // no alias
                "INNER JOIN FETCH o.instrument " +      // no alias
                "INNER JOIN FETCH o.ccdType " +         // no alias
                "WHERE o.pupilMaskType.pupilMaskTypeId = :pupilMaskTypeId " +
                "AND o.instrument.instrumentId = :instrumentId " +
                "AND o.ccdType.ccdTypeId = :ccdTypeId " +
                "AND o.lightSource = :lightSource " +
                "AND o.pupilRegProcFlg = :pupilRegProcFlg"
    )
})

public class FIConfigDefaults extends FIConfig {

	
	private int lightSource;
	private boolean pupilRegProcFlg;

	@ManyToOne
	@JoinColumn(name = "pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	@ManyToOne
	@JoinColumn(name = "instrumentId")
	private Instrument instrument;

	@ManyToOne
	@JoinColumn(name = "ccdTypeId")
	private CcdType ccdType;

	public int getLightSource() {
		return lightSource;
	}

	public void setLightSource(int lightSource) {
		this.lightSource = lightSource;
	}

	public boolean isPupilRegProcFlg() {
		return pupilRegProcFlg;
	}

	public void setPupilRegProcFlg(boolean pupilRegProcFlg) {
		this.pupilRegProcFlg = pupilRegProcFlg;
	}


	
}
