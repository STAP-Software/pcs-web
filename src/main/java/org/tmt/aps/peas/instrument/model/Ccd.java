package org.tmt.aps.peas.instrument.model;

import java.awt.Point;
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

import org.tmt.aps.peas.common.PointListEncoder;

@Entity
@Table(name = "Ccd")
@NamedQueries({
	@NamedQuery(name = "findCcd", query = "SELECT o from Ccd o where ccdId = :ccdId" ),
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
	private String hotPixelListEncoded;  // encoded as x1,y1,x2,y2, etc
	
	@ManyToOne
	@JoinColumn (name="instrumentId")
	private Instrument instrument;
	
	@Transient
	private int state;
	@Transient
	private float temperature;

	
	public Ccd(String ccdName, String ccdDescription, String hotPixelListEncoded, Instrument instrument) {
		this.ccdName = ccdName;
		this.ccdDescription = ccdDescription;
		this.hotPixelListEncoded = hotPixelListEncoded;
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


	public Instrument getInstrument() {
		return instrument;
	}


	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
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

	public List<Point> getHotPixelList() {
		
		return PointListEncoder.decodeList(hotPixelListEncoded);
	}
	
	
	public void removeHotPixel(Point hotPixel) {
		
		List<Point> hotPixelList = PointListEncoder.removePoint(getHotPixelList(), hotPixel);		
		hotPixelListEncoded = PointListEncoder.encodeList(hotPixelList);
	}
	
	
	
	public void addHotPixel(Point hotPixel) {
		
		List<Point> hotPixelList = getHotPixelList();
		hotPixelList.add(hotPixel);
		
		hotPixelListEncoded = PointListEncoder.encodeList(hotPixelList);
	}
}
