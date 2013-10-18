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

@Singleton
@Startup
public class Constants {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ConstantsMgmt constantsMgmt;
	
	private PrimaryMirrorConstants primaryMirrorConstants;
	private PrimaryMirrorSegmentConstants primaryMirrorSegmentConstants;
	private PhasingConstants phasingConstants;
	

	@PostConstruct
	public void init() throws Exception {
		primaryMirrorConstants = new PrimaryMirrorConstants();
		primaryMirrorSegmentConstants = new PrimaryMirrorSegmentConstants();
		phasingConstants = new PhasingConstants();
		
		List<Object> instances = new ArrayList<Object>();
		instances.add(primaryMirrorConstants);
		instances.add(primaryMirrorConstants);
		instances.add(phasingConstants);
		
		// populate constants
		constantsMgmt.loadConstants(instances);
		
		logger.info("Constant OUTPUT IS: " + primaryMirrorConstants);
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

	
}
