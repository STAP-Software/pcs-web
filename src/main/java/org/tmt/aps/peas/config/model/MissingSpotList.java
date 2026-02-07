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

import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Configuration entity class representing the MissingSpotList table
 * @author smichaels
 */
@Entity
@Table(name = "MissingSpotList")
@NamedQueries({
    @NamedQuery(
        name = "findSpotListByTypeAndMask",
        query = "SELECT o FROM MissingSpotList o " +
                "INNER JOIN FETCH o.pupilMaskType " +  // no alias
                "INNER JOIN FETCH o.telescope " +      // no alias
                "WHERE o.spotListType = :spotListType " +
                "AND o.pupilMaskType.pupilMaskTypeId = :pupilMaskTypeId " +
                "AND o.telescope.telescopeId = :telescopeId"
    ),
    @NamedQuery(
        name = "findSpotListByTypeMaskGroup",
        query = "SELECT o FROM MissingSpotList o " +
                "INNER JOIN FETCH o.pupilMaskType " +
                "INNER JOIN FETCH o.telescope " +
                "WHERE o.spotListType = :spotListType " +
                "AND o.pupilMaskType.pupilMaskTypeId = :pupilMaskTypeId " +
                "AND o.telescope.telescopeId = :telescopeId " +
                "AND o.sufsGroup = :sufsGroup"
    )
})

public class MissingSpotList {
	
	@Id
	@SequenceGenerator(
	    name = "missingSpotList_gen",
	    sequenceName = "hibernate_sequence",
	    allocationSize = 1
	)
	@GeneratedValue(
	    strategy = GenerationType.SEQUENCE,
	    generator = "missingSpotList_gen"
	)
	private Long missingSpotListId;
	
	private int spotListType;
	private Integer ufsSegment;	
	private Integer sufsGroup;	
	private String missingSpotListEncoded = "";  
	
	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	@ManyToOne
	@JoinColumn (name="telescopeId")
	private Telescope telescope;

	
	

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
 
	public Telescope getTelescope() {
		return telescope;
	}

	public void setTelescope(Telescope telescope) {
		this.telescope = telescope;
	}

	public boolean isNewRecord() {
		return missingSpotListId == null;
	}
}
