/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.common.cdi.Computation;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.java.JavaComputations;
import org.tmt.aps.peas.computation.model.AutoCenterTelCheckResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActDeltasRmsStdResult;
import org.tmt.aps.peas.computation.model.CalcM2ActuatorsFromPttResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CalcM2PttErrorsMeanStdResult;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CalcSegmentMeanTipTiltsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.computation.model.FindCentResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.FineScreenScaleErrorResult;
import org.tmt.aps.peas.computation.model.PassiveTiltScaleErrorResult;
import org.tmt.aps.peas.computation.model.PseudoTipTiltCentroidStatsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.computation.model.Subimage;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.AutoRefMapConfig;
import org.tmt.aps.peas.config.model.CalcM2M1Config;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfig;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.PupilRegErrorConfig;
import org.tmt.aps.peas.config.model.TelescopeConstants;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.lang.interop.JcalculateCentroidOffsets;
import org.tmt.aps.peas.lang.interop.JcalculateCentroidStats;
import org.tmt.aps.peas.lang.interop.JcalculateM2M1RayTrace;
import org.tmt.aps.peas.lang.interop.JcalculatePupilRegError;
import org.tmt.aps.peas.lang.interop.JdecomposeActs;
import org.tmt.aps.peas.lang.interop.JfindAndIdentify;
import org.tmt.aps.peas.lang.interop.JfindCent;
import org.tmt.aps.peas.lang.interop.JfindCentroids;
import org.tmt.aps.peas.lang.interop.JfineScreenScaleError;
import org.tmt.aps.peas.lang.interop.Jm2ActuatorsFromPtt;
import org.tmt.aps.peas.lang.interop.JoptimalPistons;
import org.tmt.aps.peas.lang.interop.JpassiveTiltScaleError;
import org.tmt.aps.peas.lang.interop.JremoveBadPixels;
import org.tmt.aps.peas.lang.interop.Jsum;
import org.tmt.aps.peas.lang.interop.JttOffsetsToActs;
import org.tmt.aps.peas.lang.interop.RetVal;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.exception.HandMarkRequiredException;
import org.tmt.aps.peas.procedure.exception.UserAssistRequiredException;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.statusLog.business.StatusLogger;

@Stateless
public class ComputationLibraryImpl {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	StatusLogger statusLogger;


	public float actuatorLengths(float a, float b) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "actuatorLengths"));

		Jsum jsum = new Jsum();
		RetVal retVal = new RetVal();
		float[] c = new float[1];
		jsum.sum(retVal, a, b, c);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "actuatorLengths"));

		return c[0];
	}

	@Computation
	public FindCentResult findCent(float[][] frame, FloatPoint guess, FindCentConfig findCentConfig, int nspotType) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "findCent"));

		JfindCent jfindCent = new JfindCent();
		RetVal retVal = new RetVal();
		
		logger.debug("findCent::  " + guess + ", value = " + frame[(int) guess.x][(int) guess.y]);

		// add one to each guess to acccount for fortran indicies starting at 1, not zero.

		Object[] result = jfindCent.jfindCent(retVal, frame, findCentConfig.getIrad(), findCentConfig.getImargin(), (int) guess.x + 1,
				(int) guess.y + 1, findCentConfig.getItermax(), nspotType, findCentConfig.getNgauss());

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("No good centroid could be found");
		}

		FloatPoint centroid = new FloatPoint((Float) result[0], (Float) result[1]);
		
		Subimage subimage = new Subimage(centroid, (Float)result[2], (Float)result[3], 0);
		
		FindCentResult findCentResult = new FindCentResult(guess, subimage);

		logger.info(MessageGenerator.generateMessage("computation.success", "findCent"));

		return findCentResult;
	}


	// findCentStatus is also a property of a spot, to be used by calcs after this.
	public FindCentroidsResult findCentroids(float[][] frame, FIResult fiResult, FindCentConfig findCentConfigInterior,  FindCentConfig findCentConfigPeripheral, int[] nspotTypes, int[] missingSpotFlags, boolean isRefMap) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "findCentroids"));

		JfindCentroids jfindCentroids = new JfindCentroids();
		RetVal retVal = new RetVal();

		// logger.debug("findCent::  " + guess + ", value = " + frame[(int)guess.x][(int)guess.y]);

		// add one to each guess to acccount for fortran indicies starting at 1, not zero.

		int arrayLen = fiResult.getnDetect().length;

		// for our guesses, we use peak location if only one peak in the box, otherwise we use the estimated location
		List<FloatPoint> guessList = new ArrayList<FloatPoint>();
		for (int i = 0; i < arrayLen; i++) {
			if (fiResult.getnDetect()[i] == 1) {
				guessList.add(fiResult.getPeakLocationList().get(i));
			} else {
				guessList.add(fiResult.getRstLocationList().get(i));
			}
		}

		int[] x_guesses = Utils.floatArrayToIntArray(FloatPointListEncoder.extractXArray(guessList));
		int[] y_guesses = Utils.floatArrayToIntArray(FloatPointListEncoder.extractYArray(guessList));


		int[] nGauss = new int[arrayLen];
		int[] irad = new int[arrayLen];
		int[] imargin = new int[arrayLen];
		int[] itermax = new int[arrayLen];
		for (int i = 0; i < arrayLen; i++) {
			
			if (nspotTypes[i] == Constants.SPOT_TYPE_INTERIOR) {
				nGauss[i] = findCentConfigInterior.getNgauss();
				irad[i] = findCentConfigInterior.getIrad();
				imargin[i] = findCentConfigInterior.getImargin();
				itermax[i] = findCentConfigInterior.getItermax();
			} else {
				nGauss[i] = findCentConfigPeripheral.getNgauss();	
				irad[i] = findCentConfigPeripheral.getIrad();
				imargin[i] = findCentConfigPeripheral.getImargin();
				itermax[i] = findCentConfigPeripheral.getItermax();
			}
		}
		
		// if isRefMap, then set all missingSpotFlags to use
		int[] passedMissingSpotFlags = missingSpotFlags;
		if (isRefMap) {
			passedMissingSpotFlags = new int[missingSpotFlags.length];
			for (int i=0; i<missingSpotFlags.length; i++) {
				passedMissingSpotFlags[i] = Constants.MISSING_SPOT_TYPE_USE;
			}
		}
		
		float[] x_cent = new float[arrayLen];
		float[] y_cent = new float[arrayLen];
		float[] intensity = new float[arrayLen];
		float[] peak = new float[arrayLen];
		int[] findCentStatus = new int[arrayLen];  // return status of each call to 

		Object[] result = jfindCentroids.jfindCentroids(retVal, frame, irad, imargin, x_guesses,
				y_guesses, itermax, nspotTypes, passedMissingSpotFlags, nGauss, x_cent, y_cent, intensity, peak, findCentStatus);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("No good centroid could be found");
		}

		
		FindCentroidsResult findCentroidsResult = new FindCentroidsResult(x_cent, y_cent, intensity, peak, findCentStatus);
		

		// Code for findCent unit testing
		// logger.info("fiCentroids:: ");
		//
		// logger.info("Irad::" + findCentConfig.getIrad() + "\n");
		// logger.info("Imargin::" + findCentConfig.getImargin()+ "\n");
		// logger.info("Itermax::" + findCentConfig.getItermax()+ "\n");
		// logger.info("Ngauss set to 0::\n" );
		//
		// Object[] tmpResult = jfindCentroids.jfindCentroids(retVal, frame, findCentConfig.getIrad(), findCentConfig.getImargin(),
		// x_guesses, y_guesses, findCentConfig.getItermax(), nspotType, 0, x_cent, y_cent, intensity);
		//
		// if (retVal.getCode() > 0) {
		// statusLogger.log(retVal);
		// throw new ComputationException("No good centroid could be found");
		// }
		//
		// List<FloatPoint> tempCentroids = FloatPointListEncoder.constructFromXandY(x_cent, y_cent);
		// logger.info("Xcentroids::\n" + FloatPointListEncoder.encodeXList(tempCentroids));
		// logger.info("Ycentroids::\n" + FloatPointListEncoder.encodeYList(tempCentroids));
		// logger.info("Intensity::\n");
		// StringBuffer buf = new StringBuffer();
		// for (int i=0;i<arrayLen; i++) {
		// buf.append(intensity[i] + ",");
		// }
		// buf.deleteCharAt(buf.length()-1);
		// logger.info(buf.toString());
		//
		//
		// logger.info("Ngauss set to 1::\n" );
		//
		// tmpResult = jfindCentroids.jfindCentroids(retVal, frame, findCentConfig.getIrad(), findCentConfig.getImargin(),
		// x_guesses, y_guesses, findCentConfig.getItermax(), nspotType, 1, x_cent, y_cent, intensity);
		//
		// if (retVal.getCode() > 0) {
		// statusLogger.log(retVal);
		// throw new ComputationException("No good centroid could be found");
		// }
		//
		//
		// tempCentroids = FloatPointListEncoder.constructFromXandY(x_cent, y_cent);
		// logger.info("Xcentroids::\n" + FloatPointListEncoder.encodeXList(tempCentroids));
		// logger.info("Ycentroids::\n" + FloatPointListEncoder.encodeYList(tempCentroids));
		// logger.info("Intensity::\n");
		//
		// buf.delete(0,buf.length());
		// for (int i=0;i<arrayLen; i++) {
		// buf.append(intensity[i] + ",");
		// }
		// buf.deleteCharAt(buf.length()-1);
		// logger.info(buf.toString());
		//

		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "findCentroids"));

		return findCentroidsResult;

	}

	public int[][] removeBadPixels(int[][] frame, List<Rect> badPixelList) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "removeBadPixels"));

		int[] x1 = new int[badPixelList.size()];
		int[] x2 = new int[badPixelList.size()];
		int[] y1 = new int[badPixelList.size()];
		int[] y2 = new int[badPixelList.size()];

		logger.debug("removeBadPixels:: ");
		int i = 0;
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

		logger.info(MessageGenerator.generateMessage("computation.success", "removeBadPixels"));

		return arrayOut;

	}

	
	public FIResult findAndIdentify(float[][] frame, int numSpots, FIConfig fiConfig, RefBeamMap currentRefMap, List<FloatPoint> refDefCentroids, int[] missingSpotFlags, boolean isRefMap)
			throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "findAndIdentify"));

		JfindAndIdentify jfindAndIdentify = new JfindAndIdentify();
		RetVal retVal = new RetVal();

		float[] fiParams = new float[6];

		int nsp = -1; // segment number of current group
		int ngp = -1; // sufs group number

		float[] x_ref_def = FloatPointListEncoder.extractXArray(refDefCentroids);
		float[] y_ref_def = FloatPointListEncoder.extractYArray(refDefCentroids);

		// if isRefMap, then set all missingSpotFlags to use
		int[] passedMissingSpotFlags = missingSpotFlags;
		if (isRefMap) {
			passedMissingSpotFlags = new int[missingSpotFlags.length];
			for (int i=0; i<missingSpotFlags.length; i++) {
				passedMissingSpotFlags[i] = Constants.MISSING_SPOT_TYPE_USE;
			}
		}
		
		// Force scale and rotation values, potentially coming from current ref map
		float forceScaleValue = (fiConfig.isForceScale() && fiConfig.getForceScaleSource() == FIConfig.FORCE_SOURCE_REF_MAP) ? currentRefMap
				.getCentroidMap().getScale() : fiConfig.getForceScaleValue();
		float forceRotationDeg = (fiConfig.isForceRotation() && fiConfig.getForceRotationSource() == FIConfig.FORCE_SOURCE_REF_MAP) ? currentRefMap
				.getCentroidMap().getRotation() : fiConfig.getForceRotationValue();

		float forceRotationRad = forceRotationDeg * (float) Constants.DEG2RAD;

		// the result object
		FIResult fiResult = new FIResult(numSpots, frame);

		Object output[] = jfindAndIdentify.jfindAndIdentify(retVal, frame, nsp, ngp, x_ref_def, y_ref_def, fiConfig.getuEst(),
				fiConfig.getuDelta0(), fiConfig.getMatchbox(), fiConfig.getnThresh0(), fiConfig.getnPeakMinThresh(),
				fiConfig.getnPeakMaxThresh(), fiConfig.isForceScale() ? 1 : 0, forceScaleValue, fiConfig.isForceRotation() ? 1 : 0,
				forceRotationRad, fiConfig.getMatchFineThresh(), fiConfig.getLensletOrientation(), fiConfig.getSpiralRingCount(),
				passedMissingSpotFlags, fiResult.getXiRst(), fiResult.getYiRst(), fiResult.getxPeak(), fiResult.getyPeak(), fiResult.getnDetect(),
				fiParams, fiResult.getN0123(), fiResult.getCcdBoxesAll(), fiResult.getCcdBoxesSha(), fiResult.getCcdBoxesNum());

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Unknown Find and Identify Error");
		}

		// store fi_param values
		fiResult.setFourierQuality(fiParams[0]);
		fiResult.setScale(fiParams[1]);
		fiResult.setRotation(fiParams[3]); // degrees
		fiResult.setTranslation(new FloatPoint(fiParams[4], fiParams[5]));

		// store scalars
		fiResult.setNumFilledBoxes((Integer) output[0]);
		fiResult.setFracFilledBoxes((Float) output[1]);
		fiResult.setnSolution((Integer) output[2]);
		
		

		// debug
		// logger.info("fiNew :: ");
		// logger.info("Xpeaks::\n" + FloatPointListEncoder.encodeXList(fiResult.getPeakLocationList()));
		// logger.info("Ypeaks::\n"+FloatPointListEncoder.encodeYList(fiResult.getPeakLocationList()));
		// logger.info("Xrst::\n"+FloatPointListEncoder.encodeXList(fiResult.getRstLocationList()));
		// logger.info("Yrst::\n"+FloatPointListEncoder.encodeYList(fiResult.getRstLocationList()));

		logger.info(MessageGenerator.generateMessage("computation.success", "findAndIdentify"));

		return fiResult;
	}

	public void evalFiResult(FIResult fiResult, FIConfig fiConfig, ProcedureConfig procedureConfig) throws UserAssistRequiredException,
			AbortProcedureException, HandMarkRequiredException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "evalFiResult"));

		// Need to Check this first
		UserAssistRequiredException userAssistException = new UserAssistRequiredException();
		//if (procedureConfig.getLightSource() == ProcedureConfig.LIGHT_SOURCE_LED && !fiResult.allDetectionsSinglePeaks()) {
		//	userAssistException.setNdetectNotAllSingle(true);
		//}
		if (fiResult.getFracFilledBoxes() < fiConfig.getFracFilledThresh()) {
			userAssistException.setFracThreshExceeded(true);
		
			if (procedureConfig.getPupilMaskType().isPupilMaskTypePt()) {
				userAssistException.setFracThreshExceededPT(true);
			}
		
		}

		if (fiResult.getFourierQuality() < fiConfig.getFourierQualityThresh()) {
			userAssistException.setFourierThreshExceeded(true);
		}
		
		if (fiResult.getnSolution() != 1) {
			userAssistException.setBadNSolution(true);
		}

		if (userAssistException.shouldThrow()) {
			throw userAssistException;
		}
		
		logger.info(MessageGenerator.generateMessage("computation.success", "evalFiResult"));

	}

	@Computation
	public CenterTelescopeCalcResult centerTelescopeCalc(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {

		logger.info(MessageGenerator.generateMessage("computation.start", "centerTelescopeCalc"));

		FloatPoint result = JavaComputations.pixLocationToDeltaArcSeconds(measuredPix, desiredPix, secPerPixel);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "centerTelescopeCalc"));
		
		return new CenterTelescopeCalcResult(result);
	}

	/*
	public FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {

		logger.info(MessageGenerator.generateMessage("computation.start", "pixLocationToDeltaArcSeconds"));

		FloatPoint result = JavaComputations.pixLocationToDeltaArcSeconds(measuredPix, desiredPix, secPerPixel);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "pixLocationToDeltaArcSeconds"));
		
		return result;
	}
	*/
	
	public float calcRms(float[][] data) {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calcRms"));

		float result =  JavaComputations.calcRms(data);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calcRms"));
		
		return result;
	}

	
	@Computation
	public CentroidOffsetsResult calculateCentroidOffsets(List<FloatPoint> centroids, List<FloatPoint> refMapCentroids,
			CentroidOffsetsConfig centroidOffsetsConfig, PupilMaskType pupilMaskType, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateCentroidOffsets"));

		JcalculateCentroidOffsets jcalculateCentroidOffsets = new JcalculateCentroidOffsets();
		RetVal retVal = new RetVal();

		float[] fiParams = new float[6];

		float[][] ref_cent = FloatPointListEncoder.convertToNby2Array(refMapCentroids);
		float[][] centroid = FloatPointListEncoder.convertToNby2Array(centroids);

		int numSpots = centroids.size();

		// spots that can be used (found without errors and should be used for analysis)
		int[] good_spots = 	goodCentroidsFound(missingSpotFlags, findCentStatusList);

		
		// output arrays
		float[][] ccdOffsets = new float[numSpots][2];
		float[][] cartesianOffsets = new float[numSpots][2];
		float[] image_translation = new float[2];

		Object output[] = jcalculateCentroidOffsets.jcalculateCentroidOffsets(retVal, centroid, ref_cent,
				centroidOffsetsConfig.isRemoveScale() ? 1 : 0, centroidOffsetsConfig.isRemoveRotation() ? 1 : 0, good_spots, nspotTypes, ccdOffsets,
				image_translation);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Centroid Offsets Calculation Error");
		}
		
		// convert to cartesian offsets
		for (int i=0; i<numSpots; i++) {
			cartesianOffsets[i][0] = ccdOffsets[i][0] * pupilMaskType.getCcdToCartesianPixelX();
			cartesianOffsets[i][1] = ccdOffsets[i][1] * pupilMaskType.getCcdToCartesianPixelY();
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateCentroidOffsets"));

		// store fi_param values
		return new CentroidOffsetsResult(ccdOffsets, cartesianOffsets, new FloatPoint(image_translation[0], image_translation[1]), (Float) output[0],
				(Float) output[1]);

	}

	@Computation
	public CentroidStatsResult calculateCentroidStats(FloatPoint[] centroidOffsets, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateCentroidStats"));

		JcalculateCentroidStats jcalculateCentroidStats = new JcalculateCentroidStats();
		RetVal retVal = new RetVal();

		float[][] offsets = FloatPointListEncoder.convertToNby2Array(Arrays.asList(centroidOffsets));

		// spots that can be used (found without errors and should be used for analysis)
		int[] good_spots = 	goodCentroidsFound(missingSpotFlags, findCentStatusList);

		Object output[] = jcalculateCentroidStats.jcalculateCentroidStats(retVal, offsets, good_spots, nspotTypes);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Centroid Offset Stats Calculation Error");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateCentroidStats"));

		// store fi_param values
		return new CentroidStatsResult((Integer) output[0], (Float) output[1], (Float) output[2], (Float) output[3], (Float) output[4]);

	}

	@Computation
	public PseudoTipTiltCentroidStatsResult calculatePseudoCentroidStats(FloatPoint[] centroidOffsetsPixels, int[] nspotTypes) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateCentroidStats"));

		JcalculateCentroidStats jcalculateCentroidStats = new JcalculateCentroidStats();
		RetVal retVal = new RetVal();

		
		float[][] offsets = FloatPointListEncoder.convertToNby2Array(Arrays.asList(centroidOffsetsPixels));

		// spots that can be used (found without errors and should be used for analysis)
		int [] good_spots = new int[nspotTypes.length];
		for (int i=0; i<nspotTypes.length; i++) {
			good_spots[i] = 1;
		}

		Object output[] = jcalculateCentroidStats.jcalculateCentroidStats(retVal, offsets, good_spots, nspotTypes);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Centroid Offset Stats Calculation Error");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateCentroidStats"));

		// store fi_param values
		return new PseudoTipTiltCentroidStatsResult((Integer) output[0], (Float) output[1], (Float) output[2], (Float) output[3], (Float) output[4]);

	}
	



	@Computation
	public PassiveTiltScaleErrorResult passiveTiltScaleErrorResult(FloatPoint[] centroidOffsets, List<FloatPoint> centerSpot) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "passiveTiltScaleError"));

		JpassiveTiltScaleError jpassiveTiltScaleError = new JpassiveTiltScaleError();
		RetVal retVal = new RetVal();

		float[][] offsets = FloatPointListEncoder.convertToNby2Array(Arrays.asList(centroidOffsets));

		float[] x_ref_def = FloatPointListEncoder.extractXArray(centerSpot);
		float[] y_ref_def = FloatPointListEncoder.extractYArray(centerSpot);

		Object output[] = jpassiveTiltScaleError.jpassiveTiltScaleError(retVal, offsets, x_ref_def, y_ref_def);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Passive Tilt Scale Error Calculation Error");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "passiveTiltScaleError"));

		// store fi_param values
		return new PassiveTiltScaleErrorResult((Float) output[0], (Float) output[1]);
	}
	
	
	@Computation
	public FineScreenScaleErrorResult fineScreenScaleErrorResult(FloatPoint[] centroidOffsets, List<FloatPoint> centerSpots, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "fineScreenScaleError"));

		JfineScreenScaleError jfineScreenScaleError = new JfineScreenScaleError();
		RetVal retVal = new RetVal();

		float[][] offsets = FloatPointListEncoder.convertToNby2Array(Arrays.asList(centroidOffsets));

		float[] x_ref_def = FloatPointListEncoder.extractXArray(centerSpots);
		float[] y_ref_def = FloatPointListEncoder.extractYArray(centerSpots);
		
		// spots that can be used (found without errors and should be used for analysis)
		int[] good_spots = 	goodCentroidsFound(missingSpotFlags, findCentStatusList);

		Object output[] = jfineScreenScaleError.jfineScreenScaleError(retVal, good_spots, offsets, x_ref_def, y_ref_def);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Fine Screen Scale Error Calculation Error");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "fineScreenScaleError"));

		// store fi_param values
		return new FineScreenScaleErrorResult((Float) output[0], (Float) output[1]);
	}

	
	public float[][] ttOffsetsToActs(List<FloatPoint> actuatorPositions, float imageScale, FloatPoint[] centroidOffsets)
			throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "ttOffsetsToActs"));

		JttOffsetsToActs jttOffsetsToActs = new JttOffsetsToActs();
		RetVal retVal = new RetVal();

		float[] x_act_pos = FloatPointListEncoder.extractXArray(actuatorPositions);
		float[] y_act_pos = FloatPointListEncoder.extractYArray(actuatorPositions);

		float[] x_offsets = FloatPointListEncoder.extractXArray(Arrays.asList(centroidOffsets));
		float[] y_offsets = FloatPointListEncoder.extractYArray(Arrays.asList(centroidOffsets));

		// output arrays
		float[] desired_act_deltas = new float[actuatorPositions.size()];
		float[] x_offsets_out = new float[centroidOffsets.length];
		float[] y_offsets_out = new float[centroidOffsets.length];

		Object output[] = jttOffsetsToActs.jttOffsetsToActs(retVal, x_act_pos, y_act_pos, imageScale, x_offsets, y_offsets, x_offsets_out,
				y_offsets_out, desired_act_deltas);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Tip/Tilt Offsets to Actuator Calculation Error");
		}

		// store fi_param values
		float[][] desiredActDeltas = new float[actuatorPositions.size()/3][3];
		for (int i = 0; i<actuatorPositions.size()/3; i++) {
			desiredActDeltas[i][0] = desired_act_deltas[i*3 + 0]; 
			desiredActDeltas[i][1] = desired_act_deltas[i*3 + 1]; 
			desiredActDeltas[i][2] = desired_act_deltas[i*3 + 2]; 
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "ttOffsetsToActs"));

		return desiredActDeltas;
	}

	@Computation
	public DecomposeActsResult decomposeActs(float[][] actuatorPositions) throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "decomposeActs"));

		JdecomposeActs jdecomposeActs = new JdecomposeActs();
		RetVal retVal = new RetVal();

		float[] actPos = flatten2dArray(actuatorPositions);

		// output arrays
		float[] act_tt = new float[actPos.length];
		float[] act_p = new float[actPos.length];


		Object output[] = jdecomposeActs.jdecomposeActs(retVal, actPos, act_tt, act_p);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Decompose Actuators Calculation Error");
		}

		// store _param values
		float[][] tipTiltActs = expandTo2dArray(act_tt, 3);
		float[][] pistonActs = expandTo2dArray(act_p, 3);

		logger.info(MessageGenerator.generateMessage("computation.success", "decomposeActs"));

		return new DecomposeActsResult(tipTiltActs, pistonActs);	
		
	}

	
	public float[][] optimalPistons(float[][] controlMatrix, float[][] tipTiltActs) throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "optimalPistons"));
		
		JoptimalPistons joptimalPistons = new JoptimalPistons();
		RetVal retVal = new RetVal();

		float[] ttActs = flatten2dArray(tipTiltActs);
		float[] testArray = new float[controlMatrix.length];

		// output arrays
		float[] act_p = new float[ttActs.length];


		Object output[] = joptimalPistons.joptimalPistons(retVal, controlMatrix, ttActs, testArray, 0, act_p);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Optimal Pistons Calculation Error");
		}

		// store _param values
		float[][] pistonActs = expandTo2dArray(act_p, 3);

		logger.info(MessageGenerator.generateMessage("computation.success", "optimalPistons"));

		return pistonActs;	

	}
	
	@Computation
	public PupilRegErrorResult calculatePupilRegError(PupilRegErrorConfig pupilRegErrorConfig, CentroidMap centroidMap, int numSpots,
			float[] peripheralSpotPerp, float[] peripheralSpotParallel, float[] peripheralSpotTheta, float aHex, float spotDiameter, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList)
					throws Exception {
		logger.info(MessageGenerator.generateMessage("computation.start", "calculatePupilRegError"));

		JcalculatePupilRegError jcalculatePupilRegError = new JcalculatePupilRegError();
		RetVal retVal = new RetVal();

		// spots that can be used (found without errors and should be used for analysis)
		int[] good_spots = 	goodCentroidsFound(missingSpotFlags, findCentStatusList);

		
		Object[] output = jcalculatePupilRegError.jcalculatePupilRegError(retVal, pupilRegErrorConfig.getFractionalIntensityCalcMethod(),
				centroidMap.getFindCentroidsResult().getIntensityList(), numSpots, pupilRegErrorConfig.getnStart(), good_spots, peripheralSpotTheta, 
				peripheralSpotPerp, peripheralSpotParallel, aHex, spotDiameter);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("calculate pupil reg error failed, status code = " + retVal.getCode());
		}
		
		PupilRegErrorResult pupilRegErrorResult = new PupilRegErrorResult((Float) output[0], (Float) output[1], (Float) output[2], 
				(Float) output[3], (Float) output[4], (Float) output[5], (Float) output[6]);
		
		
		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calculatePupilRegError"));

		return pupilRegErrorResult;

	}
	

	// private convenience methods
	private float[] flatten2dArray(float[][] input) {
		float[] result = new float[input.length * input[0].length];
		for (int i=0; i<input.length; i++) {
			for (int j=0; j<input[i].length; j++) {
				result[i * input[i].length + j] = input[i][j];
			}
		}
		return result;
	}
	
	private float[][] expandTo2dArray(float[] input, int minorIndexSize) {
		float[][] result = new float[input.length/minorIndexSize][minorIndexSize];
		for (int i = 0; i<input.length/minorIndexSize; i++) {
			for (int j=0; j < minorIndexSize; j++) {
				result[i][j] = input[i*minorIndexSize + j]; 
			}
		}
		return result;
	}
	
	private int[] goodCentroidsFound(int[] missingSpotFlags, int[] findCentStatusList) {
		int[] found = new int[findCentStatusList.length];

		for (int i=0; i<found.length; i++) {
			boolean isGood = (findCentStatusList[i] == Constants.FIND_CENT_STATUS_SUCCESS ||  findCentStatusList[i] == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_X ||  
					findCentStatusList[i] == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_Y) && missingSpotFlags[i] == Constants.MISSING_SPOT_TYPE_USE;
			found[i] = isGood ? 1 : 0;
		}

		return found;

	}

	
	public float[][] addMatricies(float[][] matrix1, float[][] matrix2) throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "addMatricies"));

		float[][] result = JavaComputations.addMatricies(matrix1, matrix2);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "addMatricies"));

		return result;
	}
	
	public void autoRefMapCheck(AutoRefMapConfig autoRefMapConfig, Point currentCoarsePosition, Point currentFinePosition, float temperature, 
			int numIterations, Date currentDate, RefBeamMap currentRefMap) throws ComputationException, AutoRefMapCheckException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "autoRefMapCheck"));
		
		JavaComputations.autoRefMapCheck(autoRefMapConfig, currentCoarsePosition, currentFinePosition, temperature,  
				numIterations, currentDate, currentRefMap);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "autoRefMapCheck"));
	}


	public AutoCenterTelCheckResult autoCenterTelescopeCheck(AutoCenterTelConfig autoCenterTelConfig, FloatPoint deltaAzEl, FloatPoint lastMove) {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "autoCenterTelescopeCheck"));
		
		AutoCenterTelCheckResult result = JavaComputations.autoCenterTelescopeCheck(autoCenterTelConfig, deltaAzEl, lastMove);

		logger.info(MessageGenerator.generateMessage("computation.success", "autoCenterTelescopeCheck"));
		
		return result;
	}

	
	public void checkSubimageIntensities(CentroidMap centroidMap, double threshold) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "checkSubimageIntensities"));

		JavaComputations.checkSubimageIntensities(centroidMap, threshold);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "checkSubimageIntensities"));
		
	}
	
	public float getMedianValue(float[] inputs) throws Exception {
		logger.info(MessageGenerator.generateMessage("computation.start", "getMedianValue"));
	
		float result = JavaComputations.getMedianValue(inputs);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "getMedianValue"));
		
		return result;
	}

	@Computation
	public CalcPrCommandsResult calcPrCommands(boolean centerPupil, int desiredCenterPupilMech, PupilRegErrorResult pupilRegErrorResult,
			PupilRegErrorConfig pupilRegErrorConfig, FineTiltMirror fineTiltMirror, CoarseTiltMirror coarseTiltMirror)
					throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calcCoarseMirrorCmds"));
		
		CalcPrCommandsResult result = JavaComputations.calcPrCommands(centerPupil, desiredCenterPupilMech, pupilRegErrorResult,
				pupilRegErrorConfig, fineTiltMirror, coarseTiltMirror);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calcCoarseMirrorCmds"));
		
		return result;
	}


	public Point calcCoarseMirrorCmds(FloatPoint desiredMotion, FloatPoint leverCoarse, float oraFactor) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calcCoarseMirrorCmds"));
		
		Point result = JavaComputations.calcCoarseMirrorCmds(desiredMotion, leverCoarse, oraFactor);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calcCoarseMirrorCmds"));
		
		return result;

	}

	@Computation
	public CalcM2M1Result calculateM2M1RayTrace(FindCentroidsResult findCentroidsResult, CentroidOffsetsResult centroidOffsetsResult, int[] subimagesForM2Calc,
			CalcM2M1Config calcM2M1Config, FloatPoint[][] fineScreenSpotCoords, int[] nspotTypes, TelescopeConstants telescopeConstants, float secPerPixel) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calculateM2M1RayTrace"));

		JcalculateM2M1RayTrace jcalculateM2M1RayTrace = new JcalculateM2M1RayTrace();
		RetVal retVal = new RetVal();

		// logger.debug("findCent::  " + guess + ", value = " + frame[(int)guess.x][(int)guess.y]);

		// add one to each guess to acccount for fortran indicies starting at 1, not zero.

		// TODO: check if we want cartesian vs ccd coordinates/is the conversion correct?
		
		List<FloatPoint> centroidOffsetsPixels = Arrays.asList(centroidOffsetsResult.getCartesianInteriorCentroidOffsets(nspotTypes));
		
		// convert offsets from pixels to arcsec
		List<FloatPoint> centroidOffsetsArcsecs = new ArrayList<FloatPoint>();
		for (FloatPoint pixelOffset : centroidOffsetsPixels) {
		
			FloatPoint arcsecOffset = new FloatPoint(pixelOffset.x * secPerPixel, pixelOffset.y * secPerPixel);
			centroidOffsetsArcsecs.add(arcsecOffset);
		}
		
		float[] offsetsX = FloatPointListEncoder.extractXArray(centroidOffsetsArcsecs);
		float[] offsetsY = FloatPointListEncoder.extractYArray(centroidOffsetsArcsecs);
		
		int[] validSubimages = findCentroidsResult.getFoundInteriorSubimageFlags(nspotTypes);

		float[][] xLensletLocations = FloatPointListEncoder.extractXfrom2dFloatPoint(fineScreenSpotCoords);
		float[][] yLensletLocations = FloatPointListEncoder.extractYfrom2dFloatPoint(fineScreenSpotCoords);
		
		float[] m2TipTiltArr = new float[2];
		
		float[] m1OffsetsCorrectedForM2X = new float[telescopeConstants.getNumberOfSegments()];
		float[] m1OffsetsCorrectedForM2Y = new float[telescopeConstants.getNumberOfSegments()];
		
		Object[] result = jcalculateM2M1RayTrace.jcalculateM2M1RayTrace(retVal, offsetsX, offsetsY, validSubimages, subimagesForM2Calc, 
				calcM2M1Config.getM2PistonUnitPertibation(), calcM2M1Config.getM2TTUnitPertibation(), xLensletLocations, yLensletLocations, 
				telescopeConstants.getBackFocalDistance(), telescopeConstants.getM1FocalLength(), telescopeConstants.getTelescopeFocalLength(), 
				telescopeConstants.getM1CurvatureRadius(), m2TipTiltArr, m1OffsetsCorrectedForM2X, m1OffsetsCorrectedForM2Y);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("M2 ray trace calcuation error");
		}

		
		float m2Piston = (Float)result[0];
		float centroidResidual = (Float)result[1];
		float pistonErrorMultiplier = (Float)result[2];
		FloatPoint tipTiltErrorMulitplier = new FloatPoint((Float)result[3], (Float)result[4]);
		FloatPoint m2TipTilt = new FloatPoint(m2TipTiltArr[0], m2TipTiltArr[1]);
		List<FloatPoint> m1OffsetsCorrectedForM2 = FloatPointListEncoder.constructFromXandY(m1OffsetsCorrectedForM2X, m1OffsetsCorrectedForM2Y);
		
		// convert corrected offsets from arcsec to pixels
		List<FloatPoint> m1OffsetsCorrectedForM2Pixels = new ArrayList<FloatPoint>();
		for (FloatPoint arcsecOffset : m1OffsetsCorrectedForM2) {
		
			FloatPoint pixelOffset = new FloatPoint(arcsecOffset.x / secPerPixel, arcsecOffset.y / secPerPixel);
			m1OffsetsCorrectedForM2Pixels.add(pixelOffset);
		}
		
		CalcM2M1Result calcM2M1Result = new CalcM2M1Result(m2Piston, m2TipTilt, centroidResidual, pistonErrorMultiplier, tipTiltErrorMulitplier,
				 m1OffsetsCorrectedForM2.toArray(new FloatPoint[0]), m1OffsetsCorrectedForM2Pixels.toArray(new FloatPoint[0]));


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calculateM2M1RayTrace"));

		return calcM2M1Result;
	
	}

	@Computation
	public CalcDesiredActCommandsResult calcDesiredActCommands(float[][] controlMatrix, float[][] tipTiltActs) throws Exception {
		
		// Calculate the optimal pistons associated with the calculated
		// actuators (minimizes the changes to the edges). Note that this
		// routine just determines the optimal pistons; if you want to add
		// these on to the tip/tilt pistons, you need to do it yourself.
		float[][] pistonActs = optimalPistons(controlMatrix, tipTiltActs);

		// calculate RMS of the actuator cmds
		float pistonActsRms = calcRms(pistonActs);

		
		// combine tip/tilt and piston commands
		/*****************************************************/
		/*               calcDesiredActCommands              */
		/*****************************************************/
		float[][] desiredActDeltas = addMatricies(tipTiltActs, pistonActs);

		// calculate RMS of the actuator cmds
		float desiredActDeltasRms = calcRms(desiredActDeltas);
		
		return new CalcDesiredActCommandsResult(pistonActs, pistonActsRms, desiredActDeltas, desiredActDeltasRms);
	}

	@Computation
	public CalcM2ActuatorsFromPttResult calcM2ActuatorsFromPtt(float meanM2PistonError, FloatPoint meanM2TipTiltError,
			float m2ActuatorRadius, float m2TtCorrectionFactor) throws Exception {

		logger.info(MessageGenerator.generateMessage("computation.start", "calcM2ActuatorsFromPtt"));

		Jm2ActuatorsFromPtt jm2ActuatorsFromPtt = new Jm2ActuatorsFromPtt();
		RetVal retVal = new RetVal();

		
		float[] m2ActDeltas = new float[3];
		
		Object[] result = jm2ActuatorsFromPtt.jm2ActuatorsFromPtt(retVal, meanM2TipTiltError.x, meanM2TipTiltError.y, meanM2PistonError * 1000.0f, 
				m2ActuatorRadius, m2TtCorrectionFactor, m2ActDeltas);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("calcM2ActuatorsFromPtt calcuation error");
		}

				
		CalcM2ActuatorsFromPttResult calcM2ActuatorsFromPttResult = new CalcM2ActuatorsFromPttResult(m2ActDeltas);


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calcM2ActuatorsFromPtt"));

		return calcM2ActuatorsFromPttResult;

	
	}

	@Computation
	public CalcDesiredActDeltasRmsStdResult calcDesiredActDeltasRmsStd(Float[] desiredActDeltaRmsIterations) {

		logger.info(MessageGenerator.generateMessage("computation.start", "calcDesiredActDeltasRmsStd"));

		float desiredActDeltasRmsStd = JavaComputations.getStd(desiredActDeltaRmsIterations);

		logger.info(MessageGenerator.generateMessage("computation.success", "calcDesiredActDeltasRmsStd"));

		return new CalcDesiredActDeltasRmsStdResult(desiredActDeltasRmsStd);
	}

	@Computation
	public CalcM2PttErrorsMeanStdResult calcM2PttErrorsMeanStd(Float[] m2PistonErrors, FloatPoint[] m2TipTiltErrors) {


		logger.info(MessageGenerator.generateMessage("computation.start", "calcM2PttErrorsMeanStd"));

		float meanM2PistonError = JavaComputations.getMean(m2PistonErrors);
		FloatPoint meanM2TipTiltError = JavaComputations.getMean(m2TipTiltErrors);
		
		float stdM2PistonError = JavaComputations.getStd(m2PistonErrors);
		FloatPoint stdM2TipTiltError = JavaComputations.getStd(m2TipTiltErrors);

		logger.info(MessageGenerator.generateMessage("computation.success", "calcM2PttErrorsMeanStd"));

		return new CalcM2PttErrorsMeanStdResult(meanM2PistonError, meanM2TipTiltError, stdM2PistonError, stdM2TipTiltError);
		
		
	}

	@Computation
	public CalcSegmentMeanTipTiltsResult calcSegmentMeanTipTilts(FloatPoint[][] m1SegmentTipTiltErrors) {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calcM2PttErrorsMeanStd"));
		// transpose array for easier mean calculating
		FloatPoint[][] transposedArray = JavaComputations.transpose2dArray(m1SegmentTipTiltErrors);
		
		// loop over all segments
		FloatPoint[] segmentMeanTipTiltErrors = new FloatPoint[transposedArray.length];
		for (int i=0; i<transposedArray.length; i++) {
			segmentMeanTipTiltErrors[i] = JavaComputations.getMean(transposedArray[i]);	
		}
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calcM2PttErrorsMeanStd"));
		
		return new CalcSegmentMeanTipTiltsResult(segmentMeanTipTiltErrors);
	}

	
	
}


