/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
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

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.telescope.model.Telescope;

@Entity
@Table(name = "Session")
@NamedQueries({
	@NamedQuery(name = "findAllSessions", query = "SELECT s from Session s INNER JOIN s.telescope t where t.telescopeId = :telescopeId" ),
	@NamedQuery(name = "findSessionByDate", query = "SELECT DISTINCT s from Session s "
			+ "INNER JOIN FETCH s.telescope t INNER JOIN FETCH s.instrument LEFT OUTER JOIN FETCH s.procedureList p "
			+ "LEFT OUTER JOIN FETCH p.telescope LEFT OUTER JOIN FETCH p.instrument "
			+ "LEFT OUTER JOIN FETCH p.procedureType LEFT OUTER JOIN FETCH p.procedureConfigSet pcs LEFT OUTER JOIN FETCH pcs.procedureConfig LEFT OUTER JOIN FETCH pcs.globalConfig LEFT OUTER JOIN FETCH p.session "
			+ "where s.sessionDate = :sessionDate AND t.telescopeId = :telescopeId" ),
	@NamedQuery(name = "findSession", query = "SELECT DISTINCT s from Session s "
			+ "INNER JOIN FETCH s.telescope INNER JOIN FETCH s.instrument LEFT OUTER JOIN FETCH s.procedureList p "
			+ "LEFT OUTER JOIN FETCH p.telescope LEFT OUTER JOIN FETCH p.instrument "
			+ "LEFT OUTER JOIN FETCH p.procedureType LEFT OUTER JOIN FETCH p.procedureConfigSet pcs LEFT OUTER JOIN FETCH pcs.procedureConfig "
			+ "LEFT OUTER JOIN FETCH pcs.globalConfig LEFT OUTER JOIN FETCH p.session "
			+ "where s.sessionId = :sessionId" )
})
public class Session {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long sessionId;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date sessionDate;
	
	private String description;
	
	private String observers;

	@OneToMany (fetch = FetchType.LAZY, mappedBy = "session")
	private List<Procedure> procedureList;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "telescopeId")
	Telescope telescope;
	
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "instrumentId")
	Instrument instrument;

	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getObservers() {
		return observers;
	}

	public void setObservers(String observers) {
		this.observers = observers;
	}

	public String getSessionDateFormatted() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(sessionDate);
	}
	
	public String toString() {
		return "Telescope: " + telescope.getTelescopeName() + ", Date: " + getSessionDateFormatted();
	}

	public boolean isNewRecord() {
		
		return sessionId == null;
	}
	
	
}
