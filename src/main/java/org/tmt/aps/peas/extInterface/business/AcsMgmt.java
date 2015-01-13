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
	
	// Convenience routine 
	public void commandActuatorDeltas(Double[][] actDeltas) throws Exception {
		
		// interface requires that we use indexes 1-108

		double[] actDeltaCmds = new double[109];

		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltaCmds[1 + i * 3 + j] = actDeltas[i][j];
			}
		}
		logger.info("doSendActDeltaCommands: actDeltaCmds = ");
		for (int i=0; i<109; i++) {
			logger.info(actDeltaCmds[i]);
		}

		commandActuatorDelta(actDeltaCmds);

	}

}
