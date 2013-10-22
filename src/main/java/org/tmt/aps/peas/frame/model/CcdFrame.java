/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.model;

import java.io.ByteArrayInputStream;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Entity
@Table(name = "CcdFrame")
@NamedQueries({

})
public class CcdFrame {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long ccdFrameId;

	@Column(length=200)
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
	protected short result[][];
	
	@Transient
	protected float values[][];
	
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

	public short[][] getResult() {
		return result;
	}
	public void setResult(short[][] result) {
		this.result = result;
	}
	public Long getCcdFrameId() {
		return ccdFrameId;
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

	public float[][] getValue() {
		float[][] value = new float[1024][1024];
		for (int i=0; i<1024; i++) {
			for (int j=0; j<1024; j++) {
				value[i][j] = result[i][j];
			}
		}
			return value;
	}
	
	@Transient
	byte[] falseColorPng;
	public byte[] getFalseColorPng() {
		return falseColorPng;
	}
	public void setFalseColorPng(byte[] falseColorPng) {
		this.falseColorPng = falseColorPng;
	}


}
