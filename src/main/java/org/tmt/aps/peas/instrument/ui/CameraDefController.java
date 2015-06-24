/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Instrument;

@Named
@SessionScoped
public class CameraDefController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@EJB
	PhysicalModel physicalModel;

	private Instrument instrument;

	@PostConstruct
	private void init() {
		
		try {
		instrument = physicalModel.getInstrument();

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}


	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}


}
