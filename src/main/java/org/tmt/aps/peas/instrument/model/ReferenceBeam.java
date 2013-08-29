/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ReferenceBeam")
public class ReferenceBeam {

	@Id
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





}
