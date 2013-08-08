package org.tmt.aps.peas.procedure.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

@Entity
@Table(name = "ProcedureContext")
@NamedQueries({


})
public class ProcedureContext {

	@Id
	private Long procedureId;

	@Column(length=50)
	String testNumber;
	
	@Column(length=50)
	String acsSnapNumberAfter;
	
	@Column(length=50)
	String starName;
	
	@Column(length=50)
	String starSpType;
	
	@Column(length=50)
	String starVmag;
	
	@Column(length=2048)
	String comments;

	


	public Long getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}

	public String getTestNumber() {
		return testNumber;
	}

	public void setTestNumber(String testNumber) {
		this.testNumber = testNumber;
	}

	public String getAcsSnapNumberAfter() {
		return acsSnapNumberAfter;
	}

	public void setAcsSnapNumberAfter(String acsSnapNumberAfter) {
		this.acsSnapNumberAfter = acsSnapNumberAfter;
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


	
	
	
	
}
