package org.tmt.aps.peas.frame.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FitsFilesMaps {

	Map<Integer, Map<Date, List<FitsFilename>>> telescope2Fits = new HashMap<Integer, Map<Date, List<FitsFilename>>>();

	Map<String, List<FitsFilename>> type2Fits = new HashMap<String, List<FitsFilename>>();

	public FitsFilesMaps(Map<Integer, Map<Date, List<FitsFilename>>> telescope2Fits, Map<String, List<FitsFilename>> type2Fits) {
		this.telescope2Fits = telescope2Fits;
		this.type2Fits = type2Fits;
	}

	
	public Map<Integer, Map<Date, List<FitsFilename>>> getTelescope2Fits() {
		return telescope2Fits;
	}

	public void setTelescope2Fits(Map<Integer, Map<Date, List<FitsFilename>>> telescope2Fits) {
		this.telescope2Fits = telescope2Fits;
	}

	public Map<String, List<FitsFilename>> getType2Fits() {
		return type2Fits;
	}

	public void setType2Fits(Map<String, List<FitsFilename>> type2Fits) {
		this.type2Fits = type2Fits;
	}
	
}
