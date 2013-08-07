package org.tmt.aps.peas.frame.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Transient;

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
	
	@Transient
	protected int noOfAxes; 
	@Transient
	protected int axes1; 
	@Transient
	protected int axes2;
	@Transient
	protected int bitPix; 
	@Transient
	protected String startTime; 
	@Transient
	protected String endTime; 
	@Transient
	protected String obsDate; 
	@Transient
	protected short result[][];
	
	
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
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getObsDate() {
		return obsDate;
	}
	public void setObsDate(String obsDate) {
		this.obsDate = obsDate;
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



}
