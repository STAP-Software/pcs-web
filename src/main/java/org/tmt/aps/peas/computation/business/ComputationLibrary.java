/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.config.model.FindCentConfig;


public interface ComputationLibrary {

	public float actuatorLengths(float a, float b) throws ComputationException;
	public void findAndIdentify(float[][] frame, float[][] centroids ) throws ComputationException;
	
	public FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel);  // local java routine
	
	public FloatPoint findCentGauss(float[][] frame, FloatPoint guess, FindCentConfig findCentConfig, int spotType) throws ComputationException;
	
}
