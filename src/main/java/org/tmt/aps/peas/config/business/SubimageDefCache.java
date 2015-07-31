/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.config.model.MissingSpotList;
import org.tmt.aps.peas.config.model.SubimageDef;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

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
		
	private Map<Long, List<SubimageDef>> subimageDefMap;

	@PostConstruct
	public void init() throws Exception {

		// get ref def maps
		subimageDefMap = new HashMap<Long, List<SubimageDef>>();
		
		// No UFS/SUFS for now, will upgrade later
		for (int i=1; i<4; i++) {
			
			RefBeamMap refBeamMap = centroidMapMgmt.getRefBeamDefMap(new Long(i));
			List<SubimageDef> subimageDefList = refBeamMap.getCentroidMap().getFindCentroidsResult().getSubimageDefList();
							
			subimageDefMap.put(new Long(i), subimageDefList);
		
		}
		
		// TODO: any change to missing spot type in UI needs to just call init() after DB is updated to refresh the cache
		
	}
	
	public List<SubimageDef> getSubimageDefList(Long pupilMaskTypeId) {
		
		return subimageDefMap.get(pupilMaskTypeId);
	}
	
}
