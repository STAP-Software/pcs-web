/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.model;

import java.util.Date;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.tmt.aps.peas.procedure.model.ProcedureType;

/**
 * Database Entity class representing a row in the RefBeamMap table.
 * @author smichaels
 *
 */
@Entity
@Table(name = "RefBeamMap")
@NamedQueries({
	@NamedQuery(
	    name = "findCurrentRefBeamMap",
	    query = "SELECT rb FROM RefBeamMap rb " +
	            "INNER JOIN FETCH rb.centroidMap " +                 // fetch root
	            "INNER JOIN FETCH rb.procedureRefBeamMap " +         // fetch root
	            "INNER JOIN rb.procedureRefBeamMap.procedure p " +  // normal join, alias allowed for filtering
	            "INNER JOIN p.procedureType pt " +                  // normal join, alias allowed
	            "INNER JOIN FETCH p.procedureCcdFrameList " +       // fetch collections you want loaded
	            "INNER JOIN FETCH p.procedureCcdFrameList.ccdFrame " +
	            "INNER JOIN FETCH p.procedureCcdFrameList.ccdFrame.cameraState " +
	            "WHERE rb.instrumentId = :instrumentId " +
	            "AND rb.centroidMap.pupilMaskTypeId = :pupilMaskTypeId " +
	            "AND rb.filterTypeId = :filterTypeId " +
	            "AND rb.refBeamDefMapFlg = false " +
	            "AND pt.procedureTypeId = 8 " +
	            "ORDER BY rb.createDate DESC"
	),
    @NamedQuery(
	    name = "findNewestRefBeamMap",
	    query = "SELECT rb FROM RefBeamMap rb " +
	            "INNER JOIN FETCH rb.centroidMap " +               // fetch root, no alias
	            "INNER JOIN FETCH rb.procedureRefBeamMap " +       // fetch root, no alias
	            "INNER JOIN rb.procedureRefBeamMap.procedure p " + // normal join with alias for filtering
	            "INNER JOIN p.procedureType pt " +                 // normal join with alias
	            "INNER JOIN FETCH p.procedureCcdFrameList " +      // fetch collections you want loaded
	            "INNER JOIN FETCH p.procedureCcdFrameList.ccdFrame " +
	            "INNER JOIN FETCH p.procedureCcdFrameList.ccdFrame.cameraState " +
	            "WHERE rb.instrumentId = :instrumentId " +
	            "AND rb.centroidMap.pupilMaskTypeId = :pupilMaskTypeId " +
	            "AND rb.refBeamDefMapFlg = false " +
	            "AND pt.procedureTypeId = 8 " +
	            "ORDER BY rb.createDate DESC"
	),
    @NamedQuery(
	    name = "findCurrentSufsRefBeamMap",
	    query = "SELECT rb FROM RefBeamMap rb " +
	            "INNER JOIN FETCH rb.centroidMap " +               // no alias
	            "INNER JOIN FETCH rb.procedureRefBeamMap " +       // no alias
	            "INNER JOIN rb.procedureRefBeamMap.procedure p " + // normal join with alias for filtering
	            "INNER JOIN p.procedureType pt " +                 // normal join with alias
	            "INNER JOIN FETCH p.procedureCcdFrameList " +      // fetch root collection
	            "INNER JOIN FETCH p.procedureCcdFrameList.ccdFrame " +
	            "INNER JOIN FETCH p.procedureCcdFrameList.ccdFrame.cameraState " +
	            "WHERE rb.instrumentId = :instrumentId " +
	            "AND rb.centroidMap.pupilMaskTypeId = :pupilMaskTypeId " +
	            "AND rb.sufsGroupNumber = :sufsGroupNumber " +
	            "AND rb.filterTypeId = :filterTypeId " +
	            "AND rb.refBeamDefMapFlg = false " +
	            "AND pt.procedureTypeId = 8 " +
	            "ORDER BY rb.createDate DESC"
	),
    @NamedQuery(
    	    name = "findNewestSufsRefBeamMap",
    	    query = "SELECT rb FROM RefBeamMap rb " +
    	            "INNER JOIN FETCH rb.centroidMap " +
    	            "INNER JOIN FETCH rb.procedureRefBeamMap " +
    	            "INNER JOIN rb.procedureRefBeamMap.procedure p " +   // normal join with alias 
    	            "INNER JOIN p.procedureType pt " +
    	            "INNER JOIN FETCH p.procedureCcdFrameList " +
    	            "INNER JOIN FETCH p.procedureCcdFrameList.ccdFrame " +
    	            "INNER JOIN FETCH p.procedureCcdFrameList.ccdFrame.cameraState " +
    	            "WHERE rb.instrumentId = :instrumentId " +
    	            "AND rb.centroidMap.pupilMaskTypeId = :pupilMaskTypeId " +
    	            "AND rb.sufsGroupNumber = :sufsGroupNumber " +
    	            "AND rb.refBeamDefMapFlg = false " +
    	            "AND pt.procedureTypeId = 8 " +
    	            "ORDER BY rb.createDate DESC"
    ),
    @NamedQuery(
        name = "findRefBeamDefMap",
        query = "SELECT rb FROM RefBeamMap rb " +
                "INNER JOIN FETCH rb.centroidMap " +   // no alias here
                "WHERE rb.centroidMap.pupilMaskTypeId = :pupilMaskTypeId " +
                "AND rb.refBeamDefMapFlg = true " +
                "ORDER BY rb.createDate DESC"
    )
})

public class RefBeamMap {

	@Id
	@SequenceGenerator(
		    name = "refBeamMap_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "refBeamMap_gen"
		)
	private Long refBeamMapId;

	private Long instrumentId;
	
	private Long filterTypeId;
	
	private Integer sufsGroupNumber;
	
	boolean refBeamDefMapFlg;
		
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn (name="centroidMapId")
	private CentroidMap centroidMap;

	@OneToOne (fetch = FetchType.LAZY, mappedBy="refBeamMap")
	private ProcedureRefBeamMap procedureRefBeamMap;

	
	public Long getRefBeamMapId() {
		return refBeamMapId;
	}

	public void setRefBeamMapId(Long refBeamMapId) {
		this.refBeamMapId = refBeamMapId;
	}

	
	
	public Long getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}

	public boolean isRefBeamDefMapFlg() {
		return refBeamDefMapFlg;
	}

	public void setRefBeamDefMapFlg(boolean refBeamDefMapFlg) {
		this.refBeamDefMapFlg = refBeamDefMapFlg;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public CentroidMap getCentroidMap() {
		return centroidMap;
	}

	public void setCentroidMap(CentroidMap centroidMap) {
		this.centroidMap = centroidMap;
	}

	public Long getFilterTypeId() {
		return filterTypeId;
	}

	public void setFilterTypeId(Long filterTypeId) {
		this.filterTypeId = filterTypeId;
	}

	public Integer getSufsGroupNumber() {
		return sufsGroupNumber;
	}

	public void setSufsGroupNumber(Integer sufsGroupNumber) {
		this.sufsGroupNumber = sufsGroupNumber;
	}

	public boolean isNewRecord() {
		return refBeamMapId == null;
	}

	public ProcedureRefBeamMap getProcedureRefBeamMap() {
		return procedureRefBeamMap;
	}

	public void setProcedureRefBeamMap(ProcedureRefBeamMap procedureRefBeamMap) {
		this.procedureRefBeamMap = procedureRefBeamMap;
	}




}
