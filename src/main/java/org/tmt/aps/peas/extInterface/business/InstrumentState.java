/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.instrument.model.Instrument;

@Singleton
@Startup
public class InstrumentState {

	Logger logger = Logger.getLogger(this.getClass());

	// caches the instrument (CCD and Camera) state for use by PEAS PCS
	
	private Instrument instrument;
	
	
	@PostConstruct
	void init() {
		// load up CCD and Camera definition model structures
	}
	
	


	
	
}
