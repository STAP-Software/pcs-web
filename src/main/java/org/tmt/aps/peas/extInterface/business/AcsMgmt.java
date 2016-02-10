/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

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

	public long commandActuatorDelta(double[] actDeltas) throws Exception {
		long start = System.currentTimeMillis();
		extInfFactory.getAcsCommand().setActuDeltas(actDeltas);
		long end = System.currentTimeMillis();
		return end - start;
	}
	
	// Convenience routine 
	public long commandActuatorDeltas(Float[][] actDeltas) throws Exception {
		float[][] myFloat = new float[actDeltas.length][actDeltas[0].length];
		for (int i=0; i<actDeltas.length; i++) {

			for (int j=0; j< actDeltas[0].length; j++) {
				myFloat[i][j] = actDeltas[i][j];
			}
		}
		long deltaMs = commandActuatorDeltas(myFloat);
		
		return deltaMs;
	}
	
	
	public long commandActuatorDeltas(float[][] actDeltas) throws Exception {
		
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

		long deltaMs = commandActuatorDelta(actDeltaCmds);

		return deltaMs;
	}
	
	public long commandActuatorDeltas(float[] actDeltas) throws Exception {
		
		// interface requires that we use indexes 1-108
		
		double[] actDeltaCmds = new double[109];

		for (int i = 0; i < 108; i++) {
			actDeltaCmds[1 + i] = actDeltas[i];
		}
		logger.info("doSendActDeltaCommands: actDeltaCmds = ");
		for (int i=0; i<109; i++) {
			logger.info(actDeltaCmds[i]);
		}

		long deltaMs = commandActuatorDelta(actDeltaCmds);

		return deltaMs;
	}

}
