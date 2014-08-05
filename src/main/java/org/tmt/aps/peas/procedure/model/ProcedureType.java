/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

@Entity
@Table(name = "ProcedureType")
@NamedQueries({
	@NamedQuery(name = "findAllProcedureTypes", query = "SELECT p from ProcedureType p INNER JOIN FETCH p.defaultPupilMaskType " ),
	@NamedQuery(name = "findProcedureType", query = "SELECT p from ProcedureType p INNER JOIN FETCH p.defaultPupilMaskType "
			+ "WHERE p.procedureTypeId = :procedureTypeId" )
})
public class ProcedureType {

	public static final Long PROCEDURE_TYPE_ID_PASSIVE_TILT = new Long(1);
	public static final Long PROCEDURE_TYPE_ID_FINE_SCREEN = new Long(2);
	public static final Long PROCEDURE_TYPE_ID_PHASING = new Long(3);
	public static final Long PROCEDURE_TYPE_ID_SUFS = new Long(5);
	public static final Long PROCEDURE_TYPE_ID_PUPIL_REGISTRATION = new Long(6);
	public static final Long PROCEDURE_TYPE_ID_CENTER_TELESCOPE = new Long(7);
	public static final Long PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP = new Long(8);
	public static final Long PROCEDURE_TYPE_ID_CREATE_FIRST_REFERENCE_BEAM_MAP = new Long(9);
	
	@Id
	private Long procedureTypeId;
	
	@Column(nullable=false, length=100)
	private String procedureTypeName;
	
	@Column(nullable=false, length=10)
	private String procedureTypeCd;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "defaultMaskTypeId", referencedColumnName = "pupilMaskTypeId")
	PupilMaskType defaultPupilMaskType;

	@Column(nullable=false, length=255)
	String defaultIntTimes;
	
	@Transient
	List<Float> integrationTimeList;
	
	public Long getProcedureTypeId() {
		return procedureTypeId;
	}
	public void setProcedureTypeId(Long procedureTypeId) {
		this.procedureTypeId = procedureTypeId;
	}
	public String getProcedureTypeName() {
		return procedureTypeName;
	}
	public void setProcedureTypeName(String procedureTypeName) {
		this.procedureTypeName = procedureTypeName;
	}
	public String getProcedureTypeCd() {
		return procedureTypeCd;
	}
	public void setProcedureTypeCd(String procedureTypeCd) {
		this.procedureTypeCd = procedureTypeCd;
	}
	
	public PupilMaskType getDefaultPupilMaskType() {
		return defaultPupilMaskType;
	}
	
	public void setDefaultPupilMaskType(PupilMaskType defaultPupilMaskType) {
		this.defaultPupilMaskType = defaultPupilMaskType;
	}
	public boolean isCreateRefMap() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_CREATE_REFERENCE_BEAM_MAP);
	}
	public boolean isPassiveTilt() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_PASSIVE_TILT);
	}
	public boolean isCenterTelescope() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_CENTER_TELESCOPE);
	}
	public boolean isFineScreen() {
		return procedureTypeId.equals(PROCEDURE_TYPE_ID_FINE_SCREEN);
	}
	public String getDefaultIntTimes() {
		return defaultIntTimes;
	}
	public void setDefaultIntTimes(String defaultIntTimes) {
		this.defaultIntTimes = defaultIntTimes;
	}
	
	public List<Float> getIntegrationTimeList() {
		if (integrationTimeList == null && defaultIntTimes != null) {
			integrationTimeList =  FloatListEncoder.decodeList(defaultIntTimes);
			Collections.sort(integrationTimeList);
		} 
		return integrationTimeList;
	}
	public void setIntegrationTimeList(List<Float> integrationTimeList) {
		this.integrationTimeList = integrationTimeList;
	}
	public String toString() {
		return procedureTypeName;
	}
	
	
	
	
}
