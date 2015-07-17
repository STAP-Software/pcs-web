/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.java.JavaComputations;
import org.tmt.aps.peas.computation.model.AutoCenterTelCheckResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.computation.model.FindCentResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.ScaleError;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.AutoRefMapConfig;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfig;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.lang.interop.JcalculateCentroidOffsets;
import org.tmt.aps.peas.lang.interop.JcalculateCentroidStats;
import org.tmt.aps.peas.lang.interop.JdecomposeActs;
import org.tmt.aps.peas.lang.interop.JfindAndIdentify;
import org.tmt.aps.peas.lang.interop.JfindCent;
import org.tmt.aps.peas.lang.interop.JfindCentroids;
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

public class ComputationLibraryImpl implements ComputationLibrary {

	Logger logger = Logger.getLogger(this.getClass());

	StatusLogger statusLogger;

	// package protected constructor
	ComputationLibraryImpl() throws Exception {

		statusLogger = (StatusLogger) InitialContext.doLookup("java:module/StatusLogger");

	}

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
		
		FindCentResult findCentResult = new FindCentResult(centroid, (Float)result[2], (Float)result[3]);

		logger.info(MessageGenerator.generateMessage("computation.success", "findCent"));

		return findCentResult;
	}

	public FindCentroidsResult findCentroids(float[][] frame, FIResult fiResult, FindCentConfig findCentConfig) throws ComputationException {

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

		// TODO: This needs to be generalized

		int[] nspotType = new int[arrayLen];
		for (int i = 0; i < arrayLen; i++) {
			nspotType[i] = Constants.SPOT_TYPE_INTERIOR;
		}

		float[] x_cent = new float[arrayLen];
		float[] y_cent = new float[arrayLen];
		float[] intensity = new float[arrayLen];
		float[] peak = new float[arrayLen];

		Object[] result = jfindCentroids.jfindCentroids(retVal, frame, findCentConfig.getIrad(), findCentConfig.getImargin(), x_guesses,
				y_guesses, findCentConfig.getItermax(), nspotType, findCentConfig.getNgauss(), x_cent, y_cent, intensity, peak);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("No good centroid could be found");
		}

		
		FindCentroidsResult findCentroidsResult = new FindCentroidsResult(x_cent, y_cent, intensity, peak);
		

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

	public FIResult findAndIdentify(float[][] frame, int numSpots, FIConfig fiConfig, RefBeamMap currentRefMap, RefBeamMap refDefMap)
			throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "findAndIdentify"));

		JfindAndIdentify jfindAndIdentify = new JfindAndIdentify();
		RetVal retVal = new RetVal();

		float[] fiParams = new float[6];

		int nsp = -1; // segment number of current group
		int ngp = -1; // sufs group number

		List<FloatPoint> refDefCentroids = refDefMap.getCentroidMap().getFindCentroidsResult().getCentroidList();
		float[] x_ref_def = FloatPointListEncoder.extractXArray(refDefCentroids);
		float[] y_ref_def = FloatPointListEncoder.extractYArray(refDefCentroids);

		// initialize spot_flag
		// TODO: this needs to be derived from missing spots
		int[] spot_flag = new int[numSpots];
		for (int i = 0; i < numSpots; i++) {
			spot_flag[i] = 2;
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
				spot_flag, fiResult.getXiRst(), fiResult.getYiRst(), fiResult.getxPeak(), fiResult.getyPeak(), fiResult.getnDetect(),
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
			throw new HandMarkRequiredException();
		}

		if (userAssistException.shouldThrow()) {
			throw userAssistException;
		}
		
		logger.info(MessageGenerator.generateMessage("computation.success", "evalFiResult"));

	}

	// TODO: move to Fortran?
	public FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {

		logger.info(MessageGenerator.generateMessage("computation.start", "pixLocationToDeltaArcSeconds"));

		FloatPoint result = JavaComputations.pixLocationToDeltaArcSeconds(measuredPix, desiredPix, secPerPixel);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "pixLocationToDeltaArcSeconds"));
		
		return result;
	}
	
	public float calcRms(float[][] data) {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calcRms"));

		float result =  JavaComputations.calcRms(data);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calcRms"));
		
		return result;
	}

	@Override
	public CentroidOffsetsResult calculateCentroidOffsets(List<FloatPoint> centroids, List<FloatPoint> refMapCentroids,
			CentroidOffsetsConfig centroidOffsetsConfig, PupilMaskType pupilMaskType) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateCentroidOffsets"));

		JcalculateCentroidOffsets jcalculateCentroidOffsets = new JcalculateCentroidOffsets();
		RetVal retVal = new RetVal();

		float[] fiParams = new float[6];

		float[][] ref_cent = FloatPointListEncoder.convertToNby2Array(refMapCentroids);
		float[][] centroid = FloatPointListEncoder.convertToNby2Array(centroids);

		// initialize spot_flag
		// TODO: this needs to be derived from missing spots
		int numSpots = centroids.size();
		int[] good_spots = new int[numSpots];
		for (int i = 0; i < numSpots; i++) {
			good_spots[i] = 1;
		}

		// output arrays
		float[][] ccdOffsets = new float[numSpots][2];
		float[][] cartesianOffsets = new float[numSpots][2];
		float[] image_translation = new float[2];

		Object output[] = jcalculateCentroidOffsets.jcalculateCentroidOffsets(retVal, centroid, ref_cent,
				centroidOffsetsConfig.isRemoveScale() ? 1 : 0, centroidOffsetsConfig.isRemoveRotation() ? 1 : 0, good_spots, ccdOffsets,
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

	public CentroidStatsResult calculateCentroidStats(List<FloatPoint> centroidOffsets) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateCentroidStats"));

		JcalculateCentroidStats jcalculateCentroidStats = new JcalculateCentroidStats();
		RetVal retVal = new RetVal();

		float[][] offsets = FloatPointListEncoder.convertToNby2Array(centroidOffsets);

		// initialize spot_flag
		// TODO: this needs to be derived from missing spots
		int numSpots = centroidOffsets.size();
		int[] good_spots = new int[numSpots];
		for (int i = 0; i < numSpots; i++) {
			good_spots[i] = 1;
		}

		Object output[] = jcalculateCentroidStats.jcalculateCentroidStats(retVal, offsets, good_spots);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Centroid Offset Stats Calculation Error");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateCentroidStats"));

		// store fi_param values
		return new CentroidStatsResult((Integer) output[0], (Float) output[1], (Float) output[2], (Float) output[3], (Float) output[4]);

	}

	@Override
	public ScaleError passiveTiltScaleError(List<FloatPoint> centroidOffsets, List<FloatPoint> centerSpot) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "passiveTiltScaleError"));

		JpassiveTiltScaleError jpassiveTiltScaleError = new JpassiveTiltScaleError();
		RetVal retVal = new RetVal();

		float[][] offsets = FloatPointListEncoder.convertToNby2Array(centroidOffsets);

		float[] x_ref_def = FloatPointListEncoder.extractXArray(centerSpot);
		float[] y_ref_def = FloatPointListEncoder.extractYArray(centerSpot);

		Object output[] = jpassiveTiltScaleError.jpassiveTiltScaleError(retVal, offsets, x_ref_def, y_ref_def);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Passive Tilt Scale Error Calculation Error");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "passiveTiltScaleError"));

		// store fi_param values
		return new ScaleError((Float) output[0], (Float) output[1]);
	}

	@Override
	public float[][] ttOffsetsToActs(List<FloatPoint> actuatorPositions, float imageScale, List<FloatPoint> centroidOffsets)
			throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "ttOffsetsToActs"));

		JttOffsetsToActs jttOffsetsToActs = new JttOffsetsToActs();
		RetVal retVal = new RetVal();

		float[] x_act_pos = FloatPointListEncoder.extractXArray(actuatorPositions);
		float[] y_act_pos = FloatPointListEncoder.extractYArray(actuatorPositions);

		float[] x_offsets = FloatPointListEncoder.extractXArray(centroidOffsets);
		float[] y_offsets = FloatPointListEncoder.extractYArray(centroidOffsets);

		// output arrays
		float[] desired_act_deltas = new float[actuatorPositions.size()];
		float[] x_offsets_out = new float[centroidOffsets.size()];
		float[] y_offsets_out = new float[centroidOffsets.size()];

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

	@Override
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

	@Override
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
	
	public float[][] addMatricies(float[][] matrix1, float[][] matrix2) throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "addMatricies"));

		float[][] result = JavaComputations.addMatricies(matrix1, matrix2);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "addMatricies"));

		return result;
	}

	public void autoRefMapCheck(AutoRefMapConfig autoRefMapConfig, Point currentPosition, float temperature, 
			int numIterations, Date currentDate, RefBeamMap currentRefMap) throws ComputationException, AutoRefMapCheckException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "autoRefMapCheck"));

		JavaComputations.autoRefMapCheck(autoRefMapConfig, currentPosition, temperature,  
				numIterations, currentDate, currentRefMap);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "autoRefMapCheck"));
	}

	public AutoCenterTelCheckResult autoCenterTelescopeCheck(AutoCenterTelConfig autoCenterTelConfig, FloatPoint deltaAzEl, FloatPoint lastMove) {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "autoCenterTelescopeCheck"));
		
		AutoCenterTelCheckResult result = JavaComputations.autoCenterTelescopeCheck(autoCenterTelConfig, deltaAzEl, lastMove);

		logger.info(MessageGenerator.generateMessage("computation.success", "autoCenterTelescopeCheck"));
		
		return result;
	}

	@Override
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
}


