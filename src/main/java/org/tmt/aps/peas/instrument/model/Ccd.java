/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.common.RectListEncoder;

/**
 * Instrument configuration Entity class representing the Ccd database table.  Also contains transient fields for CCD state and temperature.
 * Hot pixel lists are encoded strings in the database.  This class encodes and decodes hot pixel lists from the database.
 * @author smichaels
 *
 */
@Entity
@Table(name = "Ccd")
@NamedQueries({
	@NamedQuery(name = "findCcd", query = "SELECT o from Ccd o INNER JOIN FETCH o.ccdType t INNER JOIN FETCH o.ccdGain0 "
			+ " INNER JOIN FETCH o.ccdGain1  INNER JOIN FETCH o.ccdGain2  INNER JOIN FETCH o.ccdGain3 where o.ccdId = :ccdId" ),
	@NamedQuery(name = "findInstrumentCcd", query = "SELECT o from Ccd o INNER JOIN FETCH o.ccdType t INNER JOIN FETCH o.ccdGain0 "
			+ " INNER JOIN FETCH o.ccdGain1  INNER JOIN FETCH o.ccdGain2  INNER JOIN FETCH o.ccdGain3 INNER JOIN FETCH o.instrument i"
			+ " where i.instrumentId = :instrumentId" ),
	@NamedQuery(name = "findAllCcds", query = "SELECT o from Ccd o INNER JOIN FETCH o.ccdType t INNER JOIN FETCH o.ccdGain0 "
			+ " INNER JOIN FETCH o.ccdGain1  INNER JOIN FETCH o.ccdGain2  INNER JOIN FETCH o.ccdGain3 "
			+ " LEFT OUTER JOIN o.instrument" )
})
public class Ccd {

	public static final int POWER_STATE_ON = 1;
	public static final int POWER_STATE_OFF = 2;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long ccdId;
	private String ccdName;	
	private String ccdDescription;	
	private String hotPixelListEncoded;  // encoded as p1x,p1y,p2x,p2y, etc
	private String hotColumnListEncoded;  // encoded as p1x,p1y,p2x,p2y, etc
	private float nonLinearThreshold;
	
	
	@ManyToOne
	@JoinColumn (name="instrumentId")
	private Instrument instrument;
	
	@ManyToOne
	@JoinColumn (name="ccdTypeId")
	private CcdType ccdType;
	
	@ManyToOne
	@JoinColumn (name="ccdGainId0")
	private CcdGain ccdGain0;
	
	@ManyToOne
	@JoinColumn (name="ccdGainId1")
	private CcdGain ccdGain1;
	
	@ManyToOne
	@JoinColumn (name="ccdGainId2")
	private CcdGain ccdGain2;
	
	@ManyToOne
	@JoinColumn (name="ccdGainId3")
	private CcdGain ccdGain3;
	
	
	
	@Transient
	private int state;
	@Transient
	private float temperature;
	@Transient
	private int currentGainNumber = 0;

	
	public Ccd(String ccdName, String ccdDescription, String hotPixelListEncoded, String hotColumnListEncoded, Instrument instrument) {
		this.ccdName = ccdName;
		this.ccdDescription = ccdDescription;
		this.hotPixelListEncoded = hotPixelListEncoded;
		this.hotColumnListEncoded = hotColumnListEncoded;
		this.instrument = instrument;
	}
	
	
	public Ccd(int state, float temperature, int currentGainNumber) {
		this.state = state;
		this.temperature = temperature;
		this.currentGainNumber = currentGainNumber;
	}
	
	public Ccd() {
		
	}

	public Long getCcdId() {
		return ccdId;
	}

	public void setCcdId(Long ccdId) {
		this.ccdId = ccdId;
	}
	
	public String getCcdName() {
		return ccdName;
	}

	public void setCcdName(String ccdName) {
		this.ccdName = ccdName;
	}

	public String getCcdDescription() {
		return ccdDescription;
	}
	
	public void setCcdDescription(String ccdDescription) {
		this.ccdDescription = ccdDescription;
	}

	public String getHotPixelListEncoded() {
		return hotPixelListEncoded;
	}

	public void setHotPixelListEncoded(String hotPixelListEncoded) {
		this.hotPixelListEncoded = hotPixelListEncoded;
	}

	public String getHotColumnListEncoded() {
		return hotColumnListEncoded;
	}


	public void setHotColumnListEncoded(String hotColumnListEncoded) {
		this.hotColumnListEncoded = hotColumnListEncoded;
	}


	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public CcdType getCcdType() {
		return ccdType;
	}

	public void setCcdType(CcdType ccdType) {
		this.ccdType = ccdType;
	}

	public CcdGain getCcdGain1() {
		return ccdGain1;
	}
	
	public void setCcdGain1(CcdGain ccdGain1) {
		this.ccdGain1 = ccdGain1;
	}

	public CcdGain getCcdGain2() {
		return ccdGain2;
	}

	public void setCcdGain2(CcdGain ccdGain2) {
		this.ccdGain2 = ccdGain2;
	}

	public CcdGain getCcdGain3() {
		return ccdGain3;
	}

	public void setCcdGain3(CcdGain ccdGain3) {
		this.ccdGain3 = ccdGain3;
	}

	public CcdGain getCcdGain0() {
		return ccdGain0;
	}

	public void setCcdGain0(CcdGain ccdGain0) {
		this.ccdGain0 = ccdGain0;
	}


	public int getRowCount() {
		return ccdType.getNormalReadoutHeight();
	}

	public int getColCount() {
		return ccdType.getNormalReadoutWidth();
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	
	public float getNonLinearThreshold() {
		return nonLinearThreshold;
	}


	public void setNonLinearThreshold(float nonLinearThreshold) {
		this.nonLinearThreshold = nonLinearThreshold;
	}


	public String getDisplayString() {
		switch (state) {
		case POWER_STATE_ON:
			return "On";
		case POWER_STATE_OFF:
			return "Off";
		}
		return "";
	}

	/**
	 * Returns the list of hot pixels for this CCD
	 * @return a list of rectangles for all hot pixels 
	 */
	public List<Rect> getHotPixelList() {
		
		return RectListEncoder.decodeList(hotPixelListEncoded);
	}
	
	/**
	 * Returns the list of hot columns for this CCD
	 * @return a list of rectangles for all hot columns 
	 */
	public List<Rect> getHotColumnList() {
		
		return RectListEncoder.decodeList(hotColumnListEncoded);
	}
	
	/**
	 * @return all rectangles in hot pixel list, plus generated rectangles for each pixel in each hot column
	 */
	public List<Rect> getAllHotPixelRects() {
		
		List<Rect> allHotPixelRects = new ArrayList<Rect>();
		
		for (Rect colRect : getHotColumnList()) {
			for (int i = colRect.p1.y; i<=colRect.p2.y; i++) {
				allHotPixelRects.add(new Rect(colRect.p1.x-1, i, colRect.p1.x+1, i));
			}
		}
		
		allHotPixelRects.addAll(getHotPixelList());
		
		return allHotPixelRects;
		
	}
	/**
	 * Removes a hot pixel from this CCD instance
	 * @param boundingRect
	 */
	public void removeHotPixel(Rect boundingRect) {
		
		List<Rect> hotPixelList = RectListEncoder.removeRect(getHotPixelList(), boundingRect);		
		hotPixelListEncoded = RectListEncoder.encodeList(hotPixelList);
	}
	
	/**
	 * Adds a hot pixel to this CCD instance
	 * @param hotPixel the hot pixel to add
	 */
	public void addHotPixel(Rect hotPixel) {
		
		List<Rect> hotPixelList = getHotPixelList();
		hotPixelList.add(hotPixel);
		
		hotPixelListEncoded = RectListEncoder.encodeList(hotPixelList);
	}
	
	/**
	 * Removes a hot column from this CCD
	 * @param boundingRect the rectangle bounding the hot column
	 */
	public void removeHotColumn(Rect boundingRect) {
		
		List<Rect> hotColumnList = RectListEncoder.removeRect(getHotColumnList(), boundingRect);		
		hotColumnListEncoded = RectListEncoder.encodeList(hotColumnList);
	}
		
	/**
	 * Adds a hot column to this CCD
	 * @param hotColumn the hot column to add
	 */
	public void addHotColumn(Rect hotColumn) {
		
		List<Rect> hotColumnList = getHotColumnList();
		hotColumnList.add(hotColumn);
		
		hotColumnListEncoded = RectListEncoder.encodeList(hotColumnList);
	}
	
	public void setGain(int gainNumber) {
		this.currentGainNumber = gainNumber;
	}
	
	public CcdGain getCcdGain() {
		if (currentGainNumber == 0) {
			return ccdGain0;
		} else if (currentGainNumber == 1) {
			return ccdGain1;
		} else if (currentGainNumber == 2) {
			return ccdGain2;
		} else if (currentGainNumber == 3) {
			return ccdGain3;
		} else {
			return null;
		}
	}


	public List<CcdGain> getCcdGainList() {
		List<CcdGain> ccdGainList = new ArrayList<CcdGain>();
		ccdGainList.add(ccdGain0);
		ccdGainList.add(ccdGain1);
		ccdGainList.add(ccdGain2);
		ccdGainList.add(ccdGain3);
		return ccdGainList;
	}
	
	public boolean isNewRecord() {
		return ccdId == null;
	}


	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Ccd) {
			Ccd candidate = (Ccd)obj;
			return candidate.getCcdId().longValue() == getCcdId().longValue();
		}
		
		
		return false;
	}

	
}
