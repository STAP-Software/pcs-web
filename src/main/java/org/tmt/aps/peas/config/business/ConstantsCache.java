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
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.config.model.MaskConstants;
import org.tmt.aps.peas.config.model.PhasingConstants;
import org.tmt.aps.peas.config.model.PrimaryMirrorConstants;
import org.tmt.aps.peas.config.model.PrimaryMirrorSegmentConstants;
import org.tmt.aps.peas.config.model.SufsConstants;
import org.tmt.aps.peas.config.model.TelescopeConstants;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;

/**
 * EJB Singleton cache for all constants.  On initialization, this EJB calls {@link org.tmt.aps.peas.config.business.ConstantsMgmt#loadConstants(List)} to pull all constant values from the database into this cache. 
 * Provides methods for reading constants values.  This EJB is initialized on startup.
 * @author smichaels
 */
@Singleton
@Startup
@Named
public class ConstantsCache {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ConstantsMgmt constantsMgmt;
	@EJB
	CentroidMapMgmt centroidMapMgmt;
	
	private PrimaryMirrorConstants primaryMirrorConstants;
	private PrimaryMirrorSegmentConstants primaryMirrorSegmentConstants;
	private PhasingConstants phasingConstants;
	private SufsConstants sufsConstants;
	private TelescopeConstants telescopeConstants;
	private MaskConstants maskConstants;
	

	@PostConstruct
	public void init() throws Exception {
		primaryMirrorConstants = new PrimaryMirrorConstants();
		primaryMirrorSegmentConstants = new PrimaryMirrorSegmentConstants();
		phasingConstants = new PhasingConstants();
		sufsConstants = new SufsConstants();
		telescopeConstants = new TelescopeConstants();
		maskConstants = new MaskConstants();
		
		List<Object> instances = new ArrayList<Object>();
		instances.add(primaryMirrorConstants);
		instances.add(primaryMirrorSegmentConstants);
		instances.add(phasingConstants);
		instances.add(sufsConstants);
		instances.add(telescopeConstants);
		instances.add(maskConstants);
		
		// populate constants
		constantsMgmt.loadConstants(instances);
		
		logger.info("\n\nPrimary Mirror Constants: \n" + primaryMirrorConstants);
		logger.info("\n\nPrimary Mirror Segment Constants: \n" + primaryMirrorSegmentConstants);
		logger.info("\n\nPhasing Constants: \n" + phasingConstants);
		logger.info("\n\nSUFS Constants: \n" + sufsConstants);
		logger.info("\n\nTelescope Constants: \n" + telescopeConstants);
		logger.info("\n\nMask Constants: \n" + maskConstants);
		

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

	public TelescopeConstants getTelescopeConstants() {
		return telescopeConstants;
	}

	public MaskConstants getMaskConstants() {
		return maskConstants;
	}

	
}
