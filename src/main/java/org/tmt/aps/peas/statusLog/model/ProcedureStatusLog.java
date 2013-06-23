package org.tmt.aps.peas.statusLog.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcedureStatusLog {

	List<StatusEntry> logEntryList;

	public List<StatusEntry> getLogEntryList() {
		return logEntryList;
	}

	public void setLogEntryList(List<StatusEntry> logEntryList) {
		this.logEntryList = logEntryList;
	}
	
	public void addEntry(String entry) {
		if (logEntryList == null) {
			logEntryList = new ArrayList<StatusEntry>();
		}
		logEntryList.add(new StatusEntry(entry, new Date()));
	}
	

}
