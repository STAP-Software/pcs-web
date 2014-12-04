/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.model;

import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "RefBeamMap")
@NamedQueries({
	@NamedQuery(name = "findCurrentRefBeamMap", query = "SELECT rb from RefBeamMap rb "
			+ "inner join fetch rb.centroidMap cm "
			+ "where rb.instrumentId = :instrumentId AND cm.pupilMaskTypeId = :pupilMaskTypeId "
			+ "AND rb.filterTypeId = :filterTypeId "
			+ "and rb.refBeamDefMapFlg = false "
			+ "ORDER BY rb.createDate desc"),
	@NamedQuery(name = "findCurrentSufsRefBeamMap", query = "SELECT rb from RefBeamMap rb "
			+ "inner join fetch rb.centroidMap cm "
			+ "where rb.instrumentId = :instrumentId AND cm.pupilMaskTypeId = :pupilMaskTypeId "
			+ "AND rb.filterTypeId = :filterTypeId AND rb.sufsGroupNumber = :sufsGroupNumber "
			+ "and rb.refBeamDefMapFlg = false "
			+ "ORDER BY rb.createDate desc"),
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
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn (name="centroidMapId")
	private CentroidMap centroidMap;

	
	
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






}
