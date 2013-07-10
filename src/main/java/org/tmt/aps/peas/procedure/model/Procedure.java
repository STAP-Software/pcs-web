package org.tmt.aps.peas.procedure.model;

import java.util.Date;

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

import org.tmt.aps.peas.camera.model.Instrument;
import org.tmt.aps.peas.config.model.GlobalConfig;
import org.tmt.aps.peas.session.model.Session;
import org.tmt.aps.peas.telescope.model.Telescope;

@Entity
@Table(name = "Procedure")
@NamedQueries({
	@NamedQuery(name = "findAllProcedures", query = "SELECT p from Procedure p INNER JOIN FETCH p.telescope INNER JOIN FETCH p.instrument "
			+ "INNER JOIN FETCH p.procedureType INNER JOIN FETCH p.procedureConfig INNER JOIN FETCH p.globalConfig" ),
	@NamedQuery(name = "findLatestSessionProcedure", query = "SELECT p from Procedure p INNER JOIN FETCH p.telescope INNER JOIN FETCH p.instrument "
			+ "INNER JOIN FETCH p.procedureType INNER JOIN FETCH p.procedureConfig INNER JOIN FETCH p.globalConfig INNER JOIN FETCH p.session "
			+ "WHERE p.session.sessionId = :sessionId ORDER BY p.procedureNumber desc" )
	

})
public class Procedure {

	public static final int PROCEDURE_STATE_NEW = 1;
	public static final int PROCEDURE_STATE_EXECUTING = 2;
	public static final int PROCEDURE_STATE_COMPLETED = 3;
	public static final int PROCEDURE_STATE_ABORTED = 4;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long procedureId;
	
	private int procedureNumber;
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
	@JoinColumn(name = "procedureConfigId")
	ProcedureConfig procedureConfig;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "globalConfigId")
	GlobalConfig globalConfig;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "sessionId")
	Session session;


	public Procedure() {
		procedureConfig = new ProcedureConfig();
	}

	public Long getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}

	public int getProcedureNumber() {
		return procedureNumber;
	}

	public void setProcedureNumber(int procedureNumber) {
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

	public ProcedureConfig getProcedureConfig() {
		return procedureConfig;
	}

	public int getProcedureState() {
		return procedureState;
	}

	public void setProcedureState(int procedureState) {
		this.procedureState = procedureState;
	}

	public void setProcedureConfig(ProcedureConfig procedureConfig) {
		this.procedureConfig = procedureConfig;
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

	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public void setGlobalConfig(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
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
}
