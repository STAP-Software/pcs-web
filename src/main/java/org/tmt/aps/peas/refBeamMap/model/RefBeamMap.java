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
	@NamedQuery(name = "findCurrentRefBeamMap", query = "SELECT rb from RefBeamMap rb "
			+ "inner join fetch rb.centroidMap cm "
			+ "inner join fetch rb.procedureRefBeamMap prbm "
			+ "inner join fetch prbm.procedure p "
			+ "inner join p.procedureType pt "
			+ "inner join fetch p.procedureCcdFrameList pcfl "
			+ "inner join fetch pcfl.ccdFrame cf "
			+ "inner join fetch cf.cameraState "
			+ "where rb.instrumentId = :instrumentId AND cm.pupilMaskTypeId = :pupilMaskTypeId "
			+ "AND rb.filterTypeId = :filterTypeId "
			+ "and rb.refBeamDefMapFlg = false "
			+ "and pt.procedureTypeId = 8 " 
			+ "ORDER BY rb.createDate desc "),
	@NamedQuery(name = "findNewestRefBeamMap", query = "SELECT rb from RefBeamMap rb "
			+ "inner join fetch rb.centroidMap cm "
			+ "inner join fetch rb.procedureRefBeamMap prbm "
			+ "inner join fetch prbm.procedure p "
			+ "inner join p.procedureType pt "
			+ "inner join fetch p.procedureCcdFrameList pcfl "
			+ "inner join fetch pcfl.ccdFrame cf "
			+ "inner join fetch cf.cameraState "
			+ "where rb.instrumentId = :instrumentId AND cm.pupilMaskTypeId = :pupilMaskTypeId "
			+ "and rb.refBeamDefMapFlg = false "
			+ "and pt.procedureTypeId = 8 " 
			+ "ORDER BY rb.createDate desc "),
	@NamedQuery(name = "findCurrentSufsRefBeamMap", query = "SELECT rb from RefBeamMap rb "
			+ "inner join fetch rb.centroidMap cm "
			+ "inner join fetch rb.procedureRefBeamMap prbm "
			+ "inner join fetch prbm.procedure p "
			+ "inner join p.procedureType pt "
			+ "inner join fetch p.procedureCcdFrameList pcfl "
			+ "inner join fetch pcfl.ccdFrame cf "
			+ "inner join fetch cf.cameraState "
			+ "where rb.instrumentId = :instrumentId AND cm.pupilMaskTypeId = :pupilMaskTypeId "
			+ "AND rb.sufsGroupNumber = :sufsGroupNumber "
			+ "AND rb.filterTypeId = :filterTypeId "
			+ "and rb.refBeamDefMapFlg = false "
			+ "and pt.procedureTypeId = 8 " 
			+ "ORDER BY rb.createDate desc "),
	@NamedQuery(name = "findNewestSufsRefBeamMap", query = "SELECT rb from RefBeamMap rb "
			+ "inner join fetch rb.centroidMap cm "
			+ "inner join fetch rb.procedureRefBeamMap prbm "
			+ "inner join fetch prbm.procedure p "
			+ "inner join p.procedureType pt "
			+ "inner join fetch p.procedureCcdFrameList pcfl "
			+ "inner join fetch pcfl.ccdFrame cf "
			+ "inner join fetch cf.cameraState "
			+ "where rb.instrumentId = :instrumentId AND cm.pupilMaskTypeId = :pupilMaskTypeId "
			+ "AND rb.sufsGroupNumber = :sufsGroupNumber "
			+ "and rb.refBeamDefMapFlg = false "
			+ "and pt.procedureTypeId = 8 " 
			+ "ORDER BY rb.createDate desc "),
	@NamedQuery(name = "findRefBeamDefMap", query = "SELECT rb from RefBeamMap rb "
			+ "inner join fetch rb.centroidMap cm "
			+ "where cm.pupilMaskTypeId = :pupilMaskTypeId "
			+ "and rb.refBeamDefMapFlg = true "
			+ "ORDER BY rb.createDate desc")
})
public class RefBeamMap {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
