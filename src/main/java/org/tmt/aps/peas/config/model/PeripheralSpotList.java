/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.instrument.model.PupilMaskType;

@Entity
@Table(name = "PeripheralSpotList")
@NamedQueries({
	@NamedQuery(name = "findPeripheralSpotList", query = "SELECT o from PeripheralSpotList o INNER JOIN FETCH o.pupilMaskType p "
			+ "where p.pupilMaskTypeId = :pupilMaskTypeId" )
})
public class PeripheralSpotList {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long peripheralSpotListId;
	private String peripheralSpotListEncoded = "";  
	
	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	
	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}

	public Long getPeripheralSpotListId() {
		return peripheralSpotListId;
	}

	public void setPeripheralSpotListId(Long peripheralSpotListId) {
		this.peripheralSpotListId = peripheralSpotListId;
	}

	public String getPeripheralSpotListEncoded() {
		return peripheralSpotListEncoded;
	}

	public void setPeripheralSpotListEncoded(String peripheralSpotListEncoded) {
		this.peripheralSpotListEncoded = peripheralSpotListEncoded;
	}

	public boolean isNewRecord() {
		return peripheralSpotListId == null;
	}
}
