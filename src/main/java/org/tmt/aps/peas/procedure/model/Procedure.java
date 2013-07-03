package org.tmt.aps.peas.procedure.model;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



public class Procedure {

	@Id
	private Long procedureId;
	
	private String procedureType;
	private int procedureNumber;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	protected ProcedureConfig procedureConfig;

	public Procedure(Long procedureId, String procedureType, int procedureNumber, Date createDate) {
		this.procedureId = procedureId;
		this.procedureNumber = procedureNumber;
		this.procedureType = procedureType;
		this.createDate = createDate;
		
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public ProcedureConfig getProcedureConfig() {
		return procedureConfig;
	}


	public void setProcedureConfig(ProcedureConfig procedureConfig) {
		this.procedureConfig = procedureConfig;
	}
	

}
