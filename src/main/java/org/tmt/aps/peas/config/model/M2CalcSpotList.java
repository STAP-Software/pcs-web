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

import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Configuration entity class representing the M2CalcSpotList table
 * @author smichaels
 */
@Entity
@Table(name = "M2CalcSpotList")
@NamedQueries({
	@NamedQuery(name = "findM2CalcSpotList", query = "SELECT o from M2CalcSpotList o INNER JOIN FETCH o.telescope t "
			+ "where t.telescopeId = :telescopeId" )
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
