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
@Table(name = "RefMapConfigDefaults")
@NamedQueries({
    @NamedQuery(
        name = "findByMaskTypeAndFilterType",
        query = "SELECT o FROM RefMapConfigDefaults o " +
                "INNER JOIN FETCH o.pupilMaskType " +   // no alias
                "INNER JOIN FETCH o.filterType " +      // no alias
                "INNER JOIN FETCH o.instrument " +      // no alias
                "INNER JOIN FETCH o.ccdType " +         // no alias
                "WHERE o.pupilMaskType.pupilMaskTypeId = :pupilMaskTypeId " +
                "AND o.filterType.filterTypeId = :filterTypeId " +
                "AND o.instrument.instrumentId = :instrumentId " +
                "AND o.ccdType.ccdTypeId = :ccdTypeId"
    )
})

public class RefMapConfigDefaults {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long refMapConfigDefaultId;

	float integrationTime; 
	int referenceBeamNum;
	int ccdGainNumber;
	
	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	@ManyToOne
	@JoinColumn (name="filterTypeId")
	private FilterType filterType;

	@ManyToOne
	@JoinColumn (name="instrumentId")
	private Instrument instrument;

	@ManyToOne
	@JoinColumn (name="ccdTypeId")
	private CcdType ccdType;

	@Transient
	private ReferenceBeam referenceBeam;
	

	public Long getRefMapConfigDefaultId() {
		return refMapConfigDefaultId;
	}

	public void setRefMapConfigDefaultId(Long refMapConfigDefaultId) {
		this.refMapConfigDefaultId = refMapConfigDefaultId;
	}

	public float getIntegrationTime() {
		return integrationTime;
	}

	public void setIntegrationTime(float integrationTime) {
		this.integrationTime = integrationTime;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterType filterType) {
		this.filterType = filterType;
	}

	public int getReferenceBeamNum() {
		return referenceBeamNum;
	}

	public void setReferenceBeamNum(int referenceBeamNum) {
		this.referenceBeamNum = referenceBeamNum;
	}

	public ReferenceBeam getReferenceBeam() {
		return referenceBeam;
	}

	public void setReferenceBeam(ReferenceBeam referenceBeam) {
		this.referenceBeam = referenceBeam;
	}

	public int getCcdGainNumber() {
		return ccdGainNumber;
	}

	public void setCcdGainNumber(int ccdGainNumber) {
		this.ccdGainNumber = ccdGainNumber;
	}
	
	
}
