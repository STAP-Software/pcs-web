/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;


public interface ComputationLibrary {

	public float actuatorLengths(float a, float b) throws ComputationException;
	
	public FloatPoint[] findAndIdentify(float[][] frame, int numSpots ) throws ComputationException;
	
	public FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel);  // local java routine
	
	public FloatPoint findCent(float[][] frame, FloatPoint guess, FindCentConfig findCentConfig, int spotType) throws ComputationException;

	public List<FloatPoint> findCentroids(float[][] frame, List<FloatPoint> guessList, FindCentConfig findCentConfig) throws ComputationException;
	
	public int[][] removeBadPixels(int[][] frame, List<Rect> badPixelList) throws ComputationException;
	
	public FIResult fiNew(float[][] frame, int numSpots, FIConfig fiConfig, RefBeamMap currentRefMap) throws ComputationException;

}
