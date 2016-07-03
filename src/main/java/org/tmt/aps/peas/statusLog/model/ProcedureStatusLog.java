/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.statusLog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data class for status logs.  A procedure status log is a List of {@link StatusLogEntry} objects
 * @author smichaels
 *
 */
public class ProcedureStatusLog {

	List<StatusLogEntry> logEntryList;

	public List<StatusLogEntry> getLogEntryList() {
		return logEntryList;
	}

	public void setLogEntryList(List<StatusLogEntry> logEntryList) {
		this.logEntryList = logEntryList;
	}
	
	/**
	 * Adds an entry to the log entry list, apply the current date/time to the entry
	 * @param entry the entry to add
	 */
	public void addEntry(String entry) {
		if (logEntryList == null) {
			logEntryList = new ArrayList<StatusLogEntry>();
		}
		logEntryList.add(new StatusLogEntry(entry, new Date()));
	}
	

}
