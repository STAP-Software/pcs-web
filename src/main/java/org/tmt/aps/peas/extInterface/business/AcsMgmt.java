/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.util.Arrays;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import org.jboss.logging.Logger;

/**
 * EJB Session bean for the ACS command interface.  
 * This EJB is the single entry point to the ACS interface called from executors and diagnostic user interfaces. 
 * All calls are delegated to the {@link ExtInfFactory} which will delegate to either the actual RPC client interface or a simulator.
 * @author smichaels
 *
 */
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

	/**
	 * Commands actuator deltas given an array numbered from element 1 to 108
	 * @param actDeltas - array of length 109 actuator deltas, element 0 is ignored.
	 * @return the command elapsed time in ms
	 */
	public long commandActuatorDelta(double[] actDeltas) throws Exception {
		
		logger.info("commandActuatorDelta: actDeltaCmds = " + Arrays.toString(actDeltas));

		
		// check if all commands are zero, if so do not send commands
		boolean nonZero = false;
		for (double delta: actDeltas) {
			if (delta != 0.0) nonZero = true;
		}
		
		long start = System.currentTimeMillis();
		if (nonZero) {
			
			extInfFactory.getAcsCommand().setActuDeltas(actDeltas);
		}
		long end = System.currentTimeMillis();
		return end - start;
		
	}
	
	/**
	 * Commands actuator deltas given a 2-d array of Float actuators
	 * @param actDeltas array of 36,3 actuator deltas
	 * @return the command elapsed time in ms
	 */
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
	
	
	/**
	 * Commands actuator deltas given a 2-d array of float actuators
	 * @param actDeltas array of 36,3 actuator deltas
	 * @return the command elapsed time in ms
	 */
	public long commandActuatorDeltas(float[][] actDeltas) throws Exception {
		
		// interface requires that we use indexes 1-108

		
		double[] actDeltaCmds = new double[109];

		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltaCmds[1 + i * 3 + j] = actDeltas[i][j];
			}
		}

		long deltaMs = commandActuatorDelta(actDeltaCmds);

		return deltaMs;
	}
	
	/**
	 * Commands actuator deltas given an array of 108 actuators
	 * @param actDeltas array of 108 actuator deltas
	 * @return the command elapsed time in ms
	 */
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

	public double querySensorRange() throws Exception {
		return extInfFactory.getAcsCommand().getSensorRange();
	}

}
