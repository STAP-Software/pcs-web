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
import jakarta.inject.Named;

import org.jboss.logging.Logger;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -4448080077149545313L;

	Logger logger = Logger.getLogger(this.getClass());

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
