/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

@Entity
@Table(name = "RefBeamMap")
@NamedQueries({

})
public class RefBeamMap {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long refBeamMapId;

	int firstRefBeamMapFlg;
	
	@Column
	String refBeamMapData;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@ManyToOne
	@JoinColumn (name="instrumentId")
	private Instrument instrument;

	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;


	@Transient
	protected List<FloatPoint> valueList;


	public Long getRefBeamMapId() {
		return refBeamMapId;
	}

	public void setRefBeamMapId(Long refBeamMapId) {
		this.refBeamMapId = refBeamMapId;
	}

	public int getFirstRefBeamMapFlg() {
		return firstRefBeamMapFlg;
	}

	public void setFirstRefBeamMapFlg(int firstRefBeamMapFlg) {
		this.firstRefBeamMapFlg = firstRefBeamMapFlg;
	}

	public String getRefBeamMapData() {
		return refBeamMapData;
	}

	public void setRefBeamMapData(String refBeamMapData) {
		this.refBeamMapData = refBeamMapData;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}

	public List<FloatPoint> getValueList() {
		return valueList;
	}

	public void setValueList(List<FloatPoint> valueList) {
		this.valueList = valueList;
	}



}
