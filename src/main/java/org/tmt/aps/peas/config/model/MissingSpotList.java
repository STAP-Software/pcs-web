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

import org.tmt.aps.peas.instrument.model.PupilMaskType;

@Entity
@Table(name = "MissingSpotList")
@NamedQueries({
	@NamedQuery(name = "findSpotListByTypeAndMask", query = "SELECT o from MissingSpotList o INNER JOIN FETCH o.pupilMaskType p "
			+ "where o.spotListType = :spotListType and p.pupilMaskTypeId = :pupilMaskTypeId" ),
	@NamedQuery(name = "findSpotListByTypeMaskGroup", query = "SELECT o from MissingSpotList o INNER JOIN FETCH o.pupilMaskType p "
			+ "where o.spotListType = :spotListType and p.pupilMaskTypeId = :pupilMaskTypeId and o.sufsGroup = :sufsGroup" )
})
public class MissingSpotList {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long missingSpotListId;
	private int spotListType;
	private Integer ufsSegment;	
	private Integer sufsGroup;	
	private String missingSpotListEncoded = "";  
	
	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	
	

	public Long getMissingSpotListId() {
		return missingSpotListId;
	}

	public void setMissingSpotListId(Long missingSpotListId) {
		this.missingSpotListId = missingSpotListId;
	}

	public String getMissingSpotListEncoded() {
		return missingSpotListEncoded;
	}

	public void setMissingSpotListEncoded(String missingSpotListEncoded) {
		this.missingSpotListEncoded = missingSpotListEncoded;
	}

	public int getSpotListType() {
		return spotListType;
	}

	public void setSpotListType(int spotListType) {
		this.spotListType = spotListType;
	}

	public Integer getUfsSegment() {
		return ufsSegment;
	}

	public void setUfsSegment(Integer ufsSegment) {
		this.ufsSegment = ufsSegment;
	}

	public Integer getSufsGroup() {
		return sufsGroup;
	}

	public void setSufsGroup(Integer sufsGroup) {
		this.sufsGroup = sufsGroup;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}
 

	public boolean isNewRecord() {
		return missingSpotListId == null;
	}
}
