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

@Entity
@Table(name = "Ccd")
@NamedQueries({
	@NamedQuery(name = "findCcd", query = "SELECT o from Ccd o where o.ccdId = :ccdId" ),
	@NamedQuery(name = "findAllCcds", query = "SELECT o from Ccd o LEFT OUTER JOIN o.instrument" )
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
	private int rowCount;
	private int colCount;
	private int colOffset;
	
	
	@ManyToOne
	@JoinColumn (name="instrumentId")
	private Instrument instrument;
	
	@Transient
	private int state;
	@Transient
	private float temperature;

	
	public Ccd(String ccdName, String ccdDescription, String hotPixelListEncoded, String hotColumnListEncoded, Instrument instrument) {
		this.ccdName = ccdName;
		this.ccdDescription = ccdDescription;
		this.hotPixelListEncoded = hotPixelListEncoded;
		this.hotColumnListEncoded = hotColumnListEncoded;
		this.instrument = instrument;
	}
	
	
	public Ccd(int state, float temperature) {
		this.state = state;
		this.temperature = temperature;
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

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getColCount() {
		return colCount;
	}

	public void setColCount(int colCount) {
		this.colCount = colCount;
	}

	public int getColOffset() {
		return colOffset;
	}

	public void setColOffset(int colOffset) {
		this.colOffset = colOffset;
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
	
	public String getDisplayString() {
		switch (state) {
		case POWER_STATE_ON:
			return "On";
		case POWER_STATE_OFF:
			return "Off";
		}
		return "";
	}

	public List<Rect> getHotPixelList() {
		
		return RectListEncoder.decodeList(hotPixelListEncoded);
	}
	
	public List<Rect> getHotColumnList() {
		
		return RectListEncoder.decodeList(hotColumnListEncoded);
	}
	
	// this method returns all rects in hot pixel list, plus generated rects for each pixel in each hot column
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
	
	public void removeHotPixel(Rect boundingRect) {
		
		List<Rect> hotPixelList = RectListEncoder.removeRect(getHotPixelList(), boundingRect);		
		hotPixelListEncoded = RectListEncoder.encodeList(hotPixelList);
	}
	
	public void addHotPixel(Rect hotPixel) {
		
		List<Rect> hotPixelList = getHotPixelList();
		hotPixelList.add(hotPixel);
		
		hotPixelListEncoded = RectListEncoder.encodeList(hotPixelList);
	}
	
	public void removeHotColumn(Rect boundingRect) {
		
		List<Rect> hotColumnList = RectListEncoder.removeRect(getHotColumnList(), boundingRect);		
		hotColumnListEncoded = RectListEncoder.encodeList(hotColumnList);
	}
		
	public void addHotColumn(Rect hotColumn) {
		
		List<Rect> hotColumnList = getHotColumnList();
		hotColumnList.add(hotColumn);
		
		hotColumnListEncoded = RectListEncoder.encodeList(hotColumnList);
	}

}
