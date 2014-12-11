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
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;

@Singleton
@Startup
@DependsOn("PeasProperties")
public class PhysicalModel {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	CameraDefMgmt cameraDefMgmt;
	@EJB
	TelescopeMgmt telescopeMgmt;
	@EJB
	private PeasProperties peasProperties;
	
	private Instrument instrument;	
	private Telescope telescope;

	@PostConstruct
	public void init() throws Exception {
		
		refresh();
	}

	public void refresh() throws Exception {
		Long instrumentId = new Long(peasProperties.getProp("org.tmt.aps.peas.instrumentId"));
		instrument = cameraDefMgmt.findInstrument(instrumentId);	
		Long telescopeId = new Long(peasProperties.getProp("org.tmt.aps.peas.telescopeId"));
		telescope = telescopeMgmt.findTelescope(telescopeId);	
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public Telescope getTelescope() {
		return telescope;
	}

	public void setTelescope(Telescope telescope) {
		this.telescope = telescope;
	}




	
}
