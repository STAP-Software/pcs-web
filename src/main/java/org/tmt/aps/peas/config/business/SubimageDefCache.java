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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.model.SubimageDef;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;

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
	PeasProperties peasProperties;
		
	private Map<Long, SubimageDefList> subimageDefMap;
	private Map<Integer, SubimageDefList> sufsSubimageDefMap;

	@PostConstruct
	public void init() throws Exception {

		String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
		Long telescopeId = new Long(telescopeIdStr);

		
		// get ref def maps
		subimageDefMap = new HashMap<Long, SubimageDefList>();
		
		// No UFS/SUFS for now, will upgrade later
		for (int i=1; i<4; i++) {
			
			List<SubimageDef> listOfSubimageDefs = centroidMapMgmt.getSubimageDefList(telescopeId, new Long(i));
			SubimageDefList subimageDefList = new SubimageDefList(listOfSubimageDefs);
							
			subimageDefMap.put(new Long(i), subimageDefList);
		
		}
		
		sufsSubimageDefMap = new HashMap<Integer, SubimageDefList>();
		



		sufsSubimageDefMap = new HashMap<Integer, SubimageDefList>();
		
		
		for (int i=0; i<7; i++) {
			
			List<SubimageDef> listOfSubimageDefs = centroidMapMgmt.getSubimageDefList(telescopeId, PupilMaskType.PUPIL_MASK_TYPE_ID_SUFS, i);
			SubimageDefList subimageDefList = new SubimageDefList(listOfSubimageDefs);
							
			sufsSubimageDefMap.put(new Integer(i), subimageDefList);
		
		}
		
		
		
		/*
		// set all its spots as missing at first
		for (int i=0; i<listOfSufsRefMapSubimageDefs.size(); i++) {
			listOfSufsRefMapSubimageDefs.get(i).setMissingSpotType(0);
		}
		for (int i=1; i<7; i++) {
			// generate missing spots as the intersection of missing spots for all groups
			List<SubimageDef> listOfSubimageDefs = sufsSubimageDefMap.get(i).getListOfSubimageDefs();
			listOfSufsRefMapSubimageDefs = missingSpotsIntersection(listOfSufsRefMapSubimageDefs, listOfSubimageDefs);

		}
		*/
		
		// add the first one for the reference beam map, which does not require an SUFS group
		subimageDefMap.put(PupilMaskType.PUPIL_MASK_TYPE_ID_SUFS, sufsSubimageDefMap.get(0));
		
		/*
		System.out.println("MISSING SUFS REF SPOTS");
		for (int i=0; i<listOfSufsRefMapSubimageDefs.size(); i++) {
			if (listOfSufsRefMapSubimageDefs.get(i).getMissingSpotType() == 0) {
				System.out.println(listOfSufsRefMapSubimageDefs.get(i).getSubimageNumber());
			}
		}
		*/
		
		// TODO: any change to missing spot type in UI needs to just call init() after DB is updated to refresh the cache
		
	}
	
	/*
	private List<SubimageDef> missingSpotsIntersection(List<SubimageDef> input1, List<SubimageDef> input2) {
		
		
		List<SubimageDef> resultList = new ArrayList<SubimageDef>();
		
		for (int i=0; i<input1.size(); i++) {
			SubimageDef def1 = input1.get(i);
			SubimageDef def2 = input2.get(i);
			
			// we really only care if it is zero (not expected for F&I) as this is only for create ref map
			// if the missing spots are both zero, then zero otherwise 1
			int missingSpotType = ((def1.getMissingSpotType() + def2.getMissingSpotType()) == 0) ? Constants.MISSING_SPOT_TYPE_NOT_EXPECTED : Constants.MISSING_SPOT_TYPE_USE;
			
			SubimageDef newSubimageDef = new SubimageDef(def1.getSubimageNumber(), def1.getCentroid(), def1.getSpotType(), missingSpotType, def1.getUseForM2Calc());
			resultList.add(newSubimageDef);
		}
		return resultList;
	}
	*/
	
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
