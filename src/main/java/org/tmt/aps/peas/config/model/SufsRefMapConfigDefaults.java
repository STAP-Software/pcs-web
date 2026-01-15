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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.tmt.aps.peas.instrument.model.CcdType;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;

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
	@NamedQuery(name = "findByRefBeamNum", query = "SELECT o from SufsRefMapConfigDefaults o INNER JOIN FETCH o.instrument i INNER JOIN FETCH o.ccdType t "
			+ "where i.instrumentId = :instrumentId and t.ccdTypeId = :ccdTypeId and o.referenceBeamNum = :referenceBeamNum " )
})
public class SufsRefMapConfigDefaults {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
