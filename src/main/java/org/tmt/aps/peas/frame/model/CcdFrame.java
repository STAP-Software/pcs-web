/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

/**
 * Entity class representing the CcdFrame database table.  The class also contains a number of <code>@Transient</code> fields used to contain information 
 * about the frame, but that is not stored in the database.  These include rawFrame, correctedFrame, pupilMaskType and false color png byte array.
 * @author smichaels
 *
 */
@Entity
@Table(name = "CcdFrame")
@NamedQueries({
	@NamedQuery(name = "findCcdFrameByFilename", query = "SELECT cf from CcdFrame cf "
			+ "inner join fetch cf.cameraState "
			+ "where cf.fitsFilename = :fitsFilename ")
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

	private float avgMirrorTemp;
	private float secondaryAct1;
	private float secondaryAct2;
	private float secondaryAct3;
	private float telescopeAz;
	private float telescopeEl;

	private float intTime;
	private Integer sufsGroupNumber;
	
	private float ccdGainValue;
	
	private String ccdName;

	private int ccdGainOffsetChannel0; 
	private int ccdGainOffsetChannel1;
	
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


	public float getAvgMirrorTemp() {
		return avgMirrorTemp;
	}

	public void setAvgMirrorTemp(float avgMirrorTemp) {
		this.avgMirrorTemp = avgMirrorTemp;
	}

	public float getSecondaryAct1() {
		return secondaryAct1;
	}

	public void setSecondaryAct1(float secondaryAct1) {
		this.secondaryAct1 = secondaryAct1;
	}

	public float getSecondaryAct2() {
		return secondaryAct2;
	}

	public void setSecondaryAct2(float secondaryAct2) {
		this.secondaryAct2 = secondaryAct2;
	}

	public float getSecondaryAct3() {
		return secondaryAct3;
	}

	public void setSecondaryAct3(float secondaryAct3) {
		this.secondaryAct3 = secondaryAct3;
	}

	public float[] getSecondaryAct() {
		float[] secondaryAct = new float[3];
		secondaryAct[0] = secondaryAct1;
		secondaryAct[1] = secondaryAct2;
		secondaryAct[2] = secondaryAct3;
		return secondaryAct;
	}
	
	public float getTelescopeAz() {
		return telescopeAz;
	}

	public void setTelescopeAz(float telescopeAz) {
		this.telescopeAz = telescopeAz;
	}

	public float getTelescopeEl() {
		return telescopeEl;
	}

	public void setTelescopeEl(float telescopeEl) {
		this.telescopeEl = telescopeEl;
	}
	
	public float[] getTelescopeAzEl() {
		float[] telescopeAzEl = new float[2];
		telescopeAzEl[0] = telescopeAz;
		telescopeAzEl[1] = telescopeEl;

		return telescopeAzEl;
	}

	public float getIntTime() {
		return intTime;
	}

	public void setIntTime(float intTime) {
		this.intTime = intTime;
	}

	public Integer getSufsGroupNumber() {
		return sufsGroupNumber;
	}

	public void setSufsGroupNumber(Integer sufsGroupNumber) {
		this.sufsGroupNumber = sufsGroupNumber;
	}

	public void setCorrectedFrame(float[][] correctedFrame) {
		this.correctedFrame = correctedFrame;
	}

	public float[][] getCorrectedFrame() {
		
		// we copy directly from raw frame if the corrected frame is desired and not yet initialized
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

	public float getCcdGainValue() {
		return ccdGainValue;
	}

	public void setCcdGainValue(float ccdGainValue) {
		this.ccdGainValue = ccdGainValue;
	}

	public String getCcdName() {
		return ccdName;
	}

	public void setCcdName(String ccdName) {
		this.ccdName = ccdName;
	}

	public int getCcdGainOffsetChannel0() {
		return ccdGainOffsetChannel0;
	}

	public void setCcdGainOffsetChannel0(int ccdGainOffsetChannel0) {
		this.ccdGainOffsetChannel0 = ccdGainOffsetChannel0;
	}

	public int getCcdGainOffsetChannel1() {
		return ccdGainOffsetChannel1;
	}

	public void setCcdGainOffsetChannel1(int ccdGainOffsetChannel1) {
		this.ccdGainOffsetChannel1 = ccdGainOffsetChannel1;
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
			// for legacy frames not in the database we use the fits filename to see if it is a ref beam 
			FitsFilename fitsFilenameObject = new FitsFilename(fitsFilename);
			if (fitsFilenameObject.getProcedureTypeCd().equals("RB")) {
				return ProcedureConfig.LIGHT_SOURCE_LED;
			} else {
				return ProcedureConfig.LIGHT_SOURCE_STAR;
			}
		}
		return (cameraState.getRefBeamPos() > 0) ? ProcedureConfig.LIGHT_SOURCE_LED : ProcedureConfig.LIGHT_SOURCE_STAR;
	}

	@Transient
	PupilMaskType headerPupilMaskType;

	public PupilMaskType getHeaderPupilMaskType() {
		return headerPupilMaskType;
	}

	public void setHeaderPupilMaskType(PupilMaskType headerPupilMaskType) {
		this.headerPupilMaskType = headerPupilMaskType;
	}
	
	
}
