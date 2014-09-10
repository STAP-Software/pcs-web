/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.extinf.StarInfo;

@Stateless
public class DcsMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ExtInfFactory extInfFactory;

	// All ACS Commands should be defined here

	// each method should be annotated with @DeviceAccessible(type=DeviceAccessible.DEVICE_ACS)


	public int queryDcsStatus() throws Exception {
		return extInfFactory.getDcsCommand().queryDcsStatus();
	}

	public double[] querySecondary() throws Exception {
		return extInfFactory.getDcsCommand().queryDcsM2Pos();
	}

	public StarInfo queryStar() throws Exception {
		return extInfFactory.getDcsCommand().queryStar();
	}

	public double[] queryTelescopePosition() throws Exception {
		return extInfFactory.getDcsCommand().queryTelPos();
	}

	// input in arcsec, sends in rads
	public void commandTelescopeDeltas(double[] telescopeDeltas) throws Exception {
		extInfFactory.getDcsCommand().commandDcsOffset(telescopeDeltas[0] * Constants.PI/ (60.0 * 60.0 * 180), telescopeDeltas[1]* Constants.PI/ (60.0 * 60.0 * 180));
	}
	
	
	
	public void commandSecondaryDeltas(double[] secondaryDeltas) throws Exception {
		extInfFactory.getDcsCommand().commandDcsM2PosDelta(secondaryDeltas);
	}


}
