/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.AccessTimeout;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.session.model.ExtInfConnectConfig;

/**
 * EJB Singleton cache that contains the external interface connection configuration (actual connection vs simulator, connection state, etc)
 * @author smichaels
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class ExtInfConfigState {

	Logger logger = Logger.getLogger(this.getClass());

	ExtInfConnectConfig extInfConnectConfig;
	

	@PostConstruct
	public void init() {
	    try {
	    	extInfConnectConfig = new ExtInfConnectConfig();
	    } catch (Exception e) {
	        throw new IllegalStateException("Initialization failed", e);
	    }
	}


	@Lock(LockType.READ)
	@AccessTimeout(value=120000)  // two minutes
	public ExtInfConnectConfig getExtInfConnectConfig() {
		return extInfConnectConfig;
	}


}