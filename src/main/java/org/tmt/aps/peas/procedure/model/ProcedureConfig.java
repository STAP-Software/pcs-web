/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.telescope.model.Telescope;

@Entity
@Table(name = "ProcedureConfig")
@NamedQueries({
		@NamedQuery(name = "findAllProcedureConfigs", query = "SELECT p from ProcedureConfig p INNER JOIN FETCH p.telescope INNER JOIN FETCH p.instrument "
				+ "INNER JOIN FETCH p.procedureType"),
		@NamedQuery(name = "findDefaultProcedureConfig", query = "SELECT p from ProcedureConfig p INNER JOIN FETCH p.telescope tel INNER JOIN FETCH p.instrument inst "
				+ "INNER JOIN FETCH p.procedureType pt  "
				+ "WHERE tel.telescopeId = :telescopeId AND inst.instrumentId = :instrumentId AND pt.procedureTypeId = :procedureTypeId AND defaultFlg = TRUE "
				+ "ORDER BY p.updateDate desc ")

})
public class ProcedureConfig {

	public static final int FRAME_SOURCE_CCD = 1;
	public static final int FRAME_SOURCE_FILE = 2;
	
	public static final int LIGHT_SOURCE_STAR = 1;
	public static final int LIGHT_SOURCE_LED = 2;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long procedureConfigId;

	private boolean defaultFlg;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@Transient
	private PupilMask pupilMask;  // TODO: make this non-transient
	@Transient
	Filter filter;
	
	private int filterType;
	
	private Float integrationTime;
	@Column(name="numTrials")
	private int numberOfTrials = 1;
	private int frameSource;
	private int lightSource;

	private Integer ufsSegment;
	private Integer sufsGroup;
	
	private Integer calculationOptions; // nullable


	@Column(name="imageScaleRotationRemoval")
	private int frameScaleRotationRemoval;
	@Column(name="autoCenterTelescopeFlg")
	private int autoCenterTelescope;
	@Column(name="autoCenterPupilFlg")
	private int autoCenterPupil;
	private int autoCenterPupilMechanism;
	@Column(name="autoSendActuatorCmdsFlg")
	private int autoSendActuatorCmds;
	@Column(name="autoTakeRefBeamFlg")
	private int autoTakeRefBeam;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "telescopeId")
	Telescope telescope;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instrumentId")
	Instrument instrument;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureTypeId")
	ProcedureType procedureType;

	
	public int getFilterType() {
		return filterType;
	}

	public void setFilterType(int filterType) {
		this.filterType = filterType;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Float getIntegrationTime() {
		return integrationTime;
	}

	public void setIntegrationTime(Float integrationTime) {
		this.integrationTime = integrationTime;
	}

	public int getNumberOfTrials() {
		return numberOfTrials;
	}

	public void setNumberOfTrials(int numberOfTrials) {
		this.numberOfTrials = numberOfTrials;
	}

	public int getFrameSource() {
		return frameSource;
	}

	public void setFrameSource(int frameSource) {
		this.frameSource = frameSource;
	}

	public Long getProcedureConfigId() {
		return procedureConfigId;
	}

	public void setProcedureConfigId(Long procedureConfigId) {
		this.procedureConfigId = procedureConfigId;
	}

	public int getFrameScaleRotationRemoval() {
		return frameScaleRotationRemoval;
	}

	public void setFrameScaleRotationRemoval(int frameScaleRotationRemoval) {
		this.frameScaleRotationRemoval = frameScaleRotationRemoval;
	}

	public int getAutoCenterTelescope() {
		return autoCenterTelescope;
	}

	public void setAutoCenterTelescope(int autoCenterTelescope) {
		this.autoCenterTelescope = autoCenterTelescope;
	}

	public int getAutoCenterPupil() {
		return autoCenterPupil;
	}

	public void setAutoCenterPupil(int autoCenterPupil) {
		this.autoCenterPupil = autoCenterPupil;
	}

	public int getAutoCenterPupilMechanism() {
		return autoCenterPupilMechanism;
	}

	public void setAutoCenterPupilMechanism(int autoCenterPupilMechanism) {
		this.autoCenterPupilMechanism = autoCenterPupilMechanism;
	}

	public int getAutoSendActuatorCmds() {
		return autoSendActuatorCmds;
	}

	public void setAutoSendActuatorCmds(int autoSendActuatorCmds) {
		this.autoSendActuatorCmds = autoSendActuatorCmds;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getCalculationOptions() {
		return calculationOptions;
	}

	public void setCalculationOptions(Integer calculationOptions) {
		this.calculationOptions = calculationOptions;
	}

	public boolean isDefaultFlg() {
		return defaultFlg;
	}

	public void setDefaultFlg(boolean defaultFlg) {
		this.defaultFlg = defaultFlg;
	}

	public int getAutoTakeRefBeam() {
		return autoTakeRefBeam;
	}

	public void setAutoTakeRefBeam(int autoTakeRefBeam) {
		this.autoTakeRefBeam = autoTakeRefBeam;
	}

	public Telescope getTelescope() {
		return telescope;
	}

	public void setTelescope(Telescope telescope) {
		this.telescope = telescope;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public ProcedureType getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(ProcedureType procedureType) {
		this.procedureType = procedureType;
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

	public int getLightSource() {
		return lightSource;
	}

	public void setLightSource(int lightSource) {
		this.lightSource = lightSource;
	}

	public PupilMask getPupilMask() {
		return pupilMask;
	}

	public void setPupilMask(PupilMask pupilMask) {
		this.pupilMask = pupilMask;
	}

	public boolean isFrameFromFile() {
		return frameSource == FRAME_SOURCE_FILE;
	}



}
