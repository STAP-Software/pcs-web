/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.ui;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Instrument;

/**
 * JSF Controller exposing the {#link Instrument} from the {@link PhysicalModel}
 * @author smichaels
 *
 */
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
