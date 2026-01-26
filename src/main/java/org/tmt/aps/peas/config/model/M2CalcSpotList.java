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

import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Configuration entity class representing the M2CalcSpotList table
 * @author smichaels
 */
@Entity
@Table(name = "M2CalcSpotList")
@NamedQueries({
    @NamedQuery(
        name = "findM2CalcSpotList",
        query = "SELECT o FROM M2CalcSpotList o " +
                "INNER JOIN FETCH o.telescope " +  // no alias
                "WHERE o.telescope.telescopeId = :telescopeId"
    )
})

public class M2CalcSpotList {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long m2CalcSpotListId;
	private String m2CalcSpotListEncoded = "";  
	
	@ManyToOne
	@JoinColumn (name="telescopeId")
	private Telescope telescope;

	public Long getM2CalcSpotListId() {
		return m2CalcSpotListId;
	}

	public void setM2CalcSpotListId(Long m2CalcSpotListId) {
		this.m2CalcSpotListId = m2CalcSpotListId;
	}

	public String getM2CalcSpotListEncoded() {
		return m2CalcSpotListEncoded;
	}

	public void setM2CalcSpotListEncoded(String m2CalcSpotListEncoded) {
		this.m2CalcSpotListEncoded = m2CalcSpotListEncoded;
	}

	public Telescope getTelescope() {
		return telescope;
	}

	public void setTelescope(Telescope telescope) {
		this.telescope = telescope;
	}

	public boolean isNewRecord() {
		return m2CalcSpotListId == null;
	}
}
