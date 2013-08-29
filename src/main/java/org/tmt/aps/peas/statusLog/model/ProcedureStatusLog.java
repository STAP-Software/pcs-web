/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcedureStatusLog {

	List<StatusLogEntry> logEntryList;

	public List<StatusLogEntry> getLogEntryList() {
		return logEntryList;
	}

	public void setLogEntryList(List<StatusLogEntry> logEntryList) {
		this.logEntryList = logEntryList;
	}
	
	public void addEntry(String entry) {
		if (logEntryList == null) {
			logEntryList = new ArrayList<StatusLogEntry>();
		}
		logEntryList.add(new StatusLogEntry(entry, new Date()));
	}
	

}
