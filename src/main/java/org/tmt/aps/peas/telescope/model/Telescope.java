/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.telescope.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Telescope")
@NamedQueries({
	@NamedQuery(name = "findAllTelescopes", query = "SELECT o from Telescope o" ),
	@NamedQuery(name = "findTelescope", query = "SELECT o from Telescope o where o.telescopeId = :telescopeId" )
})
public class Telescope {

	@Id
	private Long telescopeId;
	
	@Column(nullable=false, length=100)
	private String telescopeName;
	
	
	public Long getTelescopeId() {
		return telescopeId;
	}
	public void setTelescopeId(Long telescopeId) {
		this.telescopeId = telescopeId;
	}
	public String getTelescopeName() {
		return telescopeName;
	}
	public void setTelescopeName(String telescopeName) {
		this.telescopeName = telescopeName;
	}
	
	
	
	
}
