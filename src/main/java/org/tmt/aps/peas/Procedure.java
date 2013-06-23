package org.tmt.aps.peas;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "PfPartner")
public class Procedure implements Serializable {
	
	@Id
	private Long procedureId;
	
	private String procedureType;
	private int procedureNumber;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	
	public Procedure() {
		
	}
	
	public Procedure(Long procedureId, String procedureType, int procedureNumber, Date createDate) {
		this.procedureId = procedureId;
		this.procedureType = procedureType;
		this.procedureNumber = procedureNumber;
		this.createDate = createDate;
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


	

}
