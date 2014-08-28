/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import java.util.List;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.computation.java.JavaComputations;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.lang.interop.RetVal;
import org.tmt.aps.peas.statusLog.business.StatusLogger;

public class ComputationLibrarySimulator implements ComputationLibrary {

	Logger logger = Logger.getLogger(this.getClass());

	StatusLogger statusLogger;

	// package protected constructor
	ComputationLibrarySimulator() throws Exception {
		
		statusLogger = (StatusLogger)InitialContext.doLookup("java:module/StatusLogger");

	}

	// we need a way to handle simulating the interfaces so that we can run without Fortran
	public float actuatorLengths(float a, float b) {
		
		
		RetVal retVal = new RetVal();
		retVal.setCode(1);
		retVal.setArg0(1.1);
		retVal.setArg1(2.2);
		
		statusLogger.log(retVal);
		
		return 0;

		
	}

	
	public void findAndIdentify(float[][] frame, float[][] centroids) throws ComputationException {
		// TODO Auto-generated method stub
		
	}
	
	public FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {
		return JavaComputations.pixLocationToDeltaArcSeconds(measuredPix, desiredPix, secPerPixel);
	}

	@Override
	public FloatPoint findCentGauss(float[][] frame, FloatPoint guess, FindCentConfig findCentConfig, int spotType)
			throws ComputationException {
		// TODO Auto-generated method stub
		logger.debug("findCentConfig = " + findCentConfig);
		return new FloatPoint(guess.x - 10.0f, guess.y - 10.0f);
	}
	
	
	public int[][] removeBadPixels(int[][] frame, List<Rect> badPixelList) throws ComputationException {
		
		return frame;
		
	}
	
}
