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
import org.tmt.aps.peas.computation.model.AvgCentroidStatsResult;
import org.tmt.aps.peas.computation.model.BbAnalyzeFrameResult;
import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActDeltasRmsEomResult;
import org.tmt.aps.peas.computation.model.CalcM2ActuatorsFromPttResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CalcM2PttErrorsMeanEomResult;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CalcSegmentMeanTipTiltsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.ColorStepResult;
import org.tmt.aps.peas.computation.model.ColorStepToActuatorsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.computation.model.FindCentResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.FixPistonsResult;
import org.tmt.aps.peas.computation.model.MakeTemplateResult;
import org.tmt.aps.peas.computation.model.PhasingStatsResult;
import org.tmt.aps.peas.computation.model.PseudoTipTiltCentroidStatsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.computation.model.Subimage;
import org.tmt.aps.peas.computation.model.SufsCentroidStatsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentCentroidsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentOffsetsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeStatsResult;
import org.tmt.aps.peas.computation.model.SufsZernikeResult;
import org.tmt.aps.peas.computation.model.SufsZernikeStatsResult;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.AutoRefMapConfig;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfig;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.PupilRegErrorConfig;
import org.tmt.aps.peas.config.model.TelescopeConstants;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.lang.interop.JbbAnalyzeFrame;
import org.tmt.aps.peas.lang.interop.JbbAnalyzeSequence;
import org.tmt.aps.peas.lang.interop.JcalculateCentroidOffsets;
import org.tmt.aps.peas.lang.interop.JcalculateCentroidStats;
import org.tmt.aps.peas.lang.interop.JcalculateFocusModeVector;
import org.tmt.aps.peas.lang.interop.JcalculateM2M1Analytical;
import org.tmt.aps.peas.lang.interop.JcalculateM2M1RayTrace;
import org.tmt.aps.peas.lang.interop.JcalculatePupilRegError;
import org.tmt.aps.peas.lang.interop.JcolorStep;
import org.tmt.aps.peas.lang.interop.JcolorStepToActuators;
import org.tmt.aps.peas.lang.interop.JdecomposeActs;
import org.tmt.aps.peas.lang.interop.JfindAndIdentify;
import org.tmt.aps.peas.lang.interop.JfindCent;
import org.tmt.aps.peas.lang.interop.JfindCentroids;
import org.tmt.aps.peas.lang.interop.JfixPistons;
import org.tmt.aps.peas.lang.interop.Jm2ActuatorsFromPtt;
import org.tmt.aps.peas.lang.interop.JmakeTemplate;
import org.tmt.aps.peas.lang.interop.JoptimalPistons;
import org.tmt.aps.peas.lang.interop.JremoveBadPixels;
import org.tmt.aps.peas.lang.interop.JsufsOffsetsToZernikes;
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

	
	private static final int NUM_SUFS_SEGMENT_SPOTS = 127;



	
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
			throw new ComputationException("No good centroid could be found. " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		FloatPoint centroid = new FloatPoint((Float) result[0], (Float) result[1]);
		
		Subimage subimage = new Subimage(centroid, (Float)result[2], (Float)result[3], 0);
		
		FindCentResult findCentResult = new FindCentResult(guess, subimage);

		logger.info(MessageGenerator.generateMessage("computation.success", "findCent"));

		return findCentResult;
	}


	// findCentStatus is also a property of a spot, to be used by calcs after this.
	@Computation
	public FindCentroidsResult findCentroids(float[][] frame, FIResult fiResult, FindCentConfig findCentConfigInterior,  FindCentConfig findCentConfigPeripheral, int[] nspotTypes, int[] missingSpotFlags, boolean findAllMaskSpots) throws ComputationException {

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
		if (findAllMaskSpots) {
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
			throw new ComputationException("No good centroid could be found.  "  + MessageGenerator.generateErrorMessage(retVal) + ".  ");
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
			throw new ComputationException("Bad Pixel remove error. "  + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "removeBadPixels"));

		return arrayOut;

	}

	
	public FIResult findAndIdentify(float[][] frame, int numSpots, FIConfig fiConfig, RefBeamMap currentRefMap, List<FloatPoint> refDefCentroids, int[] missingSpotFlags, boolean findAllMaskSpots)
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
		if (findAllMaskSpots) {
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
		
		logger.info("jfindAndIdentify inputs:  nsp = " + nsp + ", ngp = " + ngp + ", x_ref_def = " + x_ref_def + ", y_ref_def = " + y_ref_def +
				", fiConfig = " + fiConfig + ", forceRotationDeg = " + forceRotationDeg + ", forceScaleValue = " + forceScaleValue + 
				", passedMissingSpotFlags = " + passedMissingSpotFlags);

		Object output[] = jfindAndIdentify.jfindAndIdentify(retVal, frame, nsp, ngp, x_ref_def, y_ref_def, fiConfig.getuEst(),
				fiConfig.getuDelta0(), fiConfig.getMatchbox(), fiConfig.getnThresh0(), fiConfig.getnPeakMinThresh(),
				fiConfig.getnPeakMaxThresh(), fiConfig.isForceScale() ? 1 : 0, forceScaleValue, fiConfig.isForceRotation() ? 1 : 0,
				forceRotationRad, fiConfig.getMatchFineThresh(), fiConfig.getLensletOrientation(), fiConfig.getSpiralRingCount(),
				passedMissingSpotFlags, fiResult.getXiRst(), fiResult.getYiRst(), fiResult.getxPeak(), fiResult.getyPeak(), fiResult.getnDetect(),
				fiParams, fiResult.getN0123(), fiResult.getCcdBoxesAll(), fiResult.getCcdBoxesSha(), fiResult.getCcdBoxesNum());

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Unknown Find and Identify Error. "  + MessageGenerator.generateErrorMessage(retVal) + ".  ");
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
	public CentroidOffsetsResult calculateCentroidOffsets(FloatPoint[] centroids, FloatPoint[] refMapCentroids,
			CentroidOffsetsConfig centroidOffsetsConfig, PupilMaskType pupilMaskType, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws ComputationException {

		return calcCentroidOffsets(centroids, refMapCentroids,
				centroidOffsetsConfig, pupilMaskType, nspotTypes, missingSpotFlags, findCentStatusList);
		
	}
	
	private CentroidOffsetsResult calcCentroidOffsets(FloatPoint[] centroids, FloatPoint[] refMapCentroids,
			CentroidOffsetsConfig centroidOffsetsConfig, PupilMaskType pupilMaskType, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws ComputationException {
		logger.info(MessageGenerator.generateMessage("computation.start", "calculateCentroidOffsets"));

		JcalculateCentroidOffsets jcalculateCentroidOffsets = new JcalculateCentroidOffsets();
		RetVal retVal = new RetVal();

		float[] fiParams = new float[6];

		float[][] ref_cent = FloatPointListEncoder.convertToNby2Array(Arrays.asList(refMapCentroids));
		float[][] centroid = FloatPointListEncoder.convertToNby2Array(Arrays.asList(centroids));

		int numSpots = centroids.length;

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
			throw new ComputationException("Centroid Offsets Calculation Error. "  + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}
		
		// convert to cartesian offsets
		for (int i=0; i<numSpots; i++) {
			cartesianOffsets[i][0] = ccdOffsets[i][0] * pupilMaskType.getCcdToCartesianPixelX();
			cartesianOffsets[i][1] = ccdOffsets[i][1] * pupilMaskType.getCcdToCartesianPixelY();
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateCentroidOffsets"));

		// store fi_param values
		return new CentroidOffsetsResult(ccdOffsets, cartesianOffsets, new FloatPoint(image_translation[0], image_translation[1]), (Float) output[0],
				(Float) output[1], good_spots);

	}

	@Computation
	public CentroidStatsResult calculateCentroidStats(FloatPoint[] centroidOffsets, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws ComputationException {
		return calcCentroidStats(centroidOffsets, nspotTypes, missingSpotFlags, findCentStatusList);
	}
	
	// version with missingSpotFlags and findCentStatusLists passed in
	private CentroidStatsResult calcCentroidStats(FloatPoint[] centroidOffsets, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws ComputationException {


		// spots that can be used (found without errors and should be used for analysis)
		int[] goodSpots = goodCentroidsFound(missingSpotFlags, findCentStatusList);
		
		return calcCentroidStats(centroidOffsets, nspotTypes, goodSpots);

	}

	// version with goodSpots (or validOffsets) passed in
	private CentroidStatsResult calcCentroidStats(FloatPoint[] centroidOffsets, int[] nspotTypes, int[] goodSpots) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateCentroidStats"));

		JcalculateCentroidStats jcalculateCentroidStats = new JcalculateCentroidStats();
		RetVal retVal = new RetVal();

		float[][] offsets = FloatPointListEncoder.convertToNby2Array(Arrays.asList(centroidOffsets));

		Object output[] = jcalculateCentroidStats.jcalculateCentroidStats(retVal, offsets, goodSpots, nspotTypes);

		logger.info("Fortran call completed");
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Centroid Offset Stats Calculation Error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
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
			throw new ComputationException("Centroid Offset Stats Calculation Error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateCentroidStats"));

		// store fi_param values
		return new PseudoTipTiltCentroidStatsResult((Integer) output[0], (Float) output[1], (Float) output[2], (Float) output[3], (Float) output[4]);

	}
	
	@Computation
	public AvgCentroidStatsResult calculateAvgCentroidStats(FloatPoint[] avgCentroidOffsets, int[] nspotTypes, int[] missingSpotFlags, int[] goodSpots) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "calcAvgCentroidStats"));

		JcalculateCentroidStats jcalculateCentroidStats = new JcalculateCentroidStats();
		RetVal retVal = new RetVal();

		
		float[][] offsets = FloatPointListEncoder.convertToNby2Array(Arrays.asList(avgCentroidOffsets));


		Object output[] = jcalculateCentroidStats.jcalculateCentroidStats(retVal, offsets, goodSpots, nspotTypes);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Centroid Offset Stats Calculation Error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "calcAvgCentroidStats"));

		// store fi_param values
		return new AvgCentroidStatsResult((Integer) output[0], (Float) output[1], (Float) output[2], (Float) output[3], (Float) output[4]);

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
			throw new ComputationException("Tip/Tilt Offsets to Actuator Calculation Error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
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

		float[] actPos = JavaComputations.flatten2dArray(actuatorPositions, 1);

		// output arrays
		float[] act_tt = new float[actPos.length];
		float[] act_p = new float[actPos.length];


		Object output[] = jdecomposeActs.jdecomposeActs(retVal, actPos, act_tt, act_p);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Decompose Actuators Calculation Error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		// store _param values
		float[][] tipTiltActs = JavaComputations.expandTo2dArray(act_tt, 3);
		float[][] pistonActs = JavaComputations.expandTo2dArray(act_p, 3);

		logger.info(MessageGenerator.generateMessage("computation.success", "decomposeActs"));

		return new DecomposeActsResult(tipTiltActs, pistonActs);	
		
	}

	
	public float[][] optimalPistons(float[][] controlMatrix, float[][] tipTiltActs) throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "optimalPistons"));
		
		JoptimalPistons joptimalPistons = new JoptimalPistons();
		RetVal retVal = new RetVal();

		float[] ttActs = JavaComputations.flatten2dArray(tipTiltActs, 1);
		float[] testArray = new float[controlMatrix.length];

		// output arrays
		float[] act_p = new float[ttActs.length];


		Object output[] = joptimalPistons.joptimalPistons(retVal, controlMatrix, ttActs, testArray, 0, act_p);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Optimal Pistons Calculation Error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		// store _param values
		float[][] pistonActs = JavaComputations.expandTo2dArray(act_p, 3);

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
			throw new ComputationException("calculate pupil reg error failed, status code = " + retVal.getCode() + ".  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}
		
		PupilRegErrorResult pupilRegErrorResult = new PupilRegErrorResult((Float) output[0], (Float) output[1], (Float) output[2], 
				(Float) output[3], (Float) output[4], (Float) output[5], (Float) output[6]);
		
		
		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calculatePupilRegError"));

		return pupilRegErrorResult;

	}
	


	public int[] goodCentroidsFound(int[] missingSpotFlags, int[] findCentStatusList) {
		int[] found = new int[findCentStatusList.length];

		for (int i=0; i<found.length; i++) {
			boolean isGood = (findCentStatusList[i] == Constants.FIND_CENT_STATUS_SUCCESS ||  findCentStatusList[i] == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_X ||  
					findCentStatusList[i] == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_Y) && missingSpotFlags[i] == Constants.MISSING_SPOT_TYPE_USE;
			found[i] = isGood ? 1 : 0;
		}

		return found;

	}
	
	public int[] goodOffsetsFound(int[] goodSpots, int[] jumpedSpots) {
		int[] validOffsets = new int[goodSpots.length];
		
		for (int i=0; i<goodSpots.length; i++) {
			int notJumped = (jumpedSpots[i] == 0) ? 1 : 0;
			validOffsets[i] = goodSpots[i] & notJumped;
		}
		
		return validOffsets;
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
			float m2PistonUnitPertibation, float m2TTUnitPertibation, FloatPoint[][] fineScreenSpotCoords, int[] nspotTypes, TelescopeConstants telescopeConstants, float secPerPixel,
			float m2TtCorrectionFactor, PupilMaskType pupilMaskType) throws Exception {
		
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
		float[] m2TipTiltArrTelescopeCoords = new float[2];
		
		float[] m1OffsetsCorrectedForM2X = new float[telescopeConstants.getNumberOfSegments()];
		float[] m1OffsetsCorrectedForM2Y = new float[telescopeConstants.getNumberOfSegments()];
		
		Object[] result = jcalculateM2M1RayTrace.jcalculateM2M1RayTrace(retVal, offsetsX, offsetsY, validSubimages, subimagesForM2Calc, 
				m2PistonUnitPertibation, m2TTUnitPertibation, xLensletLocations, yLensletLocations, 
				telescopeConstants.getBackFocalDistance(), telescopeConstants.getM1FocalLength(), telescopeConstants.getTelescopeFocalLength(), 
				telescopeConstants.getM1CurvatureRadius(), m2TtCorrectionFactor, m2TipTiltArr, m2TipTiltArrTelescopeCoords, m1OffsetsCorrectedForM2X, m1OffsetsCorrectedForM2Y);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("M2 ray trace calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		
		float m2Piston = (Float)result[0];
		float centroidResidual = (Float)result[1];
		float pistonErrorMultiplier = (Float)result[2];
		FloatPoint tipTiltErrorMulitplier = new FloatPoint((Float)result[3], (Float)result[4]);
		FloatPoint m2TipTilt = new FloatPoint(m2TipTiltArr[0], m2TipTiltArr[1]);
		FloatPoint m2TipTiltTelescopeCoords = new FloatPoint(m2TipTiltArrTelescopeCoords[0], m2TipTiltArrTelescopeCoords[1]);
		List<FloatPoint> m1OffsetsCorrectedForM2 = FloatPointListEncoder.constructFromXandY(m1OffsetsCorrectedForM2X, m1OffsetsCorrectedForM2Y);
		
		// convert corrected offsets from arcsec to pixels
		List<FloatPoint> m1OffsetsCorrectedForM2Pixels = new ArrayList<FloatPoint>();
		for (FloatPoint arcsecOffset : m1OffsetsCorrectedForM2) {
		
			FloatPoint pixelOffset = new FloatPoint(arcsecOffset.x / secPerPixel, arcsecOffset.y / secPerPixel);
			m1OffsetsCorrectedForM2Pixels.add(pixelOffset);
		}
		
		FloatPoint[] m1OffsetsPixelsCartesian = m1OffsetsCorrectedForM2Pixels.toArray(new FloatPoint[0]);
		FloatPoint[] m1OffsetsPixelsCcd = new FloatPoint[m1OffsetsPixelsCartesian.length];
		
		// convert cartesian to ccd offsets
		for (int i=0; i<m1OffsetsPixelsCartesian.length; i++) {
			m1OffsetsPixelsCcd[i] = new FloatPoint(m1OffsetsPixelsCartesian[i].x / pupilMaskType.getCcdToCartesianPixelX(), m1OffsetsPixelsCartesian[i].y / pupilMaskType.getCcdToCartesianPixelY());
		}

				
		CalcM2M1Result calcM2M1Result = new CalcM2M1Result(m2Piston, m2TipTilt, m2TipTiltTelescopeCoords, centroidResidual, pistonErrorMultiplier, tipTiltErrorMulitplier,
				 m1OffsetsCorrectedForM2.toArray(new FloatPoint[0]), m1OffsetsPixelsCartesian, m1OffsetsPixelsCcd);


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calculateM2M1RayTrace"));

		return calcM2M1Result;
	
	}
	
	@Computation
	public CalcM2M1Result calculateM2M1Analytical(FindCentroidsResult findCentroidsResult, CentroidOffsetsResult centroidOffsetsResult, int[] subimagesForM2Calc,
			FloatPoint[][] fineScreenSpotCoords, int[] nspotTypes, TelescopeConstants telescopeConstants, float secPerPixel, float m2TtCorrectionFactor,
			float aHex, int startSegNum, int endSegNum, PupilMaskType pupilMaskType) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calculateM2M1Analytical"));

		JcalculateM2M1Analytical jcalculateM2M1Analytical = new JcalculateM2M1Analytical();
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
		float[] m2TipTiltArrTelescopeCoords = new float[2];
		
		float[] m1OffsetsCorrectedForM2X = new float[telescopeConstants.getNumberOfSegments()];
		float[] m1OffsetsCorrectedForM2Y = new float[telescopeConstants.getNumberOfSegments()];
		
		Object[] result = jcalculateM2M1Analytical.jcalculateM2M1Analytical(retVal, offsetsX, offsetsY, validSubimages, subimagesForM2Calc, 
				xLensletLocations, yLensletLocations, 
				telescopeConstants.getBackFocalDistance(), telescopeConstants.getM1FocalLength(), telescopeConstants.getTelescopeFocalLength(), 
				m2TtCorrectionFactor, telescopeConstants.getM1OuterDiameter(), aHex, startSegNum, endSegNum, 
				m2TipTiltArr, m2TipTiltArrTelescopeCoords, m1OffsetsCorrectedForM2X, m1OffsetsCorrectedForM2Y);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("calculateM2M1Analytical calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		
		float m2Piston = (Float)result[0];
		float centroidResidual = (Float)result[1];
		
		
		FloatPoint m2TipTilt = new FloatPoint(m2TipTiltArr[0], m2TipTiltArr[1]);
		FloatPoint m2TipTiltTelescopeCoords = new FloatPoint(m2TipTiltArrTelescopeCoords[0], m2TipTiltArrTelescopeCoords[1]);
		List<FloatPoint> m1OffsetsCorrectedForM2 = FloatPointListEncoder.constructFromXandY(m1OffsetsCorrectedForM2X, m1OffsetsCorrectedForM2Y);
		
		// convert corrected offsets from arcsec to pixels
		List<FloatPoint> m1OffsetsCorrectedForM2Pixels = new ArrayList<FloatPoint>();
		for (FloatPoint arcsecOffset : m1OffsetsCorrectedForM2) {
		
			FloatPoint pixelOffset = new FloatPoint(arcsecOffset.x / secPerPixel, arcsecOffset.y / secPerPixel);
			m1OffsetsCorrectedForM2Pixels.add(pixelOffset);
		}
				
		FloatPoint[] m1OffsetsPixelsCartesian = m1OffsetsCorrectedForM2Pixels.toArray(new FloatPoint[0]);
		FloatPoint[] m1OffsetsPixelsCcd = new FloatPoint[m1OffsetsPixelsCartesian.length];
		
		// convert cartesian to ccd offsets
		for (int i=0; i<m1OffsetsPixelsCartesian.length; i++) {
			m1OffsetsPixelsCcd[i] = new FloatPoint(m1OffsetsPixelsCartesian[i].x / pupilMaskType.getCcdToCartesianPixelX(), m1OffsetsPixelsCartesian[i].y / pupilMaskType.getCcdToCartesianPixelY());
		}


		
		CalcM2M1Result calcM2M1Result = new CalcM2M1Result(m2Piston, m2TipTilt, m2TipTiltTelescopeCoords, centroidResidual,
				 m1OffsetsCorrectedForM2.toArray(new FloatPoint[0]), m1OffsetsPixelsCartesian, m1OffsetsPixelsCcd);


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calculateM2M1Analytical"));

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
		
		
		// TODO: add calc for focus mode and non-focus mode components
		float[] focusModeVector = calculateFocusModeVector(controlMatrix);
		
		// TODO: Calculate dot-product
		// RMS of the focus mode component of the actuator commands
		float[] desiredActDeltasFlattened= JavaComputations.flatten2dArray(desiredActDeltas, 1);
		float desiredActDeltasFmRms = JavaComputations.getDotProdRms(desiredActDeltasFlattened, focusModeVector);
		
		float desiredActDeltasNoFmRms = (float)Math.sqrt(desiredActDeltasRms * desiredActDeltasRms - desiredActDeltasFmRms * desiredActDeltasFmRms);
		
		return new CalcDesiredActCommandsResult(pistonActs, pistonActsRms, desiredActDeltas, desiredActDeltasRms, desiredActDeltasFmRms, desiredActDeltasNoFmRms);
	}


	 
	
	public float[] calculateFocusModeVector(float[][] controlMatrix) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calculateFocusModeVector"));

		
		float[] focusModeVector = new float[controlMatrix[0].length];
		
		
		JcalculateFocusModeVector jcalculateFocusModeVector = new JcalculateFocusModeVector();
		RetVal retVal = new RetVal();

		
		
		Object[] result = jcalculateFocusModeVector.jcalculateFocusModeVector(retVal, controlMatrix, focusModeVector);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("calculateFocusModeVector error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}
		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calculateFocusModeVector"));

		return focusModeVector;
	
	}

	
	
	@Computation
	public CalcM2ActuatorsFromPttResult calcM2ActuatorsFromPtt(float meanM2PistonError, FloatPoint meanM2TipTiltError,
			float m2ActuatorRadius) throws Exception {

		logger.info(MessageGenerator.generateMessage("computation.start", "calcM2ActuatorsFromPtt"));

		Jm2ActuatorsFromPtt jm2ActuatorsFromPtt = new Jm2ActuatorsFromPtt();
		RetVal retVal = new RetVal();

		
		float[] m2ActDeltas = new float[3];
		
		Object[] result = jm2ActuatorsFromPtt.jm2ActuatorsFromPtt(retVal, meanM2TipTiltError.x, meanM2TipTiltError.y, meanM2PistonError * 1000.0f, 
				m2ActuatorRadius * 1000.0f, m2ActDeltas);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("calcM2ActuatorsFromPtt calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

				
		CalcM2ActuatorsFromPttResult calcM2ActuatorsFromPttResult = new CalcM2ActuatorsFromPttResult(m2ActDeltas);


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calcM2ActuatorsFromPtt"));

		return calcM2ActuatorsFromPttResult;

	
	}

	@Computation
	public CalcDesiredActDeltasRmsEomResult calcDesiredActDeltasRmsEom(Float[] desiredActDeltaRmsIterations, Float[] desiredActDeltaFmRmsIterations, 
			Float[] desiredActDeltaNoFmRmsIterations) {

		logger.info(MessageGenerator.generateMessage("computation.start", "calcDesiredActDeltasRmsEom"));

		float desiredActDeltasRmsEom = JavaComputations.getEom(desiredActDeltaRmsIterations);
		float desiredActDeltasFmRmsEom = JavaComputations.getEom(desiredActDeltaFmRmsIterations);
		float desiredActDeltasNoFmRmsEom = JavaComputations.getEom(desiredActDeltaNoFmRmsIterations);

		logger.info(MessageGenerator.generateMessage("computation.success", "calcDesiredActDeltasRmsEom"));

		return new CalcDesiredActDeltasRmsEomResult(desiredActDeltasRmsEom, desiredActDeltasFmRmsEom, desiredActDeltasNoFmRmsEom);
	}

	@Computation
	public CalcM2PttErrorsMeanEomResult calcM2PttErrorsMeanEom(Float[] m2PistonErrors, FloatPoint[] m2TipTiltErrors) {


		logger.info(MessageGenerator.generateMessage("computation.start", "calcM2PttErrorsMeanEom"));

		float meanM2PistonError = JavaComputations.getMean(m2PistonErrors);
		FloatPoint meanM2TipTiltError = JavaComputations.getMean(m2TipTiltErrors);
		
		float eomM2PistonError = JavaComputations.getEom(m2PistonErrors);
		FloatPoint eomM2TipTiltError = JavaComputations.getEom(m2TipTiltErrors);

		logger.info(MessageGenerator.generateMessage("computation.success", "calcM2PttErrorsMeanEom"));

		return new CalcM2PttErrorsMeanEomResult(meanM2PistonError, meanM2TipTiltError, eomM2PistonError, eomM2TipTiltError);
		
		
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

	@Computation
	public CentroidOffsetsResult calcAvgCentroidOffsets(CentroidOffsetsResult[] offsetsIterations, int[] goodSpots) {
		return  calculateAvgCentroidOffsets(offsetsIterations, goodSpots);
	}
		
	private CentroidOffsetsResult calculateAvgCentroidOffsets(CentroidOffsetsResult[] offsetsIterations, int[] goodSpots) {

		logger.info(MessageGenerator.generateMessage("computation.start", "calcAvgCentroidOffsets"));
		
		FloatPoint avgImageTranslation = new FloatPoint();
		float avgImageScale = 0.0f;
		float avgImageRotation = 0.0f;
		FloatPoint[] avgCcdCentroidOffsets = new FloatPoint[offsetsIterations[0].getCartesianCentroidOffsets().length];
		FloatPoint[] avgCartesianCentroidOffsets = new FloatPoint[offsetsIterations[0].getCartesianCentroidOffsets().length];

		double iterations = offsetsIterations.length;

		
		for (int i=0; i<offsetsIterations.length; i++) {
			avgImageTranslation = avgImageTranslation.add(offsetsIterations[i].getImageTranslation().quot(iterations));
			avgImageScale += offsetsIterations[i].getImageScale()/iterations;
			avgImageRotation += offsetsIterations[i].getImageRotation()/iterations;
			for (int j=0; j<avgCcdCentroidOffsets.length; j++) {
				if (i==0) {
					// initialize
					avgCcdCentroidOffsets[j] = new FloatPoint();
					avgCartesianCentroidOffsets[j] = new FloatPoint();
				} 
				// don't calc average for a bad spot (display should show a zero offset for these)
				if (goodSpots[j] == 1) {
					avgCcdCentroidOffsets[j] = avgCcdCentroidOffsets[j].add(offsetsIterations[i].getCcdCentroidOffsets()[j].quot(iterations));
					avgCartesianCentroidOffsets[j] = avgCartesianCentroidOffsets[j].add(offsetsIterations[i].getCartesianCentroidOffsets()[j].quot(iterations));
				}
			}
		}
		
		CentroidOffsetsResult avgCentroidOffsets = new CentroidOffsetsResult( avgImageTranslation,  avgImageScale,  avgImageRotation, 
			 avgCcdCentroidOffsets, avgCartesianCentroidOffsets, goodSpots);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calcAvgCentroidOffsets"));
		
		return avgCentroidOffsets;

		
	}

	public int[] calculateAvgFindCentStatus(int[][] findCentStatusIterations) {
		
		// for now, a spot is good only if it was good every time
		int[] avgFindCentStatus = new int[findCentStatusIterations[0].length];
			for (int j=0; j<findCentStatusIterations[0].length; j++) {
				boolean status = true;
				for (int i=0; i<findCentStatusIterations.length; i++) {
				
				// if all iterations are success, then it is a good spot, otherwise not
				 if (findCentStatusIterations[i][j] != Constants.FIND_CENT_STATUS_SUCCESS && 
						 findCentStatusIterations[i][j] != Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_X &&
						 findCentStatusIterations[i][j] != Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_Y
						 ) status = false;
			}
			avgFindCentStatus[j] =	status ? 1: 0;
		}
		
		return avgFindCentStatus;
	}
	
	@Computation
	public MakeTemplateResult makeTemplate(int phasingSubimageFftSize, int phasingTemplateCount, FindCentConfig findCentConfig, PupilMask pupilMask, Filter filter) throws Exception {
		
		/*
		 * -output array is a 4 dim, array with the following size allocations:
		 * This is a 4-dim array which should be pre-allocated as follows:
		 * dim-1/2: X,Y should nominally be 2*irad+1
		 * dim-3: number of templates to calculate
		 * dim-4: 3 for the three edge angles
		 */
		
		logger.info(MessageGenerator.generateMessage("computation.start", "makeTemplate"));

		JmakeTemplate jmakeTemplate = new JmakeTemplate();
		RetVal retVal = new RetVal();

		int dim1 = findCentConfig.getIrad() * 2 + 1;
		float[][][][] templateArray = new float[dim1][dim1][phasingTemplateCount][3];

		
		Object[] result = jmakeTemplate.jmakeTemplate(retVal, phasingSubimageFftSize, phasingSubimageFftSize, findCentConfig.getItermax(), findCentConfig.getImargin(), findCentConfig.getNgauss(), 
				Constants.SPOT_TYPE_INTERIOR, findCentConfig.getIrad(), Constants.TEMPLATE_CENTROID_CALC_METHOD_FIND_CENT, pupilMask.getSecPerPixel(), filter.getWavelength() * Constants.NM_TO_MICRONS, 
				pupilMask.getCrossHairDiam() * Constants.METERS_TO_UM,
				pupilMask.getSpotDiamInterior() * Constants.METERS_TO_UM/2.0f, templateArray);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("makeTemplate calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

				
		MakeTemplateResult makeTemplateResult = new MakeTemplateResult(templateArray);


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "makeTemplate"));

		return makeTemplateResult;
		
	}
	
	@Computation
	public BbAnalyzeFrameResult bbAnalyzeFrame(float[][] frame, FindCentroidsResult findCentroidsResult, int[] edgeAngle, float[][][][] templateArray, int numberOfSegments) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "bbAnalyzeFrame"));

		JbbAnalyzeFrame jbbAnalyzeFrame = new JbbAnalyzeFrame();
		RetVal retVal = new RetVal();

		int edgeCount = edgeAngle.length;
		
		List<FloatPoint> centroidList = Arrays.asList(findCentroidsResult.getCentroidList());
		List<FloatPoint> edgeCentroidList = centroidList.subList(numberOfSegments, numberOfSegments+edgeCount);
		
		float[] centroidsX = FloatPointListEncoder.extractXArray(edgeCentroidList);
		float[] centroidsY = FloatPointListEncoder.extractYArray(edgeCentroidList);
		
		int[] foundCentroids = findCentroidsResult.getFoundSubimageFlags();
		int[] foundEdgeCentroids = Arrays.copyOfRange(foundCentroids, numberOfSegments, numberOfSegments+edgeCount);

		float[] coherenceArray = new float[edgeCount];
		float[] bestCorrelationIndex = new float[edgeCount];
		
		Object[] result = jbbAnalyzeFrame.jbbAnalyzeFrame(retVal, frame, centroidsX, centroidsY, foundEdgeCentroids, edgeAngle, templateArray, coherenceArray, bestCorrelationIndex);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("bbAnalyzeFrame calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

				
		BbAnalyzeFrameResult bbAnalyzeFrameResult = new BbAnalyzeFrameResult(coherenceArray, bestCorrelationIndex);


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "bbAnalyzeFrame"));

		return bbAnalyzeFrameResult;
		
	}
	
	@Computation
	public BbAnalyzeSequenceResult bbAnalyzeSequence(int[] edgeAngle, int[] edgeColor, float[][] coherenceArraySet, float stepSize, int numSegments, 
			int[] plusPiston, int[] minusPiston, float ringModeCorrectionFactor, Filter filter, float bbPhasingFracInterval,
			float[] ringMode, int numSteps, int[] useForAnalysis, int[] goodSpots) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "jbbAnalyzeSequence"));

		int numEdges = edgeAngle.length;
		
		JbbAnalyzeSequence jbbAnalyzeSequence = new JbbAnalyzeSequence();
		RetVal retVal = new RetVal();

		// generate coherenceTable
		float[][] coherenceTable = new float[numEdges][numSteps];
		for (int i=0; i<numEdges; i++) {
			for (int j=0; j<numSteps; j++) {
				coherenceTable[i][j] = coherenceArraySet[j][i];
			}
		}
		
		
		
		float sigmaMicrons = filter.getCoherenceLength();

		// rowFlagIn is whether the edge can be used
		int[] rowFlagIn = new int[numEdges];
		for (int i=0; i<numEdges; i++) {
			rowFlagIn[i] = useForAnalysis[i+numSegments] & goodSpots[i+numSegments];
		}
		
		float[][] asca = JavaComputations.generatePhasingInteractionMatrix(numEdges, numSegments, plusPiston, minusPiston);
		
		
		float[] stepCorr = new float[numEdges];
		
		float[] actCalc = new float[numSegments];
		
		float[] resid = new float[numEdges];
		
		int[] rowFlagOut = new int[numEdges];
		
		
		Object[] result = jbbAnalyzeSequence.jbbAnalyzeSequence(retVal, coherenceTable, sigmaMicrons, stepSize, bbPhasingFracInterval, asca, edgeAngle, edgeColor, 
				rowFlagIn, ringMode, ringModeCorrectionFactor, stepCorr, actCalc, resid, rowFlagOut);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("bbAnalyzeSequence calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		int  constrainedSegmentCount = (Integer)result[0];
		float segmentPistonRms = (Float)result[1]; 

		BbAnalyzeSequenceResult bbAnalyzeSequenceResult = new BbAnalyzeSequenceResult(stepCorr, actCalc, resid, rowFlagIn, rowFlagOut, constrainedSegmentCount, segmentPistonRms);


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "jbbAnalyzeSequence"));

		return bbAnalyzeSequenceResult;
		
	}
	
	
	@Computation
	public FixPistonsResult fixPistons(FloatPoint[] actuatorPositions, float[] actCalc) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "fixPistons"));

		JfixPistons jfixPistons = new JfixPistons();
		RetVal retVal = new RetVal();

		float[] pistonRaw = actCalc;
		
		float[] actuatorPositionsX = FloatPointListEncoder.extractXArray(Arrays.asList(actuatorPositions));
		float[] actuatorPositionsY = FloatPointListEncoder.extractYArray(Arrays.asList(actuatorPositions));

		float[] actRaw = new float[actuatorPositions.length];
		float[] actFixed = new float[actuatorPositions.length];
		
		
		Object[] result = jfixPistons.jfixPistons(retVal, pistonRaw, actuatorPositionsX, actuatorPositionsY, actRaw, actFixed);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("fixPistons calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		float actRms = ((Float) result[0]);

		FixPistonsResult fixPistonsResult = new FixPistonsResult(actRaw, actFixed, actRms);

		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "fixPistons"));

		return fixPistonsResult;
		
	}
	
	@Computation
	public CalcDesiredActCommandsResult fixPistonsToDesiredActs(FixPistonsResult fixPistonsResult) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "fixPistonsToDesiredActs"));

		float[][] pistonActs = new float[36][3];
		float[][] desiredActDeltas = new float[36][3];

		if (fixPistonsResult != null && fixPistonsResult.getActFixed() != null) {
			for (int i=0; i<36; i++) {
				// multiply by -1.0 to turn measured errors into commands
				desiredActDeltas[i][0] = -Constants.MICRONS_TO_NM * fixPistonsResult.getActFixed()[i*3];  
				desiredActDeltas[i][1] = -Constants.MICRONS_TO_NM * fixPistonsResult.getActFixed()[i*3 + 1];  
				desiredActDeltas[i][2] = -Constants.MICRONS_TO_NM * fixPistonsResult.getActFixed()[i*3 + 2];  
			}
		}
		float desiredActDeltasRms = Constants.MICRONS_TO_NM * fixPistonsResult.getActRms();


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "fixPistonsToDesiredActs"));

		return new CalcDesiredActCommandsResult(pistonActs, 0.0f, desiredActDeltas, desiredActDeltasRms,  0.0f, 0.0f);
	}
	
	@Computation
	public PhasingStatsResult calculatePhasingStats(int[] rowFlagIn, int[] rowFlagOut, float[] stepCorr, float[] stepResid) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calculatePhasingStats"));

		
		int goodEdgeCount = 0;

		// calculate goodEdgeCount
		int[] rowUsed = new int[rowFlagIn.length];
		// row used might be the same as rowFlagOut
		for (int i=0; i<rowFlagIn.length; i++) {
			rowUsed[i] = rowFlagIn[i] & rowFlagOut[i];
			goodEdgeCount += rowUsed[i];
		}
		
		// maximum of the absolute value of step_corr
		float edgeErrorMax = JavaComputations.calcMax(stepCorr, rowUsed); 
		// RSS of stepCorr
		float edgeErrorRss = JavaComputations.calcRss(stepCorr, rowUsed);  
		
		//  maximum of the absolute value of step_resid
		float residualEdgeErrorMax = JavaComputations.calcMax(stepResid, rowUsed);
		// RSS of step_resid
		float residualEdgeErrorRss = JavaComputations.calcRss(stepResid, rowUsed); 
		
		
		PhasingStatsResult phasingStatsResult = new PhasingStatsResult(goodEdgeCount, edgeErrorMax, edgeErrorRss, residualEdgeErrorMax, residualEdgeErrorRss);

		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calculatePhasingStats"));

		return phasingStatsResult;
		
	}
	
	@Computation
	public ColorStepResult colorStep(int stepCount, float stepSizeMicrons) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "colorStep"));

		JcolorStep jcolorStep = new JcolorStep();
		RetVal retVal = new RetVal();

		float stepSizeNm = stepSizeMicrons * Constants.MICRONS_TO_NM;
		
		float[][] colorSteps = new float[stepCount+1][3];
		
		Object[] result = jcolorStep.jcolorStep(retVal, stepCount, stepSizeNm, colorSteps);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("colorStep calcuation error. " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		ColorStepResult colorStepResult = new ColorStepResult(colorSteps);

		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "colorStep"));

		return colorStepResult;
		
	}
	
	@Computation
	public ColorStepToActuatorsResult colorStepToActuators(float[] colors, int[] segmentColors) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "colorStepToActuators"));

		JcolorStepToActuators jcolorStepToActuators = new JcolorStepToActuators();
		RetVal retVal = new RetVal();

		float[] m1ActuatorDeltas = new float[segmentColors.length*3];
	
		Object[] result = jcolorStepToActuators.jcolorStepToActuators(retVal, colors, segmentColors, m1ActuatorDeltas);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("colorStepToActuators calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		ColorStepToActuatorsResult colorStepToActuatorsResult = new ColorStepToActuatorsResult(m1ActuatorDeltas);

		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "colorStepToActuators"));

		return colorStepToActuatorsResult;
		
	}
	
	
	public SufsSegmentCentroidsResult generateSufsSegmentCentroids (FindCentroidsResult findCentroidsResult, int[][] sufsGroupSegmentToMask) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "generateSufsSegmentCentroidOffsets"));

		FindCentroidsResult[] findSegmentCentroidsResult = new FindCentroidsResult[7];
		FloatPoint[] segmentCentroidList = new FloatPoint[NUM_SUFS_SEGMENT_SPOTS];
		float[] segmentIntensities = new float[NUM_SUFS_SEGMENT_SPOTS];
		float[] segmentPeaks = new float[NUM_SUFS_SEGMENT_SPOTS];
		int[] findCentStatuses = new int[NUM_SUFS_SEGMENT_SPOTS];
		
		// loop over each SUFS group segment
		for (int i=0; i<7; i++) {
			// loop over all spots
			for (int j=0; j<NUM_SUFS_SEGMENT_SPOTS; j++) {
				// convert numbering
				// subtract one from the sufsGroupSegmentToMask to get a zero based index
				int maskIndex = sufsGroupSegmentToMask[j][i]-1;
				segmentCentroidList[j] = findCentroidsResult.getCentroidList()[maskIndex];
				segmentIntensities[j] = findCentroidsResult.getIntensityList()[maskIndex];
				segmentPeaks[j] = findCentroidsResult.getPeakList()[maskIndex];
				findCentStatuses[j] = findCentroidsResult.getFindCentStatusList()[maskIndex];
			}
			findSegmentCentroidsResult[i] = new FindCentroidsResult(segmentCentroidList, segmentIntensities, segmentPeaks, findCentStatuses);

		}

		SufsSegmentCentroidsResult result = new SufsSegmentCentroidsResult(findSegmentCentroidsResult);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "generateSufsSegmentCentroidOffsets"));

		return result;
		
	}
	
	public int[][] generateSufsSegmentInts (int[] input, int[][] sufsGroupSegmentToMask) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "generateSufsSegmentInts"));

		int[][] output = new int[7][NUM_SUFS_SEGMENT_SPOTS];
		
		// loop over each SUFS group segment
		for (int i=0; i<7; i++) {
			// loop over all spots
			for (int j=0; j<NUM_SUFS_SEGMENT_SPOTS; j++) {
				// convert numbering
				// subtract one from the sufsGroupSegmentToMask to get a zero based index
				int maskIndex = sufsGroupSegmentToMask[j][i]-1;

				output[i][j] = input[maskIndex];
			}
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "generateSufsSegmentInts"));

		return output;
		
	}
	
	public FloatPoint[][] generateSufsSegmentFloatPoints (FloatPoint[] input, int[][] sufsGroupSegmentToMask) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "generateSufsSegmentFloatPoints"));

		FloatPoint[][] output = new FloatPoint[7][NUM_SUFS_SEGMENT_SPOTS];
		
		// loop over each SUFS group segment
		for (int i=0; i<7; i++) {
			// loop over all spots
			for (int j=0; j<NUM_SUFS_SEGMENT_SPOTS; j++) {
				// convert numbering
				// subtract one from the sufsGroupSegmentToMask to get a zero based index
				int maskIndex = sufsGroupSegmentToMask[j][i]-1;
				output[i][j] = input[maskIndex];
			}
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "generateSufsSegmentFloatPoints"));

		return output;
		
	}
	
	
	
	@Computation
	public SufsSegmentOffsetsResult calculateSufsCentroidOffsets(FindCentroidsResult findCentroidsResult, FindCentroidsResult refMapCentroidsResult, 
			CentroidOffsetsConfig centroidOffsetsConfig, 
			PupilMaskType pupilMaskType, int[] nspotTypes, int[] missingSpotFlags, int[][] sufsGroupSegmentToMask, float spotJumpedThreshold) throws Exception {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateSufsCentroidOffsets"));

		
		SufsSegmentCentroidsResult sufsSegmentCentroidsResult = generateSufsSegmentCentroids(findCentroidsResult, sufsGroupSegmentToMask);
		SufsSegmentCentroidsResult sufsRefBeamSegmentCentroidsResult = generateSufsSegmentCentroids(refMapCentroidsResult, sufsGroupSegmentToMask);
		int[][] segNspotTypes = generateSufsSegmentInts(nspotTypes, sufsGroupSegmentToMask);
		int[][] segMissingSpotFlags = generateSufsSegmentInts(missingSpotFlags, sufsGroupSegmentToMask);
		
		CentroidOffsetsResult[] centroidOffsetsResult = new CentroidOffsetsResult[7];
		
		int[][] spotJumped = new int[7][NUM_SUFS_SEGMENT_SPOTS];
		int[][] validOffsets = new int[7][NUM_SUFS_SEGMENT_SPOTS];
		
		for (int groupSegment=0; groupSegment<7; groupSegment++) {
		
			FindCentroidsResult groupSegmentCentroidsResult = sufsSegmentCentroidsResult.getSegmentCentroidResultList()[groupSegment];
			FindCentroidsResult refBeamGroupSegmentCentroidsResult = sufsRefBeamSegmentCentroidsResult.getSegmentCentroidResultList()[groupSegment];
					
			centroidOffsetsResult[groupSegment] = calcCentroidOffsets(groupSegmentCentroidsResult.getCentroidList(),
					refBeamGroupSegmentCentroidsResult.getCentroidList(), 
					centroidOffsetsConfig, pupilMaskType, 
					segNspotTypes[groupSegment], 
					segMissingSpotFlags[groupSegment], 
					groupSegmentCentroidsResult.getFindCentStatusList());
			
			spotJumped[groupSegment] = determineJumpedSpots(centroidOffsetsResult[groupSegment].getCartesianCentroidOffsets(), spotJumpedThreshold);
			
			// determine the 'valid' offsets (spot not missing, was found and did not jump)
			int[] goodSpots = 	goodCentroidsFound(segMissingSpotFlags[groupSegment], groupSegmentCentroidsResult.getFindCentStatusList());
			validOffsets[groupSegment] = goodOffsetsFound(goodSpots, spotJumped[groupSegment]);
			
		}
		
		

		SufsSegmentOffsetsResult result = new SufsSegmentOffsetsResult(centroidOffsetsResult, spotJumped, validOffsets);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calculateSufsCentroidOffsets"));
		
		return result;
		
	}
	
	private int[] determineJumpedSpots(FloatPoint[] offsets, float threshold) {
		int[] jumped = new int[offsets.length];
		for (int i=0; i<offsets.length; i++) {
			jumped[i] = (offsets[i].mag() > threshold) ? 1 : 0;
		}
		return jumped;
	}
	
	
	@Computation
	public SufsCentroidStatsResult calculateSufsCentroidStats(SufsSegmentOffsetsResult sufsSegmentOffsetsResult, int[] findCentStatusList,  
			int[] nspotTypes, int[] missingSpotFlags, int[][] sufsGroupSegmentToMask) throws Exception {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateSufsCentroidStats"));


		int[][] segNspotTypes = generateSufsSegmentInts(nspotTypes, sufsGroupSegmentToMask);
		int[][] segMissingSpotFlags = generateSufsSegmentInts(missingSpotFlags, sufsGroupSegmentToMask);
		int[][] segFindCentStatusList = generateSufsSegmentInts(findCentStatusList, sufsGroupSegmentToMask);
		
		CentroidStatsResult[] centroidStatsResult = new CentroidStatsResult[7];
		
		for (int groupSegment=0; groupSegment<7; groupSegment++) {
		
			CentroidOffsetsResult centroidOffsetsResult = sufsSegmentOffsetsResult.extractCentroidOffsetsResult(groupSegment);
			int[] validOffsets = sufsSegmentOffsetsResult.getValidOffsets()[groupSegment];
			 
			centroidStatsResult[groupSegment] = calcCentroidStats(centroidOffsetsResult.getCartesianCentroidOffsets(), 
					segNspotTypes[groupSegment], validOffsets);
			
		}

		SufsCentroidStatsResult result = new SufsCentroidStatsResult(centroidStatsResult);

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateSufsCentroidStats"));

		return result;
		
	}
	
	@Computation
	public SufsCentroidStatsResult calculateSufsAvgCentroidStats(SufsSegmentOffsetsResult sufsAvgSegmentOffsetsResult, int[] avgGoodSpotMask,  
			int[] nspotTypes, int[][] sufsGroupSegmentToMask) throws Exception {


		logger.info(MessageGenerator.generateMessage("computation.start", "calculateSufsAvgCentroidStats"));

		int[][] segNspotTypes = generateSufsSegmentInts(nspotTypes, sufsGroupSegmentToMask);
		int[][] segAvgGoodSpotList = generateSufsSegmentInts(avgGoodSpotMask, sufsGroupSegmentToMask);
		
		CentroidStatsResult[] centroidStatsResult = new CentroidStatsResult[7];
		
		for (int groupSegment=0; groupSegment<7; groupSegment++) {
							
			CentroidOffsetsResult centroidOffsetsResult = sufsAvgSegmentOffsetsResult.extractCentroidOffsetsResult(groupSegment);
			 
			centroidStatsResult[groupSegment] = calcCentroidStats(centroidOffsetsResult.getCartesianCentroidOffsets(), 
					segNspotTypes[groupSegment], segAvgGoodSpotList[groupSegment]);
			
		}
	
		SufsCentroidStatsResult result = new SufsCentroidStatsResult(centroidStatsResult);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calculateSufsAvgCentroidStats"));

		return result;
		
	}
	
	
	@Computation
	public SufsSegmentOffsetsResult calcAvgSufsCentroidOffsets(SufsSegmentOffsetsResult[] sufsSegmentOffsetsResultIterations, int[] goodSpotsMask, int[][] sufsGroupSegmentToMask) throws Exception {

		logger.info(MessageGenerator.generateMessage("computation.start", "calcAvgSufsCentroidOffsets"));

		// we need to reorder this into sufsSegmentOffsetsResultIterations into CentroidOffsetsResult[groupSegment][iteration]
		
		//int[][] goodSpotsGroupSegment = generateSufsSegmentInts(goodSpotsMask, sufsGroupSegmentToMask);

		
		CentroidOffsetsResult[][] centroidOffsetsResultGroupSegmentIteration = new CentroidOffsetsResult[7][sufsSegmentOffsetsResultIterations.length];
		
		for (int iteration=0; iteration<sufsSegmentOffsetsResultIterations.length; iteration++) {
		
			for (int groupSegment=0; groupSegment<7; groupSegment++) {
				CentroidOffsetsResult centroidOffsetsResult = sufsSegmentOffsetsResultIterations[iteration].extractCentroidOffsetsResult(groupSegment);
				centroidOffsetsResultGroupSegmentIteration[groupSegment][iteration] = centroidOffsetsResult;
				
			}
		}
				
		CentroidOffsetsResult[] avgCentroidOffsetsResult = new CentroidOffsetsResult[7];
		

		
		// spotJumped not used, but in the interface
		int[][] spotJumped = new int[7][NUM_SUFS_SEGMENT_SPOTS];
		int[][] validOffsets = new int[7][NUM_SUFS_SEGMENT_SPOTS];
		
	
		for (int groupSegment=0; groupSegment<7; groupSegment++) {
			
			// find the averaged valid offsets
			int[] averagedGoodSpots = new int[NUM_SUFS_SEGMENT_SPOTS];
			Arrays.fill(averagedGoodSpots, 1);
			
			for (int i=0; i<centroidOffsetsResultGroupSegmentIteration[groupSegment].length; i++) {
				int[] goodSpotsIteration = centroidOffsetsResultGroupSegmentIteration[groupSegment][i].getGoodSpots();
				for (int j=0; j<NUM_SUFS_SEGMENT_SPOTS; j++) {
					averagedGoodSpots[j] = averagedGoodSpots[j] & goodSpotsIteration[j];
				}
			}
											
			// reuse common calcAvgCentroidOffsets
			avgCentroidOffsetsResult[groupSegment] = calculateAvgCentroidOffsets(centroidOffsetsResultGroupSegmentIteration[groupSegment], averagedGoodSpots);
			
			validOffsets[groupSegment] = averagedGoodSpots;
		}


		SufsSegmentOffsetsResult result = new SufsSegmentOffsetsResult(avgCentroidOffsetsResult, spotJumped, validOffsets);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calcAvgSufsCentroidOffsets"));

		return result;
		
	}
	

	private SufsZernikeResult calcSufsZernikesOneSeg(FloatPoint[] idealSpots,  
			FloatPoint[] offsets, float aHex, float secPerPixel, int[] goodSpots, int zernikeOrder) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calcSufsZernikesOneSeg"));

		JsufsOffsetsToZernikes jsufsOffsetsToZernikes = new JsufsOffsetsToZernikes();
		RetVal retVal = new RetVal();

		List<FloatPoint> idealSpotsInMeters = FloatPointListEncoder.multiplyPoints(idealSpots, aHex);
		float[] xIdealSpotsInMeters = FloatPointListEncoder.extractXArray(idealSpotsInMeters);
		float[] yIdealSpotsInMeters = FloatPointListEncoder.extractYArray(idealSpotsInMeters);
		
		
		List<FloatPoint> centroidOffsets = Arrays.asList(offsets);
		
		// offsets to arcseconds
		List<FloatPoint> offsetsArcSec = FloatPointListEncoder.multiplyPoints(centroidOffsets, secPerPixel);
		float[] offsetsArcsecondsX = FloatPointListEncoder.extractXArray(offsetsArcSec);
		float[] offsetsArcsecondsY = FloatPointListEncoder.extractYArray(offsetsArcSec);
		
		int zernikeCount = (zernikeOrder+1)*(zernikeOrder+2)/2;
		
		float[] bestFitZernikes = new float[zernikeCount];
		float[] theoreticalOffsetsX = new float[offsetsArcsecondsX.length];
		float[] theoreticalOffsetsY = new float[offsetsArcsecondsY.length];
		
		
		Object[] result = jsufsOffsetsToZernikes.jsufsOffsetsToZernikes(retVal, xIdealSpotsInMeters, yIdealSpotsInMeters, 
				offsetsArcsecondsX, offsetsArcsecondsY, aHex*Constants.METERS_TO_MM, goodSpots, zernikeOrder,
				bestFitZernikes, theoreticalOffsetsX, theoreticalOffsetsY);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("sufsOffsetsToZernikes calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}
		
		float whFactor = (Float)result[0];
		

		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "calcSufsZernikesOneSeg"));

		List<FloatPoint> theoreticalOffsetList = FloatPointListEncoder.constructFromXandY(theoreticalOffsetsX, theoreticalOffsetsX);
		FloatPoint[] theoreticalOffsets = theoreticalOffsetList.toArray(new FloatPoint[0]);
		return new SufsZernikeResult(bestFitZernikes, theoreticalOffsets, whFactor);
		
	}
	
	@Computation
	public SufsSegmentZernikeResult calculateSufsZernikes(FloatPoint[] sufsSegmentIdealSpotLocations, FloatPoint[] maskOffsets, float aHex, float secPerPixel,
			int[][] validOffsets, int[][] sufsGroupSegmentToMask, int[] sufsZernikeOrder,
			int[] groupSegmentNumbers) throws Exception {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateSufsZernikes"));
		
		//FloatPoint[][] idealSpots = generateSufsSegmentFloatPoints(sufsMaskSpotLocations, sufsGroupSegmentToMask);
		FloatPoint[][] segmentOffsets = generateSufsSegmentFloatPoints(maskOffsets, sufsGroupSegmentToMask);
		
		
		// get the sufsOffsetsToZernikes for each segment
		
		
		SufsZernikeResult[] sufsZernikeResults = new SufsZernikeResult[7];
		
		// for each segment in the group
		for (int groupSegment=0; groupSegment<7; groupSegment++) {
			
			int zernikeOrder = sufsZernikeOrder[groupSegmentNumbers[groupSegment]-1];
							
			sufsZernikeResults[groupSegment] = calcSufsZernikesOneSeg(sufsSegmentIdealSpotLocations,  
				segmentOffsets[groupSegment], aHex, secPerPixel, validOffsets[groupSegment], zernikeOrder);
		
		}

		SufsSegmentZernikeResult result = new SufsSegmentZernikeResult(sufsZernikeResults);

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateSufsZernikes"));

		return result;
		
	}

	
	@Computation
	public SufsSegmentZernikeStatsResult calculateSufsZernikeStats(SufsSegmentZernikeResult[] sufsSegmentZernikeResultIterations) throws Exception {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateSufsZernikeStats"));
		
		
		// get the sufsOffsetsToZernikes for each segment
		
		SufsZernikeStatsResult[] sufsZernikeStatsResults = new SufsZernikeStatsResult[7];
		
		
		// for each segment in the group
		for (int groupSegment=0; groupSegment<7; groupSegment++) {
			
			// holder for all zernikes for and each iteration for a single segment
			int numberOfZernikesForSegment = sufsSegmentZernikeResultIterations[0].getBestFitZernikes()[groupSegment].length;
			float[][] bestFitZernikesForSegmentIterations = new float[numberOfZernikesForSegment][sufsSegmentZernikeResultIterations.length];

			
			// for each iteration
			for (int j=0; j<sufsSegmentZernikeResultIterations.length; j++) {
				SufsSegmentZernikeResult result = sufsSegmentZernikeResultIterations[j];
				
				float[] bestFitZernikesForSegment = result.getBestFitZernikes()[groupSegment];
				for (int k=0; k<bestFitZernikesForSegment.length; k++) {
					bestFitZernikesForSegmentIterations[k][j] = bestFitZernikesForSegment[k];
				}
			}
			
			// for each zernike in a single segment
			float[] zernikeMeans = new float[numberOfZernikesForSegment];
			float[] zernikeEoms = new float[numberOfZernikesForSegment];
			for (int zernike=0; zernike<numberOfZernikesForSegment; zernike++) {
				// zernikeIterations is now a single array of iterations of best fit for a single zernike within a single segment
				double[] zernikeIterations = JavaComputations.floatArrayToDouble(bestFitZernikesForSegmentIterations[zernike]);
				// now we find the mean and eom for this segment zernike
				zernikeMeans[zernike] = JavaComputations.getMean(zernikeIterations);
				zernikeEoms[zernike] = JavaComputations.getEom(zernikeIterations);
			}
			
			// place all the zernike errors and means for this segment in a SufsZernikeStatsResult
			sufsZernikeStatsResults[groupSegment] = new SufsZernikeStatsResult(zernikeMeans, zernikeEoms);
						
		}

		// put all seven segment results into the return object
		SufsSegmentZernikeStatsResult result = new SufsSegmentZernikeStatsResult(sufsZernikeStatsResults);

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateSufsZernikeStats"));

		return result;
		
	}


	/*
	 * Given the X and Y coarse mirror motions that are about to be sent to the instrument, calculate the desired telescope commands
	 * to keep the telescope centered.  These are calculated as approx using on-sky data.to keep the telescope centered.  
	 * 
	 * @param coarseMirrorOffsets 	The x,y motion about to be applied to the coarse mirror in microns
	 * @param telPerCoarseMotion	Telescope motion (in arcsecs) for a 1 micron coarse mirror move
	 * 
	 * @return 						Required telescope motion (az,el) in arcseconds
	 * 
	 */
	public FloatPoint coarseOffsetsToTelMoves(Point coarseMirrorOffsets, float telPerCoarseMotion) {

		float sin30 = 0.5f;
		float cos30 = 0.866025404f;
		
		float az = -1.0f * telPerCoarseMotion * (sin30 * coarseMirrorOffsets.x - cos30 * coarseMirrorOffsets.y);
		float el = -1.0f * telPerCoarseMotion * (cos30 * coarseMirrorOffsets.x + sin30 * coarseMirrorOffsets.y);

		return new FloatPoint(az, el);
	}
	
}


