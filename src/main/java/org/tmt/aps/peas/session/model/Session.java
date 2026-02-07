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

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Database Entity representing a single row in the Session table.
 * @author smichaels
 *
 */
@Entity
@Table(name = "Session")
@NamedQueries({
    @NamedQuery(
        name = "findAllSessions",
        query = "SELECT s FROM Session s " +
                "INNER JOIN s.telescope " +       // simple join, no alias needed
                "WHERE s.telescope.telescopeId = :telescopeId"
    ),
    @NamedQuery(
        name = "findLastSessions",
        query = "SELECT s FROM Session s " +
                "INNER JOIN s.telescope " +
                "WHERE s.telescope.telescopeId = :telescopeId " +
                "AND s.sessionDate < :searchDate " +
                "ORDER BY s.sessionDate DESC"
    ),
    @NamedQuery(
    	    name = "findSessionByDate",
    	    query = "SELECT DISTINCT s FROM Session s " +
    	            "INNER JOIN FETCH s.telescope " +         // fetch root association
    	            "INNER JOIN FETCH s.instrument " +        // fetch root association
    	            "LEFT OUTER JOIN FETCH s.procedureList " + // fetch root collection
    	            "LEFT OUTER JOIN s.procedureList.procedureType pt " + // normal join for filtering
    	            "LEFT OUTER JOIN s.procedureList.procedureConfigSet pcs " +
    	            "LEFT OUTER JOIN pcs.procedureConfig " +
    	            "LEFT OUTER JOIN pcs.globalConfig " +
    	            "WHERE s.sessionDate = :sessionDate " +
    	            "AND s.telescope.telescopeId = :telescopeId"
    ),
    @NamedQuery(
	    name = "findSession",
	    query = "SELECT DISTINCT s FROM Session s " +
	            "INNER JOIN FETCH s.telescope " +
	            "INNER JOIN FETCH s.instrument " +
	            "LEFT OUTER JOIN FETCH s.procedureList " +
	            "LEFT OUTER JOIN s.procedureList.procedureConfigSet pcs " +
	            "LEFT OUTER JOIN FETCH pcs.procedureConfig " +
	            "LEFT OUTER JOIN FETCH pcs.globalConfig " +
	            "LEFT OUTER JOIN FETCH pcs.sufsCoarseOffsetsConfig " +
	            "LEFT OUTER JOIN FETCH pcs.iterationListConfig " +
	            "LEFT OUTER JOIN s.procedureList.telescope t " +      // normal join, alias allowed for filtering
	            "LEFT OUTER JOIN s.procedureList.instrument i " +    // normal join, alias allowed for filtering
	            "LEFT OUTER JOIN s.procedureList.procedureType pt " +// normal join, alias allowed for filtering
	            "LEFT OUTER JOIN s.procedureList.session pSess " +   // normal join, alias allowed for filtering
	            "WHERE s.sessionId = :sessionId " +
	            "AND pt.procedureTypeId = :procedureTypeId " +       // can filter now
	            "AND t.telescopeId = :telescopeId"
	),
    
    @NamedQuery(
    	    name = "findSessionOperationalData",
    	    query = "SELECT DISTINCT s FROM Session s " +
    	            "INNER JOIN FETCH s.telescope " +
    	            "INNER JOIN FETCH s.instrument " +
    	            "LEFT JOIN s.procedureList p " +          // normal join for filtering only
    	            "WHERE s.sessionId = :sessionId " +
    	            "AND (p.operational = true OR p.procedureId IS NULL)"
    ),
    // TODO: the findSessionOperationalData also grab the full procedureList associations.  We will have to do it separately using this query
    @NamedQuery(
    	    name = "findProcedureListForSession",
    	    query = "SELECT DISTINCT p FROM Procedure p " +
    	            "LEFT JOIN FETCH p.telescope " +
    	            "LEFT JOIN FETCH p.instrument " +
    	            "LEFT JOIN FETCH p.procedureType " +
    	            "LEFT JOIN FETCH p.procedureConfigSet " + // no alias
    	            "LEFT JOIN FETCH p.procedureConfigSet.procedureConfig " +
    	            "LEFT JOIN FETCH p.procedureConfigSet.globalConfig " +
    	            "LEFT JOIN FETCH p.procedureConfigSet.sufsCoarseOffsetsConfig " +
    	            "LEFT JOIN FETCH p.procedureConfigSet.iterationListConfig " +
    	            "WHERE p.session.sessionId = :sessionId " +
    	            "AND (p.operational = true OR p.procedureId IS NULL)"
    ),

    @NamedQuery(
        name = "findSessionLight",
        query = "SELECT DISTINCT s FROM Session s " +
                "INNER JOIN FETCH s.telescope " +
                "INNER JOIN FETCH s.instrument " +
                "WHERE s.sessionId = :sessionId"
    )
})

public class Session {

	@Id
	@SequenceGenerator(
	    name = "session_gen",
	    sequenceName = "hibernate_sequence",
	    allocationSize = 1
	)
	@GeneratedValue(
	    strategy = GenerationType.SEQUENCE,
	    generator = "session_gen"
	)
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

	/**
	 * Convienience method returning the session date formatted as a UTC date
	 * @return the date as a formatted string
	 */
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
