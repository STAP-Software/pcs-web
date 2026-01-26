/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.SufsGroup;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Singleton EJB cache that contains current configuration and state of the PCS Camera, CCD, and telescope
 * @author smichaels
 *
 */
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

	/**
	 * Called on startup, calls {@link #refresh()}
	 */
	@PostConstruct
	public void init() throws Exception {
		
		refresh();
	}

	/**
	 * Reads instrument and telescope ids from peas.properties file, loads all instrument and telescope configuration into the cache.
	 * Also loads up metadata such as all filter types, all pupil mask types and all SUFS groups.
	 * @throws Exception
	 */
	public void refresh() throws Exception {
		Long instrumentId = Long.valueOf(peasProperties.getProp("org.tmt.aps.peas.instrumentId"));
		instrument = cameraDefMgmt.findInstrument(instrumentId);	
		Long telescopeId = Long.valueOf(peasProperties.getProp("org.tmt.aps.peas.telescopeId"));
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

	/**
	 * Convenience method to return a pupil mask type given its id without having to make a trip to the database.
	 * @param pupilMaskTypeId the id of the pupil mask type record to find
	 * @return the pupil mask type record
	 */
	public PupilMaskType getPupilMaskTypeById(Long pupilMaskTypeId) {
		for (PupilMaskType pupilMaskType : pupilMaskTypeList) {
			if (pupilMaskType.getPupilMaskTypeId().equals(pupilMaskTypeId)) {
				return pupilMaskType;
			}
		}
		return null;
	}

	/**
	 * Convenience method to return a filter type given its id without having to make a trip to the database.
	 * @param filterTypeId the id of the filter type record to find
	 * @return the filter type record
	 */
	public FilterType getFilterTypeById(Long filterTypeId) {
		for (FilterType filterType : filterTypeList) {
			if (filterType.getFilterTypeId().equals(filterTypeId)) {
				return filterType;
			}
		}
		return null;
	}

	/**
	 * Convenience method to return an SUFS group given its group number without having to make a trip to the database
	 * @param sufsGroupNumber the group number to search on
	 * @return the SUFS group matching the passed group number
	 */
	public SufsGroup getSufsGroupByNumber(int sufsGroupNumber) {
		return sufsGroupMap.get(sufsGroupNumber);
	}

	
}
