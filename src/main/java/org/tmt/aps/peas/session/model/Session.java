package org.tmt.aps.peas.session.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tmt.aps.peas.procedure.model.Procedure;

@Entity
@Table(name = "Session")
@NamedQueries({
	@NamedQuery(name = "findAllSessions", query = "SELECT s from Session s" ),
	@NamedQuery(name = "findSession", query = "SELECT DISTINCT s from Session s LEFT OUTER JOIN s.procedureList where s.sessionId = :sessionId" )
})
public class Session {

	@Id
	private Long sessionId;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date sessionDate;

	@OneToMany (mappedBy = "session")
	private List<Procedure> procedureList;
	
	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public Date getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(Date sessionDate) {
		this.sessionDate = sessionDate;
	}

	public List<Procedure> getProcedureList() {
		return procedureList;
	}

	public void setProcedureList(List<Procedure> procedureList) {
		this.procedureList = procedureList;
	}

	
	
	
	
	
}
