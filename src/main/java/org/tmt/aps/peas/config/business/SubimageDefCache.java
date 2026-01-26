/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.model.SubimageDef;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;

/**
 * EJB Singleton cache for subimage definitions.  On initialization, this EJB calls {@link org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt#getSubimageDefList(Long, Long)} to load all subimage definition values from the database into this cache. 
 * Provides methods for reading subimage definitions.  This EJB is initialized on startup.
 * @author smichaels
 */
@Singleton
@Startup
public class SubimageDefCache {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ConstantsMgmt constantsMgmt;
	@EJB
	MissingSpotsMgmt missingSpotsMgmt;
	@EJB
	CentroidMapMgmt centroidMapMgmt;
	@EJB
	GlobalConfigMgmt globalConfigMgmt;
	@EJB
	PeasProperties peasProperties;	
	@EJB
	ConstantsCache constantsCache;
		
	private Map<Long, SubimageDefList> subimageDefMap;
	private Map<Integer, SubimageDefList> sufsSubimageDefMap;
	private Long telescopeId;
	private Long instrumentId;

	@PostConstruct
	public void init() throws Exception {

		String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
		telescopeId = Long.valueOf(telescopeIdStr);
		String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
		instrumentId = Long.valueOf(instrumentIdStr);

		
		List<Integer> mirrors = new ArrayList<Integer>();
		
		for (int i=0; i<constantsCache.getTelescopeConstants().getNumberOfSegments(); i++) {
			mirrors.add(Integer.valueOf(1));
		}

		refreshCache();
	}
	
	
	public void refreshCache() throws Exception {
		
		Integer[] mirrors = globalConfigMgmt.findDefaultConfig(telescopeId, instrumentId).getMirrorList();
		
		// get ref def maps
		subimageDefMap = new HashMap<Long, SubimageDefList>();

		
		// No UFS/SUFS for now, will upgrade later
		for (int i=1; i<4; i++) {
			
			List<SubimageDef> listOfSubimageDefs = centroidMapMgmt.getSubimageDefList(telescopeId, instrumentId, Long.valueOf(i), mirrors);
			SubimageDefList subimageDefList = new SubimageDefList(listOfSubimageDefs);
							
			subimageDefMap.put(Long.valueOf(i), subimageDefList);
		
		}
		
		sufsSubimageDefMap = new HashMap<Integer, SubimageDefList>();
		
		
		
		for (int i=0; i<7; i++) {
			
			List<SubimageDef> listOfSubimageDefs = centroidMapMgmt.getSubimageDefList(telescopeId, instrumentId, PupilMaskType.PUPIL_MASK_TYPE_ID_SUFS, mirrors, i);
			SubimageDefList subimageDefList = new SubimageDefList(listOfSubimageDefs);
							
			sufsSubimageDefMap.put(Integer.valueOf(i), subimageDefList);
		
		}
		
		
		// add the first one for the reference beam map, which does not require an SUFS group
		subimageDefMap.put(PupilMaskType.PUPIL_MASK_TYPE_ID_SUFS, sufsSubimageDefMap.get(0));
		
		
	}
	

	
	public SubimageDefList getSubimageDefList(Long pupilMaskTypeId) {
		return getSubimageDefList(pupilMaskTypeId, null);
	}
	
	public SubimageDefList getSubimageDefList(Long pupilMaskTypeId, Integer sufsGroup) {
		
		
		if (sufsGroup == null) {
			SubimageDefList subimageDefList = subimageDefMap.get(pupilMaskTypeId);
		
			return subimageDefList;
		
		} else {
			
			SubimageDefList subimageDefList = sufsSubimageDefMap.get(sufsGroup);
			
			return subimageDefList;
			
		}
	}
	
}
