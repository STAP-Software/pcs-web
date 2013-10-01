/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import javax.inject.Named;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.lang.interop.Jsum;
import org.tmt.aps.peas.lang.interop.RetVal;
import org.tmt.aps.peas.statusLog.business.StatusLogger;



public class ComputationLibraryImpl implements ComputationLibrary {

	Logger logger = Logger.getLogger(this.getClass());

	StatusLogger statusLogger;
	
	// package protected constructor
	ComputationLibraryImpl() throws Exception{
		
		statusLogger = (StatusLogger)InitialContext.doLookup("java:module/StatusLogger");
		
	}

	// we need a way to handle simulating the interfaces so that we can run without Fortran
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
	

	
}
