package org.tmt.aps.peas.extInterface.business;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class AcsState {

	// caches the current state of the ACS for use in PEAS PCS
	
	@PostConstruct
	void init() {
		
	}
	


	
	
}
