/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;

@Entity
@Table(name = "CcdFrame")
@NamedQueries({

})
public class CcdFrame {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long ccdFrameId;
	
	private Long instrumentId;  // the instrument this frame was taken with
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn (name="cameraStateId")
	private CameraState cameraState;

	
	@Column(length = 200)
	private String fitsFilename;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Transient
	protected int noOfAxes;
	@Transient
	protected int axes1;
	@Transient
	protected int axes2;
	@Transient
	protected int bitPix;

	// are these next three required by FITS standard
	@Transient
	protected short rawFrame[][];

	@Transient
	protected float correctedFrame[][];
	
	public int getNoOfAxes() {
		return noOfAxes;
	}

	public void setNoOfAxes(int noOfAxes) {
		this.noOfAxes = noOfAxes;
	}

	public int getAxes1() {
		return axes1;
	}

	public void setAxes1(int axes1) {
		this.axes1 = axes1;
	}

	public int getAxes2() {
		return axes2;
	}

	public void setAxes2(int axes2) {
		this.axes2 = axes2;
	}

	public int getBitPix() {
		return bitPix;
	}

	public void setBitPix(int bitPix) {
		this.bitPix = bitPix;
	}

	public short[][] getRawFrame() {
		return rawFrame;
	}

	public void setRawFrame(short[][] rawFrame) {
		this.rawFrame = rawFrame;
	}

	public Long getCcdFrameId() {
		return ccdFrameId;
	}

	public CameraState getCameraState() {
		return cameraState;
	}

	public void setCameraState(CameraState cameraState) {
		this.cameraState = cameraState;
	}

	public void setCcdFrameId(Long ccdFrameId) {
		this.ccdFrameId = ccdFrameId;
	}

	public String getFitsFilename() {
		return fitsFilename;
	}

	public void setFitsFilename(String fitsFilename) {
		this.fitsFilename = fitsFilename;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public Long getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(Long instrumentId) {
		this.instrumentId = instrumentId;
	}


	public float[][] getCorrectedFrame() {
		
		// TODO: for now we copy directly from raw frame if the corrected frame is desired and not yet initialized
		if (correctedFrame == null) {
			correctedFrame = new float[1024][1024];
			for (int i = 0; i < 1024; i++) {
				for (int j = 0; j < 1024; j++) {
					correctedFrame[i][j] = rawFrame[i][j];
				}
			}
		}
		return correctedFrame;
	}

	@Transient
	byte[] falseColorPng;

	public byte[] getFalseColorPng() {
		return falseColorPng;
	}

	public void setFalseColorPng(byte[] falseColorPng) {
		this.falseColorPng = falseColorPng;
	}
	
	public int getFrameLightSource() {
		if (cameraState == null) {
			return ProcedureConfig.LIGHT_SOURCE_STAR;
		}
		return (cameraState.getRefBeamPos() > 0) ? ProcedureConfig.LIGHT_SOURCE_LED : ProcedureConfig.LIGHT_SOURCE_STAR;
	}

}
