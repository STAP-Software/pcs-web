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

@Singleton
@Startup
public class Constants {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ConstantsMgmt constantsMgmt;
	
	private PrimaryMirrorConstants primaryMirrorConstants;
	private PrimaryMirrorSegmentConstants primaryMirrorSegmentConstants;
	private PhasingConstants phasingConstants;
	private SufsConstants sufsConstants;
	

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
		
		logger.info("\n\nPrimary Mirror Constants: \n" + primaryMirrorConstants);
		logger.info("\n\nPrimary Mirror Segment Constants: \n" + primaryMirrorSegmentConstants);
		logger.info("\n\nPhasing Constants: \n" + phasingConstants);
		logger.info("\n\nSUFS Constants: \n" + sufsConstants);

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

	
}
