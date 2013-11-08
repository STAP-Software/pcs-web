/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "PupilWheel")

public class PupilWheel {

	@Id
	private Long pupilWheelId;
	
	@OneToOne
	@JoinColumn (name="cameraId")
	private Camera camera;
	
	@OneToMany (mappedBy="pupilWheel")
	List<PupilMask> pupilMaskList;

	
	@Transient
	private PupilMask selectedPupilMask;

	@Transient
	private PupilMask pupilMask1;
	@Transient
	private PupilMask pupilMask2;
	@Transient
	private PupilMask pupilMask3;
	@Transient
	private PupilMask pupilMask4;
	@Transient
	private PupilMask pupilMask5;
	@Transient
	private PupilMask pupilMask6;
	
	public Long getPupilWheelId() {
		return pupilWheelId;
	}

	public void setPupilWheelId(Long pupilWheelId) {
		this.pupilWheelId = pupilWheelId;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public PupilMask getSelectedPupilMask() {
		return selectedPupilMask;
	}

	public void setSelectedPupilMask(PupilMask selectedPupilMask) {
		this.selectedPupilMask = selectedPupilMask;
	}

	public List<PupilMask> getPupilMaskList() {
		return pupilMaskList;
	}

	public void setPupilMaskList(List<PupilMask> pupilMaskList) {
		this.pupilMaskList = pupilMaskList;
	}

	public PupilMask getPupilMask1() {
		return pupilMask1;
	}

	public void setPupilMask1(PupilMask pupilMask1) {
		this.pupilMask1 = pupilMask1;
	}

	public PupilMask getPupilMask2() {
		return pupilMask2;
	}

	public void setPupilMask2(PupilMask pupilMask2) {
		this.pupilMask2 = pupilMask2;
	}

	public PupilMask getPupilMask3() {
		return pupilMask3;
	}

	public void setPupilMask3(PupilMask pupilMask3) {
		this.pupilMask3 = pupilMask3;
	}

	public PupilMask getPupilMask4() {
		return pupilMask4;
	}

	public void setPupilMask4(PupilMask pupilMask4) {
		this.pupilMask4 = pupilMask4;
	}

	public PupilMask getPupilMask5() {
		return pupilMask5;
	}

	public void setPupilMask5(PupilMask pupilMask5) {
		this.pupilMask5 = pupilMask5;
	}

	public PupilMask getPupilMask6() {
		return pupilMask6;
	}

	public void setPupilMask6(PupilMask pupilMask6) {
		this.pupilMask6 = pupilMask6;
	}

	
	public PupilMask getSufsPupilMask() {
		if (pupilMask1.getPupilMaskType().isPupilMaskTypeSufs()) {
			return pupilMask1;
		}
		if (pupilMask2.getPupilMaskType().isPupilMaskTypeSufs()) {
			return pupilMask2;
		}
		if (pupilMask3.getPupilMaskType().isPupilMaskTypeSufs()) {
			return pupilMask3;
		}
		if (pupilMask4.getPupilMaskType().isPupilMaskTypeSufs()) {
			return pupilMask4;
		}
		if (pupilMask5.getPupilMaskType().isPupilMaskTypeSufs()) {
			return pupilMask5;
		}
		if (pupilMask6.getPupilMaskType().isPupilMaskTypeSufs()) {
			return pupilMask6;
		}
		
		return null;
	}
	
	
}
