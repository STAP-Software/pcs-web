/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.rmi.RemoteException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

@Stateless
public class AcsMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ExtInfFactory extInfFactory;

	// All ACS Commands should be defined here

	// each method should be annotated with @DeviceAccessible(type=DeviceAccessible.DEVICE_ACS)

	public double queryMirrorTemp() throws Exception {

		return extInfFactory.getAcsCommand().getMirrTemp();
	}

	public boolean queryRunning() throws Exception {
		return extInfFactory.getAcsCommand().isRunning();
	}

	public double queryRmsActuMove() throws Exception {
		return extInfFactory.getAcsCommand().getRMSActuMove();
	}

	public int commandTakeSnap() throws Exception {
		return extInfFactory.getAcsCommand().takeSnap();
	}

	public void commandLoadSnap(int snapNumber) throws Exception {
		extInfFactory.getAcsCommand().loadSnap(snapNumber);
	}

	public void commandActuatorDelta(double[] actDeltas) throws Exception {
		extInfFactory.getAcsCommand().setActuDeltas(actDeltas);
	}

}
