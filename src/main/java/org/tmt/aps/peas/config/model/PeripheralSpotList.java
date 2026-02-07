/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.tmt.aps.peas.instrument.model.PupilMaskType;

/**
 * Configuration entity class representing the PeripheralSpotList table
 * @author smichaels
 */
@Entity
@Table(name = "PeripheralSpotList")
@NamedQueries({
    @NamedQuery(
        name = "findPeripheralSpotList",
        query = "SELECT o FROM PeripheralSpotList o " +
                "INNER JOIN FETCH o.pupilMaskType " +  // no alias here
                "WHERE o.pupilMaskType.pupilMaskTypeId = :pupilMaskTypeId"
    )
})

public class PeripheralSpotList {
	
	@Id
	@SequenceGenerator(
		    name = "peripheralSpotList_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "peripheralSpotList_gen"
		)
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
