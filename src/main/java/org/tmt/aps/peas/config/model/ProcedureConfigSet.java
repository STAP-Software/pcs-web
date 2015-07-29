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
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "pupilRegErrorConfigId")
	private PupilRegErrorConfig pupilRegErrorConfig;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "centroidOffsetsConfigId")
	private CentroidOffsetsConfig centroidOffsetsConfig;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "autoRefMapConfigId")
	private AutoRefMapConfig autoRefMapConfig;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "autoCenterTelConfigId")
	private AutoCenterTelConfig autoCenterTelConfig;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "procedureConfigId")
	private ProcedureConfig procedureConfig;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "globalConfigId")
	private GlobalConfig globalConfig;
	
	

	public ProcedureConfig getProcedureConfig() {
		return procedureConfig;
	}

	public void setProcedureConfig(ProcedureConfig procedureConfig) {
		this.procedureConfig = procedureConfig;
	}

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

	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public void setGlobalConfig(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}

	public CentroidOffsetsConfig getCentroidOffsetsConfig() {
		return centroidOffsetsConfig;
	}

	public void setCentroidOffsetsConfig(CentroidOffsetsConfig centroidOffsetsConfig) {
		this.centroidOffsetsConfig = centroidOffsetsConfig;
	}

	public AutoRefMapConfig getAutoRefMapConfig() {
		return autoRefMapConfig;
	}

	public void setAutoRefMapConfig(AutoRefMapConfig autoRefMapConfig) {
		this.autoRefMapConfig = autoRefMapConfig;
	}

	public AutoCenterTelConfig getAutoCenterTelConfig() {
		return autoCenterTelConfig;
	}

	public void setAutoCenterTelConfig(AutoCenterTelConfig autoCenterTelConfig) {
		this.autoCenterTelConfig = autoCenterTelConfig;
	}

	public PupilRegErrorConfig getPupilRegErrorConfig() {
		return pupilRegErrorConfig;
	}

	public void setPupilRegErrorConfig(PupilRegErrorConfig pupilRegErrorConfig) {
		this.pupilRegErrorConfig = pupilRegErrorConfig;
	}



}
