/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.computation.java.JavaComputations;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.lang.interop.JfindAndIdentify;
import org.tmt.aps.peas.lang.interop.JfindCentGauss;
import org.tmt.aps.peas.lang.interop.Jsum;
import org.tmt.aps.peas.lang.interop.RetVal;
import org.tmt.aps.peas.statusLog.business.StatusLogger;



public class ComputationLibraryImpl implements ComputationLibrary {

	Logger logger = Logger.getLogger(this.getClass());

	StatusLogger statusLogger;
	
	// package protected constructor
	ComputationLibraryImpl() throws Exception {
		
		statusLogger = (StatusLogger)InitialContext.doLookup("java:module/StatusLogger");
		
	}

	public float actuatorLengths(float a, float b) throws ComputationException {
		
		Jsum jsum = new Jsum();
		RetVal retVal = new RetVal();
		float[] c = new float[1];
		jsum.sum(retVal, a, b, c);
		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
		}
		
		return c[0];
	}

	public FloatPoint findCentGauss(float[][] frame, FloatPoint guess, FindCentConfig findCentConfig, int nspotType) throws ComputationException {
		
		JfindCentGauss jfindCentGauss = new JfindCentGauss();
		RetVal retVal = new RetVal();
		
		logger.debug("findCentGauss::  " + guess + ", value = " + frame[(int)guess.x][(int)guess.y]);
		
		// add one to each guess to acccount for fortran indicies starting at 1, not zero.
		
		Object[] result = jfindCentGauss.jfindCentGauss(retVal, frame, findCentConfig.getIrad(), findCentConfig.getImargin(), 
				(int)guess.x + 1, (int)guess.y + 1, findCentConfig.getItermax(), nspotType, findCentConfig.getNgauss());
		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("No good centroid could be found");
		}

		FloatPoint centroid = new FloatPoint((Float)result[0], (Float)result[1]);

		return centroid;
	}

	public void findAndIdentify(float[][] frame, float[][] centroids) throws ComputationException {
		
		JfindAndIdentify jFindAndIdentify = new JfindAndIdentify();
		RetVal retVal = new RetVal();
		jFindAndIdentify.jfindAndIdentify(retVal, frame, centroids);
		
		if (retVal.getCode() > 0) {
		//	statusLogger.log(retVal);
		}
	}
	
	// TODO: move to Fortran?
	public FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {

		return JavaComputations.pixLocationToDeltaArcSeconds(measuredPix, desiredPix, secPerPixel);
	}
	
}
