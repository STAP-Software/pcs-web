/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ProcedureConfigSet")
public class ProcedureConfigSet {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long procedureConfigSetId;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "fiConfigId")
	private FIConfig fiConfig;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "findCentConfigId")
	private FindCentConfig findCentConfig;
	
	

	public Long getProcedureConfigSetId() {
		return procedureConfigSetId;
	}

	public void setProcedureConfigSetId(Long procedureConfigSetId) {
		this.procedureConfigSetId = procedureConfigSetId;
	}

	public FIConfig getFiConfig() {
		return fiConfig;
	}

	public void setFiConfig(FIConfig fiConfig) {
		this.fiConfig = fiConfig;
	}

	public FindCentConfig getFindCentConfig() {
		return findCentConfig;
	}

	public void setFindCentConfig(FindCentConfig findCentConfig) {
		this.findCentConfig = findCentConfig;
	}


}
