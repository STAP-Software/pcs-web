/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.config.model.IterableEntity;

/**
 * Instrument configuration Entity class representing the ReferenceBeam table.  
 * @author smichaels
 */
@Entity
@Table(name = "ReferenceBeam")
@NamedQueries({
	@NamedQuery(name = "findByNumberAndInstrument", query = "SELECT o from ReferenceBeam o INNER JOIN o.camera c "
			+ "where o.refBeamNum = :refBeamNum and c.cameraId = :instrumentId" ),
	@NamedQuery(name = "findByNumber", query = "SELECT o from ReferenceBeam o INNER JOIN o.camera c "
			+ "where o.refBeamNum = :refBeamNum and c.cameraId = :cameraId " ),
	@NamedQuery(name = "findRefBeamByInstrument", query = "SELECT o from ReferenceBeam o INNER JOIN o.camera c "
			+ "where c.cameraId = :instrumentId" ),

})
public class ReferenceBeam implements IterableEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long referenceBeamId;

	private int refBeamNum;

	private float wavelength;
	private int segmentNumAlignment;  // segment number offset ref beams are aligned to 0 = no offset

	@ManyToOne
	@JoinColumn (name="cameraId")
	private Camera camera;

	
	
	public Long getReferenceBeamId() {
		return referenceBeamId;
	}

	public void setReferenceBeamId(Long referenceBeamId) {
		this.referenceBeamId = referenceBeamId;
	}

	public int getRefBeamNum() {
		return refBeamNum;
	}

	public void setRefBeamNum(int refBeamNum) {
		this.refBeamNum = refBeamNum;
	}

	public float getWavelength() {
		return wavelength;
	}

	public void setWavelength(float wavelength) {
		this.wavelength = wavelength;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public int getSegmentNumAlignment() {
		return segmentNumAlignment;
	}

	public void setSegmentNumAlignment(int segmentNumAlignment) {
		this.segmentNumAlignment = segmentNumAlignment;
	}

	public boolean isNewRecord() {
		return referenceBeamId == null;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReferenceBeam) {
			ReferenceBeam candidate = (ReferenceBeam)obj;
			return candidate.getReferenceBeamId().equals(referenceBeamId);
		}
		return super.equals(obj);
	}

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	@Override
	public String getKeyFieldName() {
		return "refBeamNum";
	}

	@Override
	public String getLabelFieldName() {
		
		return "refBeamNum";
	}

	@Override
	public String getLabel() {
		
		return "Ref Beam";
	}


}
