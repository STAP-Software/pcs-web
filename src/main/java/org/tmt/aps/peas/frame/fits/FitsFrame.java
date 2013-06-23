package org.tmt.aps.peas.frame.fits;

public class FitsFrame {
	protected int noOfAxes; 
	protected int axes1; 
	protected int axes2;
	protected int bitPix; 
	protected String startTime; 
	protected String endTime; 
	protected String obsDate; 
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



}
