package org.tmt.aps.peas.camera.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "Instrument")
@NamedQueries({
	@NamedQuery(name = "findAllInstruments", query = "SELECT o from Instrument o" )
})
public class Instrument {

	@Id
	private Long instrumentId;
	
	@Column(nullable=false, length=100)
	private String instrumentName;
	
	
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
	
	
}
