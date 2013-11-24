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
import javax.persistence.NamedQuery;
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
	@NamedQuery(name = "findCurrentRefBeamMap", query = "SELECT rb from RefBeamMap rb "
			+ "where rb.instrumentId = :instrumentId AND rb.pupilMaskTypeId = :pupilMaskTypeId "
			+ "and rb.firstRefBeamMapFlg = 0 and rb.refBeamDefMapFlg = 0 "
			+ "ORDER BY rb.createDate desc"),
	@NamedQuery(name = "findFirstRefBeamMap", query = "SELECT rb from RefBeamMap rb "
			+ "where rb.instrumentId = :instrumentId AND rb.pupilMaskTypeId = :pupilMaskTypeId "
			+ "and rb.firstRefBeamMapFlg = 1 and rb.refBeamDefMapFlg = 0 "
			+ "ORDER BY rb.createDate desc"),
	@NamedQuery(name = "findRefBeamDefMap", query = "SELECT rb from RefBeamMap rb "
			+ "where rb.pupilMaskTypeId = :pupilMaskTypeId "
			+ "and rb.firstRefBeamMapFlg = 0 and rb.refBeamDefMapFlg = 1 "
			+ "ORDER BY rb.createDate desc")
})
public class RefBeamMap {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long refBeamMapId;

	@Column(insertable=false, updatable=false)
	private Long instrumentId;
	
	@Column(insertable=false, updatable=false)
	private Long pupilMaskTypeId;
	
	int firstRefBeamMapFlg;
	int refBeamDefMapFlg;
	
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
	protected List<FloatPoint> values;


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

	public Long getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}

	public Long getPupilMaskTypeId() {
		return pupilMaskTypeId;
	}

	public void setPupilMaskTypeId(Long pupilMaskTypeId) {
		this.pupilMaskTypeId = pupilMaskTypeId;
	}

	public int getRefBeamDefMapFlg() {
		return refBeamDefMapFlg;
	}

	public void setRefBeamDefMapFlg(int refBeamDefMapFlg) {
		this.refBeamDefMapFlg = refBeamDefMapFlg;
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

	public List<FloatPoint> getValues() {
		return values;
	}

	public void setValues(List<FloatPoint> values) {
		this.values = values;
	}


	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("\nvalues = ");
		for (int i=0; i<values.size(); i++) {
			buf.append(values.get(i) + ", ");
		}
		buf.append("\n");
		return buf.toString();

	}



}
