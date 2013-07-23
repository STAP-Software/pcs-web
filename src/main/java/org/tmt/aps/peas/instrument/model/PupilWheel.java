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

	

	
	
}
