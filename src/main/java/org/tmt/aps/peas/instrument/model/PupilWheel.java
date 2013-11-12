/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	@JoinColumn(name = "cameraId")
	private Camera camera;

	@OneToMany(mappedBy = "pupilWheel", fetch=FetchType.LAZY)
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

	public List<PupilMask> getOrigPupilMaskList() {
		
		return pupilMaskList;
	}

	public List<PupilMask> getNewPupilMaskList() {
		
		List<PupilMask> newList = new ArrayList<PupilMask>();
		
		if (pupilMask1 != null) newList.add(pupilMask1);
		if (pupilMask2 != null) newList.add(pupilMask2);
		if (pupilMask3 != null) newList.add(pupilMask3);
		if (pupilMask4 != null) newList.add(pupilMask4);
		if (pupilMask5 != null) newList.add(pupilMask5);
		if (pupilMask6 != null) newList.add(pupilMask6);
		
		return newList;
	}

	public void setPupilMaskList(List<PupilMask> pupilMaskList) {
		this.pupilMaskList = pupilMaskList;
	}
	
	public void updateSlotsFromList() {
		pupilMask1 = null;
		pupilMask2 = null;
		pupilMask3 = null;
		pupilMask4 = null;
		pupilMask5 = null;
		pupilMask6 = null;
		
		for (PupilMask pupilMask : pupilMaskList) {
			switch (pupilMask.getWheelPosition()) {
			case 1:
				pupilMask1 = pupilMask;
				break;
			case 2:
				pupilMask2 = pupilMask;
				break;
			case 3:
				pupilMask3 = pupilMask;
				break;
			case 4:
				pupilMask4 = pupilMask;
				break;
			case 5:
				pupilMask5 = pupilMask;
				break;
			case 6:
				pupilMask6 = pupilMask;
				break;
			}
		}
	}
	
	public void updatePupilMaskStates() {
		if (pupilMask1 != null) pupilMask1.setWheelPosition(1);
		if (pupilMask2 != null) pupilMask2.setWheelPosition(2);
		if (pupilMask3 != null) pupilMask3.setWheelPosition(3);
		if (pupilMask4 != null) pupilMask4.setWheelPosition(4);
		if (pupilMask5 != null) pupilMask5.setWheelPosition(5);
		if (pupilMask6 != null) pupilMask6.setWheelPosition(6);
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
		
		for (PupilMask pupilMask : pupilMaskList) {
			if (pupilMask.getPupilMaskType().isPupilMaskTypeSufs()) {
				return pupilMask;
			}
		}

		return null;
	}

}
