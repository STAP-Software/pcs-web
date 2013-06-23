package org.tmt.aps.peas.statusLog.model;

import java.util.Date;

public class StatusEntry {

	String status;
	Date createDate;
	
	public StatusEntry(String status, Date createDate) {
		this.status = status;
		this.createDate = createDate;
	}
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
}
