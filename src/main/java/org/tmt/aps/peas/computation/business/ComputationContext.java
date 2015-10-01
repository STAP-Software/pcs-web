/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;

@Singleton
@Startup
@Deprecated
public class ComputationContext {

	@EJB 
	PeasProperties peasProperties;
		
	// caches the current state of the ACS for use in PEAS PCS
	Logger logger = Logger.getLogger(this.getClass());
	
	boolean fortranInstalled;
	
	@PostConstruct
	void init() {
		
		try {
		
			String fortranInstalledStr = peasProperties.getProp("org.tmt.aps.peas.fortran_installed");
			fortranInstalled  = new Boolean(fortranInstalledStr);
		
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	

	public ComputationLibrary getComputationLibrary() {
		
		try {
		//if (fortranInstalled) {
		//	return new ComputationLibraryImpl();
		//} else {
			return new ComputationLibrarySimulator();
		//}
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}

	
	
}
