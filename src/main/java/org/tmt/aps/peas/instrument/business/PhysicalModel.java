/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.business;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.instrument.model.Instrument;

@Singleton
@Startup
@DependsOn("PeasProperties")
public class PhysicalModel {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	CameraDefMgmt cameraDefMgmt;
	@EJB
	private PeasProperties peasProperties;
	
	private Instrument instrument;

	@PostConstruct
	public void init() throws Exception {
		
		refresh();
	}

	private void refresh() throws Exception {
		Long instrumentId = new Long(peasProperties.getProp("org.tmt.aps.peas.instrumentId"));
		instrument = cameraDefMgmt.findInstrument(instrumentId);		
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}




	
}
