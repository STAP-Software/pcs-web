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
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.computation.java.JavaComputations;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.Subimage;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.lang.interop.JfiNew;
import org.tmt.aps.peas.lang.interop.RetVal;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
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

	
	public FloatPoint[] findAndIdentify(float[][] frame, int numSpots) throws ComputationException {
		// TODO Auto-generated method stub
		
		FloatPoint[] result = new FloatPoint[numSpots];
				
		if (numSpots == Subimage.PT_DEF_X_ARRAY.length) {
			for (int i = 0; i < Subimage.PT_DEF_X_ARRAY.length; i++) {
				result[i] = new FloatPoint((float)Subimage.PT_DEF_X_ARRAY[i], (float)Subimage.PT_DEF_Y_ARRAY[i]);
			}
		}
		if (numSpots == Subimage.CPH_DEF_X_ARRAY.length) {
			for (int i = 0; i < Subimage.CPH_DEF_X_ARRAY.length; i++) {
				result[i] = new FloatPoint((float)Subimage.CPH_DEF_X_ARRAY[i], (float)Subimage.CPH_DEF_Y_ARRAY[i]);
			}
		}
		if (numSpots == Subimage.FS_DEF_X_ARRAY.length) {
			for (int i = 0; i < Subimage.FS_DEF_X_ARRAY.length; i++) {
				result[i] = new FloatPoint((float)Subimage.FS_DEF_X_ARRAY[i], (float)Subimage.FS_DEF_Y_ARRAY[i]);
			}
		}
		if (numSpots == Subimage.SUFS_DEF_X_ARRAY.length) {
			for (int i = 0; i < Subimage.SUFS_DEF_X_ARRAY.length; i++) {
				result[i] = new FloatPoint((float)Subimage.SUFS_DEF_X_ARRAY[i], (float)Subimage.SUFS_DEF_Y_ARRAY[i]);
			}
		}
		
		return result;
	}
	
	
	public FIResult fiNew(float[][] frame, int numSpots, FIConfig fiConfig, RefBeamMap currentRefMap) throws ComputationException {
		
		JfiNew jfiNew = new JfiNew();
		RetVal retVal = new RetVal();
		
		float[][] centroids = new float[numSpots][2];
		
		
		int nsp = -1; // segment number of current group
		int ngp = -1; // sufs group number
		float frame_avg = 0.0f; // average background of frame (TODO) from backgroundStats
		float frame_sigma = 20.0f; // frame background sigma (TODO) from backgroundStats
		
		List<FloatPoint> refDefCentroids = fiConfig.getRefDefMap().getCentroidMap().getValues();
		float[] x_ref_def = FloatPointListEncoder.extractXArray(refDefCentroids);
		float[] y_ref_def = FloatPointListEncoder.extractYArray(refDefCentroids);
		
		// initialize spot_flag
		// TODO: this needs to be derived from missing spots
		int[] spot_flag = new int[numSpots];
		for (int i=0; i<numSpots; i++) {
			spot_flag[i] = 2;
		}
		
		// the result object
		FIResult fiResult = new FIResult(numSpots, frame);
		

		//Object output[] = jfiNew.jfiNew(retVal, frame, nsp, ngp, frame_avg, frame_sigma, x_ref_def, y_ref_def, 
		//		fiConfig.getuEst(), fiConfig.getuDelta0(), fiConfig.getMatchbox(), fiConfig.getnThresh0(), 
		//		fiConfig.getnPeakMinThresh(), fiConfig.getnPeakMaxThresh(),
		//		fiConfig.isForceScale() ? 1 : 0, fiConfig.getForceScaleValue(),
		//		fiConfig.isForceRotation() ? 1 : 0, fiConfig.getForceRotationValue(),
		//		fiConfig.getMatchFineThresh(), fiConfig.getLensletOrientation(), 
		//		fiConfig.getSpiralRingCount(), spot_flag,
		//		fiResult.getXiRst(), fiResult.getYiRst(), fiResult.getxPeak(), fiResult.getyPeak(), 
		//		fiResult.getnDetect(), fiResult.getFiParam(), fiResult.getN0123(),
		//		fiResult.getCcdBoxesAll(), fiResult.getCcdBoxesSha(), fiResult.getCcdBoxesNum());
			

		if (retVal.getCode() > 0) {
		//	statusLogger.log(retVal);
		}

		// store scalars
		//fiResult.setNumFilledBoxes((Integer)output[0]);
		//fiResult.setFracFilledBoxes((Float)output[1]);
		//fiResult.setnSolution((Integer)output[2]);


		return fiResult;
	}
	
	
	public FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {
		return JavaComputations.pixLocationToDeltaArcSeconds(measuredPix, desiredPix, secPerPixel);
	}

	@Override
	public FloatPoint findCent(float[][] frame, FloatPoint guess, FindCentConfig findCentConfig, int spotType)
			throws ComputationException {
		// TODO Auto-generated method stub
		logger.debug("findCentConfig = " + findCentConfig);
		return new FloatPoint(guess.x - 10.0f, guess.y - 10.0f);
	}
	
	public List<FloatPoint> findCentroids(float[][] frame, List<FloatPoint> guessList, FindCentConfig findCentConfig) throws ComputationException {
		
		return guessList;
		
	}

	public int[][] removeBadPixels(int[][] frame, List<Rect> badPixelList) throws ComputationException {
		
		return frame;
		
	}
	
}
