/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
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
		double[] telPosRad =  extInfFactory.getDcsCommand().queryTelPos();
		double[] telPosDeg = new double[2];
		telPosDeg[0] = telPosRad[0] / Constants.DEG2RAD;
		telPosDeg[1] = telPosRad[1] / Constants.DEG2RAD;
		
		return telPosDeg;
	}
	
	// input in arcsec, sends in rads (delte elevation is negated)
	public void commandTelescopeDeltas(double[] telescopeDeltas) throws Exception {
		
		double deltaAz = telescopeDeltas[0] * Constants.PI/ (60.0 * 60.0 * 180);
		double deltaEl = -telescopeDeltas[1]* Constants.PI/ (60.0 * 60.0 * 180);
		logger.debug("commandTelescopeDeltas:: deltaAz = " + deltaAz + ", deltaEl = " + deltaEl);
		
		extInfFactory.getDcsCommand().commandDcsOffset(deltaAz, deltaEl);
	}
	
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
	
	// input in microns, sends in mm
	public void commandSecondaryDeltasInUm(float[] secondaryDeltasUm) throws Exception {
		double[] secondaryDeltas = new double[3];
		
		for (int i=0; i< secondaryDeltas.length; i++) {
			secondaryDeltas[i] = secondaryDeltasUm[i] * Constants.MICRONS_TO_MM;
		}
		
		extInfFactory.getDcsCommand().commandDcsM2PosDelta(secondaryDeltas);
	}
	
	public void commandSecondaryDeltas(double[] secondaryDeltas) throws Exception {
		extInfFactory.getDcsCommand().commandDcsM2PosDelta(secondaryDeltas);
	}


}
