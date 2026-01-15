/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.util.concurrent.Future;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.extinf.StarInfo;

/**
 * EJB Session bean for the DCS command interface. 
 * This EJB is the single entry point to the DCS interface called from executors and diagnostic user interfaces. 
 * All calls are delegated to the {@link ExtInfFactory} which will delegate to either the actual RPC client interface or a simulator.
 * @author smichaels
 */
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
		double[] telPosRad =  extInfFactory.getDcsCommand().queryTelPos();
		double[] telPosDeg = new double[2];
		telPosDeg[0] = telPosRad[0] / Constants.DEG2RAD;
		telPosDeg[1] = telPosRad[1] / Constants.DEG2RAD;
		
		return telPosDeg;
	}
	
	/**
	 * Sends telescope delta commands synchronously; takes input in arcseconds, sends in radians.  Delta elevation is negated.
	 * @param telescopeDeltas delta Azimuth and Elevation in arcseconds
	 */
	public void commandTelescopeDeltas(double[] telescopeDeltas) throws Exception {
		
		double deltaAz = telescopeDeltas[0] * Constants.PI/ (60.0 * 60.0 * 180);
		double deltaEl = -telescopeDeltas[1]* Constants.PI/ (60.0 * 60.0 * 180);
		logger.debug("commandTelescopeDeltas:: deltaAz = " + deltaAz + ", deltaEl = " + deltaEl);
		
		extInfFactory.getDcsCommand().commandDcsOffset(deltaAz, deltaEl);
	}
	
	/**
	 * Sends telescope delta commands asynchronously; takes input in arcseconds, sends in radians.  Delta elevation is negated.
	 * @param telescopeDeltas delta Azimuth and Elevation in arcseconds
	 */
	@Asynchronous
	public Future<Exception> commandTelescopeDeltasAsync(double[] telescopeDeltas) {
		
		Exception ex = null;
		
		try {
			double deltaAz = telescopeDeltas[0] * Constants.PI/ (60.0 * 60.0 * 180);
			double deltaEl = -telescopeDeltas[1]* Constants.PI/ (60.0 * 60.0 * 180);
			logger.debug("commandTelescopeDeltas:: deltaAz = " + deltaAz + ", deltaEl = " + deltaEl);
		
			extInfFactory.getDcsCommand().commandDcsOffset(deltaAz, deltaEl);
		
		} catch (Exception e) {
			ex = e;
		}
			
		return new AsyncResult<Exception>(ex);
	}
	
	/**
	 * Sends secondary commands in mm, given passed secondary commands in microns
	 * @param secondaryDeltasUm secondary commands in micons
	 */
	public void commandSecondaryDeltasInUm(float[] secondaryDeltasUm) throws Exception {
		double[] secondaryDeltas = new double[3];
		
		for (int i=0; i< secondaryDeltas.length; i++) {
			secondaryDeltas[i] = secondaryDeltasUm[i] * Constants.MICRONS_TO_MM;
		}
		
		extInfFactory.getDcsCommand().commandDcsM2PosDelta(secondaryDeltas);
	}
	
	/**
	 * Sends secondary commands in mm
	 * @param secondaryDeltas secondary commands in mm
	 */
	public void commandSecondaryDeltas(double[] secondaryDeltas) throws Exception {
		extInfFactory.getDcsCommand().commandDcsM2PosDelta(secondaryDeltas);
	}

	public double[] queryM2FocusAndTilts() throws Exception {
		double[] m2FocusAndTilts =  extInfFactory.getDcsCommand().queryM2FocusAndTilt();
		
		return m2FocusAndTilts;
	}

}
