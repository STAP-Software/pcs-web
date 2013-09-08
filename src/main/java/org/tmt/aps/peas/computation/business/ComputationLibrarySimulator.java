/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import org.apache.log4j.Logger;

public class ComputationLibrarySimulator implements ComputationLibrary {

	Logger logger = Logger.getLogger(this.getClass());

	// package protected constructor
	ComputationLibrarySimulator() {
		
	}

	// we need a way to handle simulating the interfaces so that we can run without Fortran
	public float actuatorLengths(float a, float b) {
		
		return 0;
	}
	
}
