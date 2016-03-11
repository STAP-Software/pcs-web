/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.SufsGroup;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;

@Singleton
@Startup
@DependsOn("PeasProperties")
public class PhysicalModel {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	CameraDefMgmt cameraDefMgmt;
	@EJB
	TelescopeMgmt telescopeMgmt;
	@EJB
	private PeasProperties peasProperties;
	
	private Instrument instrument;	
	private Telescope telescope;

	// metadata
	private List<PupilMaskType> pupilMaskTypeList;
	private List<FilterType> filterTypeList;
	private Map<Integer, SufsGroup> sufsGroupMap;

	@PostConstruct
	public void init() throws Exception {
		
		refresh();
	}

	public void refresh() throws Exception {
		Long instrumentId = new Long(peasProperties.getProp("org.tmt.aps.peas.instrumentId"));
		instrument = cameraDefMgmt.findInstrument(instrumentId);	
		Long telescopeId = new Long(peasProperties.getProp("org.tmt.aps.peas.telescopeId"));
		telescope = telescopeMgmt.findTelescope(telescopeId);	
		
		filterTypeList = cameraDefMgmt.findAllFilterTypes();
		pupilMaskTypeList = cameraDefMgmt.findAllPupilMaskTypes();
		
		List<SufsGroup> sufsGroups = cameraDefMgmt.findSufsGroups();
		sufsGroupMap = new HashMap<Integer, SufsGroup>();
		for (SufsGroup sufsGroup : sufsGroups) {
			sufsGroupMap.put(sufsGroup.getGroupNumber(), sufsGroup);
		}		
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public Telescope getTelescope() {
		return telescope;
	}

	public void setTelescope(Telescope telescope) {
		this.telescope = telescope;
	}

	public PupilMaskType getPupilMaskTypeById(Long pupilMaskTypeId) {
		for (PupilMaskType pupilMaskType : pupilMaskTypeList) {
			if (pupilMaskType.getPupilMaskTypeId().equals(pupilMaskTypeId)) {
				return pupilMaskType;
			}
		}
		return null;
	}

	public SufsGroup getSufsGroupByNumber(int sufsGroupNumber) {
		return sufsGroupMap.get(sufsGroupNumber);
	}

	
}
