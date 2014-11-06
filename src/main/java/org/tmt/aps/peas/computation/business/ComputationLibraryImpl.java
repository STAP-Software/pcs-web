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
import org.tmt.aps.peas.lang.interop.JfiNew;
import org.tmt.aps.peas.lang.interop.JfindAndIdentify;
import org.tmt.aps.peas.lang.interop.JfindCentGauss;
import org.tmt.aps.peas.lang.interop.JremoveBadPixels;
import org.tmt.aps.peas.lang.interop.Jsum;
import org.tmt.aps.peas.lang.interop.RetVal;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
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

	public int[][] removeBadPixels(int[][] frame, List<Rect> badPixelList) throws ComputationException {
		
		int[] x1 = new int[badPixelList.size()];
		int[] x2 = new int[badPixelList.size()];
		int[] y1 = new int[badPixelList.size()];
		int[] y2 = new int[badPixelList.size()];
		
		logger.debug("removeBadPixels:: ");
		int i=0;
		for (Rect rect : badPixelList) {
			logger.debug(rect);
			x1[i] = rect.p1.x;
			y1[i] = rect.p1.y;
			x2[i] = rect.p2.x;
			y2[i] = rect.p2.y;
			i++;
		}

		JremoveBadPixels jremoveBadPixels = new JremoveBadPixels();
		RetVal retVal = new RetVal();
		
		
		// add one to each guess to acccount for fortran indicies starting at 1, not zero.
		
		int[][] arrayOut = new int[frame.length][frame[0].length];
		
		Object[] result = jremoveBadPixels.jremoveBadPixels(retVal, frame, x1, y1, x2, y2, arrayOut);
		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Bad Pixel remove error");
		}

		return arrayOut;

		
		
	}
	
	
	public FloatPoint[] findAndIdentify(float[][] frame, int numSpots) throws ComputationException {
		
		JfindAndIdentify jFindAndIdentify = new JfindAndIdentify();
		RetVal retVal = new RetVal();
		
		float[][] centroids = new float[numSpots][2];
		
		jFindAndIdentify.jfindAndIdentify(retVal, frame, centroids);
		
		if (retVal.getCode() > 0) {
		//	statusLogger.log(retVal);
		}
		
		// return centroids as a FloatPoint
		FloatPoint[] centArray = new FloatPoint[numSpots];
		for (int i=0; i<numSpots; i++) {
			centArray[i] = new FloatPoint(centroids[i][0], centroids[i][1]);
		}
		return centArray;
	}
	
	public FIResult fiNew(float[][] frame, int numSpots, FIConfig fiConfig, RefBeamMap currentRefMap) throws ComputationException {
		
		JfiNew jfiNew = new JfiNew();
		RetVal retVal = new RetVal();
		
		float[] fiParams = new float[6];
		
		int nsp = -1; // segment number of current group
		int ngp = -1; // sufs group number
		
		List<FloatPoint> refDefCentroids = fiConfig.getRefDefMap().getCentroidMap().getValues();
		float[] x_ref_def = FloatPointListEncoder.extractXArray(refDefCentroids);
		float[] y_ref_def = FloatPointListEncoder.extractYArray(refDefCentroids);
		
		// initialize spot_flag
		// TODO: this needs to be derived from missing spots
		int[] spot_flag = new int[numSpots];
		for (int i=0; i<numSpots; i++) {
			spot_flag[i] = 2;
		}
		
		// Force scale and rotation values, potentially coming from current ref map
		float forceScaleValue = (fiConfig.isForceScale() && fiConfig.getForceScaleSource() == FIConfig.FORCE_SOURCE_REF_MAP) ? 
			currentRefMap.getCentroidMap().getScale() : fiConfig.getForceScaleValue();
		float forceRotationValue = (fiConfig.isForceRotation() && fiConfig.getForceRotationSource() == FIConfig.FORCE_SOURCE_REF_MAP) ? 
			currentRefMap.getCentroidMap().getRotation() : fiConfig.getForceRotationValue();
						
		// the result object
		FIResult fiResult = new FIResult(numSpots, frame);
		

		Object output[] = jfiNew.jfiNew(retVal, frame, nsp, ngp, x_ref_def, y_ref_def, 
				fiConfig.getuEst(), fiConfig.getuDelta0(), fiConfig.getMatchbox(), fiConfig.getnThresh0(), 
				fiConfig.getnPeakMinThresh(), fiConfig.getnPeakMaxThresh(),
				fiConfig.isForceScale() ? 1 : 0, forceScaleValue,
				fiConfig.isForceRotation() ? 1 : 0, forceRotationValue,
				fiConfig.getMatchFineThresh(), fiConfig.getLensletOrientation(), 
				fiConfig.getSpiralRingCount(), spot_flag,
				fiResult.getXiRst(), fiResult.getYiRst(), fiResult.getxPeak(), fiResult.getyPeak(), 
				fiResult.getnDetect(), fiParams, fiResult.getN0123(),
				fiResult.getCcdBoxesAll(), fiResult.getCcdBoxesSha(), fiResult.getCcdBoxesNum());
			

		if (retVal.getCode() > 0) {
		//	statusLogger.log(retVal);
		}
		
		// store fi_param values
		fiResult.setFourierQuality(fiParams[0]);
		fiResult.setScale(fiParams[1]);
		fiResult.setRotation(fiParams[3]); // degrees
		fiResult.setTranslation(new FloatPoint(fiParams[4], fiParams[5]));

		// store scalars
		fiResult.setNumFilledBoxes((Integer)output[0]);
		fiResult.setFracFilledBoxes((Float)output[1]);
		fiResult.setnSolution((Integer)output[2]);


		return fiResult;
	}
	
	
	
	
	
	// TODO: move to Fortran?
	public FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {

		return JavaComputations.pixLocationToDeltaArcSeconds(measuredPix, desiredPix, secPerPixel);
	}
	
}
