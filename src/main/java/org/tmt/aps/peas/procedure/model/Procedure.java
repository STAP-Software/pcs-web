/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.model;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;

import org.apache.commons.beanutils.BeanComparator;
import org.tmt.aps.peas.config.model.ProcedureConfigSet;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Database Entity class representing a single row in the Procedure table
 * Includes Transient fields for {@link RefBeamMap} and {@link ProcedureOutput} and percentComplete
 * Includes utility and convenience methods
 * @author smichaels
 *
 */

@Entity
@Table(name = "Procedure")
@NamedQueries({
	@NamedQuery(
		    name = "findAllProcedures",
		    query = "SELECT p FROM Procedure p " +
		            "INNER JOIN FETCH p.telescope " +
		            "INNER JOIN FETCH p.instrument " +
		            "INNER JOIN FETCH p.procedureType " +
		            "INNER JOIN FETCH p.procedureConfigSet " +
		            "INNER JOIN FETCH p.procedureConfigSet.procedureConfig " +
		            "INNER JOIN FETCH p.procedureConfigSet.globalConfig"
	),
	@NamedQuery(
		    name = "findLatestSessionProcedure",
		    query = "SELECT p FROM Procedure p " +
		            "INNER JOIN FETCH p.session " +
		            "WHERE p.session.sessionId = :sessionId " +
		            "ORDER BY p.executionStartTime DESC"
	),
	@NamedQuery(
		    name = "findProcedure",
		    query = "SELECT DISTINCT p FROM Procedure p " +
		            "INNER JOIN FETCH p.telescope " +
		            "INNER JOIN FETCH p.instrument " +
		            "INNER JOIN FETCH p.procedureType " +
		            "INNER JOIN FETCH p.procedureConfigSet " +
		            "INNER JOIN FETCH p.procedureConfigSet.procedureConfig " +
		            "INNER JOIN FETCH p.procedureConfigSet.globalConfig " +
		            "LEFT OUTER JOIN FETCH p.procedureCcdFrameList " +
		            "LEFT OUTER JOIN FETCH p.procedureCcdFrameList.ccdFrame " +
		            "LEFT OUTER JOIN FETCH p.procedureCcdFrameList.centroidMap " +
		            "LEFT OUTER JOIN FETCH p.procedureCcdFrameList.ccdFrame.cameraState " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.fiConfig " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.pupilRegErrorConfig " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.calcM2M1Config " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.findCentConfigInterior " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.findCentConfigPeripheral " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.centroidOffsetsConfig " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.procedureConfig.pupilMask " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.procedureConfig.filter " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.procedureConfig.referenceBeam " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.sufsCoarseOffsetsConfig " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.iterationListConfig " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.nbFilterSeqConfig " +
		            "LEFT OUTER JOIN FETCH p.procedureConfigSet.frameCorrectionConfig " +
		            "WHERE p.procedureId = :procedureId"
	)

	
})
public class Procedure {

	public static final int PROCEDURE_STATE_NEW = 1;
	public static final int PROCEDURE_STATE_EXECUTING = 2;
	public static final int PROCEDURE_STATE_COMPLETED = 3;
	public static final int PROCEDURE_STATE_ABORTED = 4;

	@Id
	@SequenceGenerator(
		    name = "procedure_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "procedure_gen"
		)
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
	private boolean operational;

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

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn (name="procedureConfigSetId")
	private ProcedureConfigSet procedureConfigSet;


	@Transient
	private ProcedureOutput procedureOutput;
	
	
	
	public Procedure() {
		procedureConfigSet = new ProcedureConfigSet();
		operational = true;
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
	
	public String getStarNameFormatted() {
		return starName != null && starName.trim().length() > 0 ? starName : "N/A";
	}

	public String getStarSpType() {
		return starSpType;
	}

	public void setStarSpType(String starSpType) {
		this.starSpType = starSpType;
	}
	
	public String getStarSpTypeFormatted() {
		return starSpType != null && starSpType.trim().length() > 0 ? starSpType : "N/A";
	}


	public String getStarVmag() {
		return starVmag;
	}

	public void setStarVmag(String starVmag) {
		this.starVmag = starVmag;
	}

	public String getStarVmagFormatted() {
		return starVmag != null && starVmag.trim().length() > 0 ? starVmag : "N/A";
	}
	
	public boolean isOperational() {
		return operational;
	}

	public void setOperational(boolean operational) {
		this.operational = operational;
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

	/**
	 * @return a display string of the procedure state
	 */
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
	
	/**
	 * @return true if this procedure state is completed or aborted
	 */
	public boolean isArchivedProcedure() {
		return procedureState == PROCEDURE_STATE_COMPLETED || procedureState == PROCEDURE_STATE_ABORTED;
	}
	
	
	/**
	 * @return the most recent ProcedureCcdFrame in the list or null if the list does not exist
	 */
	public ProcedureCcdFrame getLatestProcedureCcdFrame() {
		if (procedureCcdFrameList == null) {
			return null;
		}
		return procedureCcdFrameList.get(procedureCcdFrameList.size()-1);
	}	
	
	
	/**
	 * @return true if this is a new procedure record that has not yet been stored in the database
	 */
	public boolean isNewRecord() {
		return procedureId == null;
	}

	

	@Transient
	private int percentComplete;
	public int getPercentComplete() {
		return percentComplete;
	}
	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}
	
	@Transient
	private int iteration;
	public int getIteration() {
		return iteration;
	}
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	/**
	 * @return the associated procedure output if it is an instance of {@link PassiveTiltProcedureOutput}.
	 * Used by session detail row expansion display
	 */
	public PassiveTiltProcedureOutput getPassiveTiltProcedureOutput() {
		if (procedureOutput instanceof PassiveTiltProcedureOutput) {
			return (PassiveTiltProcedureOutput)procedureOutput;
		} else {
			return new PassiveTiltProcedureOutput();
		}
	}

	/**
	 * @return the associated procedure output if it is an instance of {@link FineScreenProcedureOutput}.
	 * Used by session detail row expansion display
	 */
	public FineScreenProcedureOutput getFineScreenProcedureOutput() {
		if (procedureOutput instanceof FineScreenProcedureOutput) {
			return (FineScreenProcedureOutput)procedureOutput;
		} else {
			return new FineScreenProcedureOutput();
		}
	}
	
	/**
	 * @return the associated procedure output if it is an instance of {@link CoarsePhasingProcedureOutput}.
	 * Used by session detail row expansion display
	 */
	public CoarsePhasingProcedureOutput getCoarsePhasingProcedureOutput() {
		if (procedureOutput instanceof CoarsePhasingProcedureOutput) {
			return (CoarsePhasingProcedureOutput)procedureOutput;
		} else {
			return new CoarsePhasingProcedureOutput();
		}
	}
	
	/**
	 * @return the associated procedure output if it is an instance of {@link NarrowBandPhasingProcedureOutput}.
	 * Used by session detail row expansion display
	 */
	public NarrowBandPhasingProcedureOutput getNarrowBandPhasingProcedureOutput() {
		if (procedureOutput instanceof NarrowBandPhasingProcedureOutput) {
			return (NarrowBandPhasingProcedureOutput)procedureOutput;
		} else {
			return new NarrowBandPhasingProcedureOutput();
		}
	}
	
	/**
	 * @return the associated procedure output if it is an instance of {@link SufsProcedureOutput}.
	 * Used by session detail row expansion display
	 */
	public SufsProcedureOutput getSufsProcedureOutput() {
		if (procedureOutput instanceof SufsProcedureOutput) {
			return (SufsProcedureOutput)procedureOutput;
		} else {
			return new SufsProcedureOutput();
		}
	}

	public boolean isProcedureStateAborted() {
		return procedureState == Procedure.PROCEDURE_STATE_ABORTED;
		
	}
}
