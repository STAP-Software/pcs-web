/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.config.model.PhasingConstants;
import org.tmt.aps.peas.config.model.PrimaryMirrorConstants;
import org.tmt.aps.peas.config.model.PrimaryMirrorSegmentConstants;
import org.tmt.aps.peas.config.model.SufsConstants;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

@Singleton
@Startup
public class Constants {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ConstantsMgmt constantsMgmt;
	@EJB
	CentroidMapMgmt centroidMapMgmt;
	
	private PrimaryMirrorConstants primaryMirrorConstants;
	private PrimaryMirrorSegmentConstants primaryMirrorSegmentConstants;
	private PhasingConstants phasingConstants;
	private SufsConstants sufsConstants;
	
	private List<RefBeamMap> refBeamDefMapList;

	@PostConstruct
	public void init() throws Exception {
		primaryMirrorConstants = new PrimaryMirrorConstants();
		primaryMirrorSegmentConstants = new PrimaryMirrorSegmentConstants();
		phasingConstants = new PhasingConstants();
		sufsConstants = new SufsConstants();
		
		List<Object> instances = new ArrayList<Object>();
		instances.add(primaryMirrorConstants);
		instances.add(primaryMirrorSegmentConstants);
		instances.add(phasingConstants);
		instances.add(sufsConstants);
		
		// populate constants
		constantsMgmt.loadConstants(instances);
		/*
		logger.info("\n\nPrimary Mirror Constants: \n" + primaryMirrorConstants);
		logger.info("\n\nPrimary Mirror Segment Constants: \n" + primaryMirrorSegmentConstants);
		logger.info("\n\nPhasing Constants: \n" + phasingConstants);
		logger.info("\n\nSUFS Constants: \n" + sufsConstants);
		*/
		// get ref def maps
		refBeamDefMapList = new ArrayList<RefBeamMap>();
		refBeamDefMapList.add(centroidMapMgmt.getRefBeamDefMap(new Long(1)));
		refBeamDefMapList.add(centroidMapMgmt.getRefBeamDefMap(new Long(2)));
		refBeamDefMapList.add(centroidMapMgmt.getRefBeamDefMap(new Long(3)));
		refBeamDefMapList.add(centroidMapMgmt.getRefBeamDefMap(new Long(4)));
		refBeamDefMapList.add(centroidMapMgmt.getRefBeamDefMap(new Long(5)));
		/*
		logger.info("\n\nRefDefMap 036: \n" + getRefBeamDefMap(new Long(1)));
		logger.info("\n\nRefDefMap 160: \n" + getRefBeamDefMap(new Long(2)));
		logger.info("\n\nRefDefMap 508: \n" + getRefBeamDefMap(new Long(3)));
		logger.info("\n\nRefDefMap UFS: \n" + getRefBeamDefMap(new Long(4)));
		logger.info("\n\nRefDefMap SUFS: \n" + getRefBeamDefMap(new Long(5)));
*/
	}


	public PrimaryMirrorConstants getPrimaryMirrorConstants() {
		return primaryMirrorConstants;
	}

	public PrimaryMirrorSegmentConstants getPrimaryMirrorSegmentConstants() {
		return primaryMirrorSegmentConstants;
	}

	public PhasingConstants getPhasingConstants() {
		return phasingConstants;
	}

	public SufsConstants getSufsConstants() {
		return sufsConstants;
	}

	public RefBeamMap getRefBeamDefMap(Long pupilMaskTypeId) {
		return refBeamDefMapList.get(pupilMaskTypeId.intValue()-1);
	}
	
}
