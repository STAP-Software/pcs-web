/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
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
	void init() throws Exception {
		
		extInfConnectConfig = new ExtInfConnectConfig();

	}

	@Lock(LockType.READ)
	@AccessTimeout(value=120000)  // two minutes
	public ExtInfConnectConfig getExtInfConnectConfig() {
		return extInfConnectConfig;
	}


}