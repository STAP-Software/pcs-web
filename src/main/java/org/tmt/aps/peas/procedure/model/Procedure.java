/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanComparator;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.ProcedureConfigSet;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.model.Telescope;

@Entity
@Table(name = "Procedure")
@NamedQueries({
	@NamedQuery(name = "findAllProcedures", query = "SELECT p from Procedure p INNER JOIN FETCH p.telescope INNER JOIN FETCH p.instrument "
			+ "INNER JOIN FETCH p.procedureType INNER JOIN FETCH p.procedureConfigSet pcs INNER JOIN FETCH pcs.procedureConfig INNER JOIN FETCH pcs.globalConfig" ),
	@NamedQuery(name = "findLatestSessionProcedure", query = "SELECT p from Procedure p INNER JOIN FETCH p.session "
			+ "WHERE p.session.sessionId = :sessionId ORDER BY p.executionStartTime desc" ),
	@NamedQuery(name = "findProcedure", query = "SELECT DISTINCT p from Procedure p INNER JOIN FETCH p.telescope INNER JOIN FETCH p.instrument "
			+ "INNER JOIN FETCH p.procedureType INNER JOIN FETCH p.procedureConfigSet pcs INNER JOIN FETCH pcs.procedureConfig pc INNER JOIN FETCH pcs.globalConfig "
			+ "LEFT OUTER JOIN FETCH p.procedureCcdFrameList pcf LEFT OUTER JOIN FETCH pcf.ccdFrame cf LEFT OUTER JOIN FETCH pcf.centroidMap "
			+ "LEFT OUTER JOIN FETCH cf.cameraState INNER JOIN FETCH p.procedureConfigSet pcs LEFT OUTER JOIN FETCH pcs.fiConfig "
			+ "LEFT OUTER JOIN FETCH pcs.pupilRegErrorConfig LEFT OUTER JOIN FETCH pcs.calcM2M1Config "
			+ "LEFT OUTER JOIN FETCH pcs.findCentConfigInterior LEFT OUTER JOIN FETCH pcs.findCentConfigPeripheral LEFT OUTER JOIN FETCH pcs.centroidOffsetsConfig LEFT OUTER JOIN FETCH pc.pupilMask "
			+ "LEFT OUTER JOIN FETCH pc.filter LEFT OUTER JOIN FETCH pc.referenceBeam "
			+ "WHERE p.procedureId = :procedureId" )
	
})
public class Procedure {

	public static final int PROCEDURE_STATE_NEW = 1;
	public static final int PROCEDURE_STATE_EXECUTING = 2;
	public static final int PROCEDURE_STATE_COMPLETED = 3;
	public static final int PROCEDURE_STATE_ABORTED = 4;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long procedureId;
	
	@Column(length=50)
	String testNumber;
		
	@Column(length=50)
	String starName;
	
	@Column(length=50)
	String starSpType;
	
	@Column(length=50)
	String starVmag;
	
	@Column(length=2048)
	String comments;
	
	private String procedureNumber;
	private int procedureState;

	@Temporal(TemporalType.TIMESTAMP)
	private Date executionStartTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date executionEndTime;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "telescopeId")
	Telescope telescope;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "instrumentId")
	Instrument instrument;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureTypeId")
	ProcedureType procedureType;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "sessionId")
	Session session;

	@OneToMany (mappedBy="procedure")
	List<ProcedureCcdFrame> procedureCcdFrameList;

	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn (name="procedureConfigSetId")
	private ProcedureConfigSet procedureConfigSet;


	@Transient
	private ProcedureOutput procedureOutput;
	@Transient 
	private RefBeamMap refBeamMap;  // the refBeamMap taken and/or used in this procedure
	
	
	public Procedure() {
		procedureConfigSet = new ProcedureConfigSet();
	}

	public Long getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
		
	}

	public String getProcedureNumber() {
		return procedureNumber;
	}

	public void setProcedureNumber(String procedureNumber) {
		this.procedureNumber = procedureNumber;
	}


	public Date getExecutionStartTime() {
		return executionStartTime;
	}

	public void setExecutionStartTime(Date executionStartTime) {
		this.executionStartTime = executionStartTime;
	}

	public Date getExecutionEndTime() {
		return executionEndTime;
	}

	public void setExecutionEndTime(Date executionEndTime) {
		this.executionEndTime = executionEndTime;
	}

	public int getProcedureState() {
		return procedureState;
	}

	public void setProcedureState(int procedureState) {
		this.procedureState = procedureState;
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

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getTestNumber() {
		return testNumber;
	}

	public void setTestNumber(String testNumber) {
		this.testNumber = testNumber;
	}

	public String getStarName() {
		return starName;
	}

	public void setStarName(String starName) {
		this.starName = starName;
	}

	public String getStarSpType() {
		return starSpType;
	}

	public void setStarSpType(String starSpType) {
		this.starSpType = starSpType;
	}

	public String getStarVmag() {
		return starVmag;
	}

	public void setStarVmag(String starVmag) {
		this.starVmag = starVmag;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<ProcedureCcdFrame> getProcedureCcdFrameList() {
		if (procedureCcdFrameList != null) {
			Collections.sort(procedureCcdFrameList, new BeanComparator("procedureFrameNumber"));
		}
		return procedureCcdFrameList;
	}

	public void setProcedureCcdFrameList(List<ProcedureCcdFrame> procedureCcdFrameList) {
		this.procedureCcdFrameList = procedureCcdFrameList;
	}
	
	public int getProcedureCcdFrameCount() {
		return procedureCcdFrameList == null ? 0 : procedureCcdFrameList.size();
	}


	public ProcedureConfigSet getProcedureConfigSet() {
		return procedureConfigSet;
	}

	public void setProcedureConfigSet(ProcedureConfigSet procedureConfigSet) {
		this.procedureConfigSet = procedureConfigSet;
	}

	public ProcedureOutput getProcedureOutput() {
		return procedureOutput;
	}

	public void setProcedureOutput(ProcedureOutput procedureOutput) {
		this.procedureOutput = procedureOutput;
	}

	public String getProcedureStateDisplayString() {

		switch (procedureState) {
		case PROCEDURE_STATE_NEW:
			return "New";
		case PROCEDURE_STATE_EXECUTING:
			return "Executing";
		case PROCEDURE_STATE_COMPLETED:
			return "Completed";
		case PROCEDURE_STATE_ABORTED:
			return "Aborted";
		default:
			return "Unknown";
		}
	}
	
	public boolean isArchivedProcedure() {
		return procedureState == PROCEDURE_STATE_COMPLETED || procedureState == PROCEDURE_STATE_ABORTED;
	}

	public void addProcedureCcdFrame(ProcedureCcdFrame procedureCcdFrame) {
		if (procedureCcdFrameList == null) {
			procedureCcdFrameList = new ArrayList<ProcedureCcdFrame>();
		}
		procedureCcdFrameList.add(procedureCcdFrame);
		
	}
	
	public ProcedureCcdFrame getLatestProcedureCcdFrame() {
		if (procedureCcdFrameList == null) {
			return null;
		}
		return procedureCcdFrameList.get(procedureCcdFrameList.size()-1);
	}
	
	public boolean isNewRecord() {
		return procedureId == null;
	}

	public RefBeamMap getRefBeamMap() {
		return refBeamMap;
	}

	public void setRefBeamMap(RefBeamMap refBeamMap) {
		this.refBeamMap = refBeamMap;
	}

	@Transient
	private int percentComplete;
	public int getPercentComplete() {
		return percentComplete;
	}
	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}
	


}
