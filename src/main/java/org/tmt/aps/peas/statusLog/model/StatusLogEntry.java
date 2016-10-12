/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Database Entity class representing a row in the the StatusLogEntry table.  Supports queries by procedure id.
 * @author smichaels
 *
 */
@Entity
@Table(name = "StatusLogEntry")
@NamedQueries({
	@NamedQuery(name = "findEntriesByProcedureId", query = "SELECT s from StatusLogEntry s "
			+ "where s.procedureId = :procedureId "
			+ "ORDER BY s.createDate" )
})
public class StatusLogEntry {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long statusLogEntryId;
	
	Long procedureId;
	String status;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date createDate;
	
	public StatusLogEntry(String status, Date createDate) {
		this.status = status;
		this.createDate = createDate;
	}
	
	public StatusLogEntry() {
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public Long getStatusLogEntryId() {
		return statusLogEntryId;
	}


	public void setStatusLogEntryId(Long statusLogEntryId) {
		this.statusLogEntryId = statusLogEntryId;
	}


	public Long getProcedureId() {
		return procedureId;
	}


	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}
	
	
}
