/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;

@Entity
@Table(name = "RefMapConfigDefaults")
@NamedQueries({
	@NamedQuery(name = "findByMaskTypeAndFilterType", query = "SELECT o from RefMapConfigDefaults o INNER JOIN FETCH o.pupilMaskType p "
			+ "INNER JOIN FETCH o.filterType ft INNER JOIN FETCH o.instrument i "
			+ "where p.pupilMaskTypeId = :pupilMaskTypeId and ft.filterTypeId = :filterTypeId and i.instrumentId = :instrumentId " )
})
public class RefMapConfigDefaults {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long refMapConfigDefaultId;

	float integrationTime; 
	int referenceBeamNum;
	
	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	@ManyToOne
	@JoinColumn (name="filterTypeId")
	private FilterType filterType;

	@ManyToOne
	@JoinColumn (name="instrumentId")
	private Instrument instrument;

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
	
	
}
