package org.tmt.aps.peas.camera.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Instrument")
@NamedQueries({
	@NamedQuery(name = "findAllInstruments", query = "SELECT o from Instrument o" ),
	@NamedQuery(name = "findInstrument", query = "SELECT o from Instrument o where instrumentId = :instrumentId" )
})
public class Instrument {

	@Id
	private Long instrumentId;
	
	@Column(nullable=false, length=100)
	private String instrumentName;
	
	@Transient  // temporary
	Camera camera;
	@Transient // temporary
	Ccd ccd;
	
	public Instrument() {
		
	}
	
	public Instrument (String instrumentName) {
		this.instrumentName = instrumentName;
	}
	
	public Long getInstrumentId() {
		return instrumentId;
	}
	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}
	public String getInstrumentName() {
		return instrumentName;
	}
	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}
	public Camera getCamera() {
		return camera;
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	public Ccd getCcd() {
		return ccd;
	}
	public void setCcd(Ccd ccd) {
		this.ccd = ccd;
	}
	
	
}
