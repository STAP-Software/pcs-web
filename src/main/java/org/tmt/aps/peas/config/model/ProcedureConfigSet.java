/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Configuration entity class representing the ProcedureConfigSet table.  This is a join table for many of the configuration tables, 
 * and this class contains references to each of the entities.
 * @author smichaels
 */
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
	@JoinColumn(name = "findCentConfigInteriorId")
	private FindCentConfig findCentConfigInterior;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "findCentConfigPeripheralId")
	private FindCentConfig findCentConfigPeripheral;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "pupilRegErrorConfigId")
	private PupilRegErrorConfig pupilRegErrorConfig;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "calcM2M1ConfigId")
	private CalcM2M1Config calcM2M1Config;
	
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
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "sufsCoarseOffsetsConfigId")
	private SufsCoarseOffsetsConfig sufsCoarseOffsetsConfig;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "iterationListConfigId")
	private IterationListConfig iterationListConfig;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "nbFilterSeqConfigId")
	private NbFilterSeqConfig nbFilterSeqConfig;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "frameCorrectionConfigId")
	private FrameCorrectionConfig frameCorrectionConfig;


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

	public FindCentConfig getFindCentConfigInterior() {
		return findCentConfigInterior;
	}

	public void setFindCentConfigInterior(FindCentConfig findCentConfigInterior) {
		this.findCentConfigInterior = findCentConfigInterior;
	}

	public FindCentConfig getFindCentConfigPeripheral() {
		return findCentConfigPeripheral;
	}

	public void setFindCentConfigPeripheral(FindCentConfig findCentConfigPeripheral) {
		this.findCentConfigPeripheral = findCentConfigPeripheral;
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

	public CalcM2M1Config getCalcM2M1Config() {
		return calcM2M1Config;
	}

	public void setCalcM2M1Config(CalcM2M1Config calcM2M1Config) {
		this.calcM2M1Config = calcM2M1Config;
	}

	public SufsCoarseOffsetsConfig getSufsCoarseOffsetsConfig() {
		return sufsCoarseOffsetsConfig;
	}

	public void setSufsCoarseOffsetsConfig(SufsCoarseOffsetsConfig sufsCoarseOffsetsConfig) {
		this.sufsCoarseOffsetsConfig = sufsCoarseOffsetsConfig;
	}

	public IterationListConfig getIterationListConfig() {
		return iterationListConfig;
	}

	public void setIterationListConfig(IterationListConfig iterationListConfig) {
		this.iterationListConfig = iterationListConfig;
	}

	public NbFilterSeqConfig getNbFilterSeqConfig() {
		return nbFilterSeqConfig;
	}

	public void setNbFilterSeqConfig(NbFilterSeqConfig nbFilterSeqConfig) {
		this.nbFilterSeqConfig = nbFilterSeqConfig;
	}

	public FrameCorrectionConfig getFrameCorrectionConfig() {
		return frameCorrectionConfig;
	}

	public void setFrameCorrectionConfig(FrameCorrectionConfig frameCorrectionConfig) {
		this.frameCorrectionConfig = frameCorrectionConfig;
	}



}
