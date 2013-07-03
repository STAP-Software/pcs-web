package org.tmt.aps.peas.procedure.model;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class Procedure {

	public static final int PROCEDURE_STATE_NEW = 1;
	public static final int PROCEDURE_STATE_EXECUTING = 2;
	public static final int PROCEDURE_STATE_COMPLETED = 3;
	public static final int PROCEDURE_STATE_ABORTED = 4;

	@Id
	private Long procedureId;

	private String procedureType;
	private int procedureNumber;

	@Temporal(TemporalType.TIMESTAMP)
	private Date executionStartTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date executionEndTime;

	protected ProcedureConfig procedureConfig;

	private int procedureState;

	public Procedure(Long procedureId, String procedureType, int procedureNumber, Date executionStartTime) {
		this.procedureId = procedureId;
		this.procedureNumber = procedureNumber;
		this.procedureType = procedureType;
		this.executionEndTime = executionStartTime;

		procedureState = PROCEDURE_STATE_NEW;

		procedureConfig = new ProcedureConfig();
	}

	public Procedure() {
		procedureConfig = new ProcedureConfig();
	}

	public Long getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}

	public String getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(String procedureType) {
		this.procedureType = procedureType;
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
