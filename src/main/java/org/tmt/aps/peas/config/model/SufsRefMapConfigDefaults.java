/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.tmt.aps.peas.instrument.model.CcdType;
import org.tmt.aps.peas.instrument.model.Instrument;


/** 
 * Configuration entity class representing the RefMapConfigDefaults table.  This information does not need to be stored with a procedure 
 * (it already is as part of the {@link ProcedureConfig}), and hence this does not conform to the standard inheritance model and there is no 
 * corresponding RefMapConfig table.
 * @author smichaels
 *
 */
@Entity
@Table(name = "SufsRefMapConfigDefaults")
@NamedQueries({
    @NamedQuery(
        name = "findByRefBeamNum",
        query = "SELECT o FROM SufsRefMapConfigDefaults o " +
                "INNER JOIN FETCH o.instrument " +   // no alias
                "INNER JOIN FETCH o.ccdType " +     // no alias
                "WHERE o.instrument.instrumentId = :instrumentId " +
                "AND o.ccdType.ccdTypeId = :ccdTypeId " +
                "AND o.referenceBeamNum = :referenceBeamNum"
    )
})

public class SufsRefMapConfigDefaults {

	@Id
	@SequenceGenerator(
		    name = "sufsRefMapConfigDefault_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
	)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "sufsRefMapConfigDefault_gen"
	)

	private Long sufsRefMapConfigDefaultId;

	float integrationTime; 
	int referenceBeamNum;
	int ccdGainNumber;
	

	@ManyToOne
	@JoinColumn (name="instrumentId")
	private Instrument instrument;

	@ManyToOne
	@JoinColumn (name="ccdTypeId")
	private CcdType ccdType;

	
	
	public Long getSufsRefMapConfigDefaultId() {
		return sufsRefMapConfigDefaultId;
	}

	public void setSufsRefMapConfigDefaultId(Long sufsRefMapConfigDefaultId) {
		this.sufsRefMapConfigDefaultId = sufsRefMapConfigDefaultId;
	}

	public float getIntegrationTime() {
		return integrationTime;
	}

	public void setIntegrationTime(float integrationTime) {
		this.integrationTime = integrationTime;
	}

	public int getReferenceBeamNum() {
		return referenceBeamNum;
	}

	public void setReferenceBeamNum(int referenceBeamNum) {
		this.referenceBeamNum = referenceBeamNum;
	}

	public int getCcdGainNumber() {
		return ccdGainNumber;
	}

	public void setCcdGainNumber(int ccdGainNumber) {
		this.ccdGainNumber = ccdGainNumber;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public CcdType getCcdType() {
		return ccdType;
	}

	public void setCcdType(CcdType ccdType) {
		this.ccdType = ccdType;
	}

	


	
}
