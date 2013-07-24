package org.tmt.aps.peas.instrument.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Ccd")
@NamedQueries({
	@NamedQuery(name = "findCcd", query = "SELECT o from Ccd o where ccdId = :ccdId" )
})
public class Ccd {

	public static final int POWER_STATE_ON = 1;
	public static final int POWER_STATE_OFF = 2;

	@Id
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
		// list is encoded as x1,y1,x2,y2, etc
		List<String> items = Arrays.asList(hotPixelListEncoded.split("\\s*,\\s*"));
		List<Point> hotPixelList = new ArrayList<Point>();
		for (int i=0; i<items.size()/2; i++) {
			Point point = new Point(new Integer(items.get(i*2)), new Integer(items.get((i*2)+1)));
			hotPixelList.add(point);
		}
		return hotPixelList;
	}
}
