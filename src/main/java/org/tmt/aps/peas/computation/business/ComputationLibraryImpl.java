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
import org.tmt.aps.peas.common.IntegerListEncoder;
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
import org.tmt.aps.peas.computation.model.NbActuatorsResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeFilterSequenceResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeFrameResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeStepSequenceResult;
import org.tmt.aps.peas.computation.model.PhasingStatsResult;
import org.tmt.aps.peas.computation.model.PseudoTipTiltCentroidStatsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.computation.model.StartupComputationsResult;
import org.tmt.aps.peas.computation.model.Subimage;
import org.tmt.aps.peas.computation.model.SufsCentroidStatsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentCentroidsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentOffsetsResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeResult;
import org.tmt.aps.peas.computation.model.SufsSegmentZernikeStatsResult;
import org.tmt.aps.peas.computation.model.SufsZernikeResult;
import org.tmt.aps.peas.computation.model.SufsZernikeStatsResult;
import org.tmt.aps.peas.computation.model.TerraceModeComponentsResult;
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
import org.tmt.aps.peas.lang.interop.JnbActuators;
import org.tmt.aps.peas.lang.interop.JnbAnalyzeFilterSequence;
import org.tmt.aps.peas.lang.interop.JnbAnalyzeFrame;
import org.tmt.aps.peas.lang.interop.JnbAnalyzeStepSequence;
import org.tmt.aps.peas.lang.interop.JoptimalPistons;
import org.tmt.aps.peas.lang.interop.JremoveBadPixels;
import org.tmt.aps.peas.lang.interop.JsufsOffsetsToZernikes;
import org.tmt.aps.peas.lang.interop.JterraceModeComponents;
import org.tmt.aps.peas.lang.interop.JttOffsetsToActs;
import org.tmt.aps.peas.lang.interop.RetVal;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.exception.HandMarkRequiredException;
import org.tmt.aps.peas.procedure.exception.NonLinearIntensitiesException;
import org.tmt.aps.peas.procedure.exception.UserAssistRequiredException;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.statusLog.business.StatusLogger;

/**
 * The computation library session EJB; this class contains all the methods that PEAS-PCS uses to perform all computations, either implemented in FORTRAN or Java.
 * When an executor object needs to call a computation, this is the EJB whose methods are called to accomplish this.
 * The methods in this EJB will delegate computations to FORTRAN library routines or to {@link org.tmt.aps.peas.computation.java.JavaComputations}.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.java.JavaComputations
 */
@Stateless
public class ComputationLibraryImpl {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	StatusLogger statusLogger;

	
	private static final int NUM_SUFS_SEGMENT_SPOTS = 127;


	/**
	 * Compute the centroid using a center of mass or Gaussian fit method.
	 * This method calls the FORTRAN function in findCent.f90
	 * 
	 * @param frame Full image array containing subimages to be centroided.
	 * @param guess original guess for centroid
	 * @param findCentConfig configuration for find cent, containing irad, imargin, itermax, and ngauss
	 * @param nspotType Spot type to determine how the background is computed: 2 for peripheral spots; 1 for non-peripheral spots.
	 * @return result class containing original guess, computed centroid, intensities and findCent status flag
	 */
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


	/**
	 * Calls findCent for multiple passed in centroid guesses.  This method calls the FORTRAN function in findCentroid.f90
	 * 
	 * @param frame Full image array containing subimages to be centroided.
	 * @param fiResult result class returned from calling findAndIdentify function
	 * @param findCentConfigInterior findCent configuration for this mask type to be used for interior spots.  FindCentConfig is configuration for find cent, containing irad, imargin, itermax, and ngauss
	 * @param findCentConfigPeripheral findCent configuration for this mask type to be used for peripheral spots. FindCentConfig is configuration for find cent, containing irad, imargin, itermax, and ngauss
	 * @param nspotTypes array of spot types for background computation (2=peripheral spot; 1=other).
	 * @param missingSpotFlags array of flags indicating if a spot is expected to be present and if it is to be used in analysis.
	 * @param findAllMaskSpots override of missing spots, if true all spots should be found.  Typically true for reference beam frames.
	 * @return result class containing computed centroid, intensities and findCent status flag for all centroids attempted to be found
	 */
	@Computation
	public FindCentroidsResult findCentroids(float[][] frame, FIResult fiResult, FindCentConfig findCentConfigInterior,  FindCentConfig findCentConfigPeripheral, int[] nspotTypes, int[] missingSpotFlags, 
			boolean findAllMaskSpots) throws ComputationException {

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
		int[] passedMissingSpotFlags = new int[missingSpotFlags.length];
		if (findAllMaskSpots) {
			for (int i=0; i<missingSpotFlags.length; i++) {
				passedMissingSpotFlags[i] = Constants.MISSING_SPOT_TYPE_USE;
			}
		} else {
			
			for (int i=0; i<arrayLen; i++ ) {
				// add nDetect == 0 spots to the spots being ignored
				if (nspotTypes[i] == Constants.SPOT_TYPE_INTERIOR) {
					passedMissingSpotFlags[i] = (findCentConfigInterior.isIgnoreNdectZeroSpots() && fiResult.getnDetect()[i] == 0) ? Constants.MISSING_SPOT_TYPE_NOT_EXPECTED : missingSpotFlags[i];
				} else {
					passedMissingSpotFlags[i] = (findCentConfigPeripheral.isIgnoreNdectZeroSpots() && fiResult.getnDetect()[i] == 0) ? Constants.MISSING_SPOT_TYPE_NOT_EXPECTED : missingSpotFlags[i];					
				}
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

	/**
	 * Correct for bad pixels in the CCD image.  This method calls the FORTRAN function in removeBadPixels.f90.
	 * 
	 * @param frame Input CCD array
	 * @param badPixelList list of rectangles specifying all bad pixels and/or bad columns
	 * @return output CCD array with corrected bad pixels
	 */
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

	/**
	 * Find and Identify all of the sub-images in a CCD frame.  This method calls the FORTRAN function in findAndIdentify.f90.
	 * 
	 * @param frame CCD Frame to find sub-images in
	 * @param numSpots the number of spots to find
	 * @param fiConfig findAndIdentify computation configuration.  See {@link org.tmt.aps.peas.config.model.FIConfig} and findAndIdentify.f90 for complete descriptions.
	 * @param currentRefMap the reference beam map to use to force scale and rotation of image, if specified in the configuration
	 * @param refDefCentroids the ideal locations of the centroids to find
	 * @param missingSpotFlags array of flags indicating if a spot is expected and if it will be used for analysis
	 * @param findAllMaskSpots missing spots override, if true find all spots.  Typically true for reference beam frames.
	 * @return result class. Descriptions of each field are in the header descriptions in findAndIdentify.f90. 
	 */
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

	/**
	 * Evaluates the findAndIdentify result an throws exception if result fails threshold tests
	 * @param fiResult the findAndIdenfity result
	 * @param fiConfig the computation configuration used for findAndIdentify
	 * @param procedureConfig the procedure configuration (only used to determine the mask type used)
	 * @throws UserAssistRequiredException contains flags indicating which threshold(s) failed: fraction of filled boxes, Fourier quality or number of solutions. 
	 */
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

	/**
	 * Converts pixel location to a delta arcsecond value to center the telescope.  Calls {@link JavaComputations#pixLocationToDeltaArcSeconds(FloatPoint, FloatPoint, double)}
	 * 
	 * @param measuredPix center of current centroid(s)
	 * @param desiredPix desired position for the center
	 * @param secPerPixel arcseconds per pixel for this mask
	 * @return result class containing delta azimuth and delta elevation
	 */
	@Computation
	public CenterTelescopeCalcResult centerTelescopeCalc(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {

		logger.info(MessageGenerator.generateMessage("computation.start", "centerTelescopeCalc"));

		FloatPoint result = JavaComputations.pixLocationToDeltaArcSeconds(measuredPix, desiredPix, secPerPixel);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "centerTelescopeCalc"));
		
		return new CenterTelescopeCalcResult(result);
	}
	
	/**
	 * Calculates RMS for a given a 2-d input array.  Calls {@link JavaComputations#calcRms(float[][])}
	 * @param data the input 2-d array
	 * @return the RMS of all values in the array
	 */
	public float calcRms(float[][] data) {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calcRms"));

		float result =  JavaComputations.calcRms(data);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "calcRms"));
		
		return result;
	}

	/**
	 * Calculates centroid offsets given an array of centroids and reference map centroids
	 * @param centroids the array of centroids to find the offsets of
	 * @param refMapCentroids the reference map of centroids to find the offsets from
	 * @param centroidOffsetsConfig rotation removal and scale removal flags
	 * @param pupilMaskType the type of mask that produced the centroids (e.g. Fine Screen, Passive Tilt, etc)
	 * @param nspotTypes array of flags indicating if a spot is interior or peripheral
	 * @param missingSpotFlags array of flags indicating if a spot is to be used for this analysis
	 * @param findCentStatusList an array of flags indicating if/how this spot was found during find and identify
	 * @return a result object containing the offsets in ccd and cartesian coordinates, the image translation, rotation and scale, and which spots were actually used in the analysis.
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public CentroidOffsetsResult calculateCentroidOffsets(FloatPoint[] centroids, FloatPoint[] refMapCentroids,
			CentroidOffsetsConfig centroidOffsetsConfig, PupilMaskType pupilMaskType, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws ComputationException {

		return calcCentroidOffsets(centroids, refMapCentroids,
				centroidOffsetsConfig, pupilMaskType, nspotTypes, missingSpotFlags, findCentStatusList);
		
	}
	
	/*
	 * private method for centroid offsets, called by both calculateCentroidOffsets and calculate SufsCentroidOffsets.  This pattern is used
	 * so that sufs can also call this for each segment, without encountering a second @Computation annotation, which would confuse the 
	 * automatic data collection.  
	 */
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

	/**
	 * Calculates centroid offsets statistics such as max and rms offsets, enclosed energy and the spot with the max offset
	 * @param centroidOffsets the array of centroid offsets to calculate statistics on
	 * @param nspotTypes array of flags indicating if a spot is interior or peripheral
	 * @param missingSpotFlags array of flags indicating if a spot is to be used for this analysis
	 * @param findCentStatusList an array of flags indicating if/how this spot was found during find and identify
	 * @return status result object containing max and rms offsets, enclosed energy and the spot with the max offset
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public CentroidStatsResult calculateCentroidStats(FloatPoint[] centroidOffsets, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws ComputationException {
		return calcCentroidStats(centroidOffsets, nspotTypes, missingSpotFlags, findCentStatusList);
	}
	
	/*
	 * private method for centroid stats, version with missingSpotFlags and findCentStatusLists passed in.  This pattern is used
	 * so that sufs can also call this for each segment, without encountering a second @Computation annotation, which would confuse the 
	 * automatic data collection.  
	 */
	private CentroidStatsResult calcCentroidStats(FloatPoint[] centroidOffsets, int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws ComputationException {


		// spots that can be used (found without errors and should be used for analysis)
		int[] goodSpots = goodCentroidsFound(missingSpotFlags, findCentStatusList);
		
		return calcCentroidStats(centroidOffsets, nspotTypes, goodSpots);

	}

	/*
	 * private method for centroid stats, version with goodSpots (or validOffsets) passed in.  This pattern is used
	 * so that sufs can also call this for each segment, without encountering a second @Computation annotation, which would confuse the 
	 * automatic data collection.  
	 */
	private CentroidStatsResult calcCentroidStats(FloatPoint[] centroidOffsets, int[] nspotTypes, int[] goodSpots) throws ComputationException {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateCentroidStats"));

		
		for (int i=0; i<centroidOffsets.length; i++) {
			if (centroidOffsets[i].mag() == 0.0f && goodSpots[i] == 1) {
				System.out.println("divide by zero anticipated: " + i);
			}
		}
		
		
		
		JcalculateCentroidStats jcalculateCentroidStats = new JcalculateCentroidStats();
		RetVal retVal = new RetVal();

		float[][] offsets = FloatPointListEncoder.convertToNby2Array(Arrays.asList(centroidOffsets));

		Object output[] = jcalculateCentroidStats.jcalculateCentroidStats(retVal, offsets, goodSpots, nspotTypes);
		//Object output[] = {1, 2.0f, 3.0f, 4.0f, 5.0f};

		logger.info("Fortran call completed");
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Centroid Offset Stats Calculation Error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		logger.info(MessageGenerator.generateMessage("computation.success", "calculateCentroidStats"));

		// store fi_param values
		return new CentroidStatsResult((Integer) output[0], (Float) output[1], (Float) output[2], (Float) output[3], (Float) output[4]);

	}

	/**
	 * Calculates centroid stats for pseudo passive tilt
	 * @param centroidOffsetsPixels array of centroid offsets 
	 * @param nspotTypes array of flags indicating if spot is peripheral or interior
	 * @return a wrapper around {#link CentroidStatsResult} so that this result can be distinguished from a {#link CentroidStatsResult} when performing auto data collection
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
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
	
	/**
	 * Calculates the centroid offsets statistics on the average of the centroid offsets
	 * @param avgCentroidOffsets an array of averaged centroid offsets
	 * @param nspotTypes array of flags indicating if a spot is interior or peripheral
	 * @param missingSpotFlags this is not used, it should be removed from the method signature
	 * @param goodSpots array of flags indicating if a spot was usable for analysis for every iteration
	 * @return a wrapper around {#link CentroidStatsResult} so that this result can be distinguished from a {#link CentroidStatsResult} when performing auto data collection
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
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
	




	/**
	 * Calculates the desired actuator corrections from x and y passive tilt offsets
	 * @param actuatorPositions array of coordinates of the actuators in microns, numbered consecutively.
	 * @param imageScale Image scale in arcseconds per pixel.
	 * @param centroidOffsets array of centroid offsets for each segment
	 * @param mirrorConfig the incomplete mirror configuration (1=present, 0=missing)
	 * @return array of actuator deltas to add to each actuator in order to correct the PT errors
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	public float[][] ttOffsetsToActs(List<FloatPoint> actuatorPositions, float imageScale, FloatPoint[] centroidOffsets, Integer[] mirrorConfig)
			throws ComputationException, Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "ttOffsetsToActs"));

		JttOffsetsToActs jttOffsetsToActs = new JttOffsetsToActs();
		RetVal retVal = new RetVal();

		float[] x_act_pos = FloatPointListEncoder.extractXArray(actuatorPositions);
		float[] y_act_pos = FloatPointListEncoder.extractYArray(actuatorPositions);

		float[] x_offsets = FloatPointListEncoder.extractXArray(Arrays.asList(centroidOffsets));
		float[] y_offsets = FloatPointListEncoder.extractYArray(Arrays.asList(centroidOffsets));

		// FIXME: this should be performed in a general function
		
		int[] mirror_config = IntegerListEncoder.convertObjectArrayToPrimitive(mirrorConfig);

		
		// output arrays
		float[] desired_act_deltas = new float[actuatorPositions.size()];
		float[] x_offsets_out = new float[centroidOffsets.length];
		float[] y_offsets_out = new float[centroidOffsets.length];

		Object output[] = jttOffsetsToActs.jttOffsetsToActs(retVal, x_act_pos, y_act_pos, imageScale, x_offsets, y_offsets, mirror_config, x_offsets_out,
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

	/**
	 * Decompose a vector of actuators into pure tilt and pure piston
	 * @param actuatorPositions input array of actuators
	 * @return result object containing 2-d array (36 x 3) of pure piston actuators and 2-d array (36 x 3) of tip/tilt actuators
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public DecomposeActsResult decomposeActs(float[][] actuatorPositions) throws ComputationException, Exception {
		
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

	/**
	 * Given pure tip/tilt actuators, calculate the pure piston actuators that minimize the changes to the sensor readings
	 * @param controlMatrix primary mirror A matrix
	 * @param tipTiltActs pure tip/tilt actuators
	 * @param mirrorConfig logical array indicating active segments
	 * @return pure piston actuators as a 2-d array (36 x 3)
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	public float[][] optimalPistons(float[][] controlMatrix, float[][] tipTiltActs, int[] mirrorConfig) throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "optimalPistons"));
		
		JoptimalPistons joptimalPistons = new JoptimalPistons();
		RetVal retVal = new RetVal();

		float[] ttActs = JavaComputations.flatten2dArray(tipTiltActs, 1);
		float[] testArray = new float[controlMatrix.length];

		// output arrays
		float[] act_p = new float[ttActs.length];


		Object output[] = joptimalPistons.joptimalPistons(retVal, controlMatrix, ttActs, mirrorConfig, testArray, 0, act_p);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("Optimal Pistons Calculation Error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		// store _param values
		float[][] pistonActs = JavaComputations.expandTo2dArray(act_p, 3);

		logger.info(MessageGenerator.generateMessage("computation.success", "optimalPistons"));

		return pistonActs;	

	}
	
	/**
	 * Calculate the pupil registration error in X, Y, rotation and scale (if fractionalIntensityCalcMethod is APS)
	 * @param pupilRegErrorConfig contains input config parameters for fractional intensity calculation method and nStart
	 * @param centroidMap the map of centroids, including each subimage intensity
	 * @param numSpots number of subimages 
	 * @param peripheralSpotPerp array of "Perpendicular" locations of the periperhal subapertures on the primary mirror.
	 * @param peripheralSpotParallel array of "Parallel" locations of the periperhal subapertures on the primary mirror.
	 * @param peripheralSpotTheta array of rotational orientations of the peripheral subapertures
	 * @param aHex segment side length in meters
	 * @param spotDiameter diameter of peripheral subiamges on the primary mirror in meters
	 * @param nspotTypes array of spot types, interior vs peripheral
	 * @param missingSpotFlags array of flags indicating missing spot type (missing from find and identify, use for analysis)
	 * @param findCentStatusList an array of flags indicating if/how this spot was found during find and identify
	 * @return result object containing registration error in x, y and phi and scale error
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
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
	

	/**
	 * Utility method that returns an array of usable spot flags.  A spot is usable if it was a missing spot type 'Use for analysis' and the
	 * findCentStatus for that spot indicates that the spot was found.
	 * @param missingSpotFlags array of flags indicating missing spot type (missing from find and identify, use for analysis)
	 * @param findCentStatusList an array of flags indicating if/how this spot was found during find and identify
	 */
	public int[] goodCentroidsFound(int[] missingSpotFlags, int[] findCentStatusList) {
		int[] found = new int[findCentStatusList.length];

		for (int i=0; i<found.length; i++) {
			boolean isGood = (findCentStatusList[i] == Constants.FIND_CENT_STATUS_SUCCESS || 
					findCentStatusList[i] == Constants.FIND_CENT_STATUS_GAUSS_FIT_FAILED_FALLBACK ||  
					findCentStatusList[i] == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_X ||  
					findCentStatusList[i] == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_Y) && 
					missingSpotFlags[i] == Constants.MISSING_SPOT_TYPE_USE;
			found[i] = isGood ? 1 : 0;
		}

		return found;

	}
	
	/**
	 * Returns an array of flags that indicate if the corresponding centroid offset is 'good'.  Good is defined as being a good spot as 
	 * defined in {@link ComputationLibraryImpl#goodCentroidsFound(int[], int[])} and also that the subimage did not jump.
	 * @param goodSpots array of 'good spots' as defined in {@link ComputationLibraryImpl#goodCentroidsFound(int[], int[])}
	 * @param jumpedSpots array of jumped flags (0 == did not jump)
	 * @return array of good offset flags
	 */
	public int[] goodOffsetsFound(int[] goodSpots, int[] jumpedSpots) {
		int[] validOffsets = new int[goodSpots.length];
		
		for (int i=0; i<goodSpots.length; i++) {
			int notJumped = (jumpedSpots[i] == 0) ? 1 : 0;
			validOffsets[i] = goodSpots[i] & notJumped;
		}
		
		return validOffsets;
	}

	/**
	 * Adds two matricies.  Delegates to {@link JavaComputations#addMatricies(float[][], float[][])}
	 * @param matrix1 input matrix (2-d)
	 * @param matrix2 input matrix (2-d)
	 * @return a 2-d sum of the two matricies
	 * @throws ComputationException if the Java computation fails
	 */
	public float[][] addMatricies(float[][] matrix1, float[][] matrix2) throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "addMatricies"));

		float[][] result = JavaComputations.addMatricies(matrix1, matrix2);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "addMatricies"));

		return result;
	}
	
	/**
	 * Checks the current reference beam map, the current date, number of iterations, CCD temperature and coarse and fine mirror positions against
	 * thresholds set in autoRefMapConfig.  If thresholds are exceeded, a new ref map should be taken and an {@link AutoRefMapCheckException} will
	 * be thrown.  This method delegates to {@link JavaComputations#autoRefMapCheck(AutoRefMapConfig, Point, Point, float, int, Date, RefBeamMap)}
	 * 
	 * @param autoRefMapConfig configuration thresholds to determine if a new reference map needs to be taken
	 * @param currentCoarsePosition current coarse mirror position
	 * @param currentFinePosition current fine mirror position
	 * @param temperature current CCD temperature
	 * @param numIterations number of iterations for the procedure being executed
	 * @param currentDate the current date/time
	 * @param currentRefMap the most current reference beam map for the procedure being executed 
	 * @throws ComputationException if there is a problem in {@link JavaComputations#autoRefMapCheck(AutoRefMapConfig, Point, Point, float, int, Date, RefBeamMap)}
	 * @throws AutoRefMapCheckException thrown if a new reference map needs to be taken
	 */
	public void autoRefMapCheck(AutoRefMapConfig autoRefMapConfig, Point currentCoarsePosition, Point currentFinePosition, float temperature, 
			int numIterations, Date currentDate, RefBeamMap currentRefMap) throws ComputationException, AutoRefMapCheckException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "autoRefMapCheck"));
		
		JavaComputations.autoRefMapCheck(autoRefMapConfig, currentCoarsePosition, currentFinePosition, temperature,  
				numIterations, currentDate, currentRefMap);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "autoRefMapCheck"));
	}

	/**
	 * Checks the magnitude of the telescope move against the passed configuration and lastMove, and determines if the telescope should be commanded
	 * and if a new frame should automatically be taken, or if the user needs to be consulted on a decision. 
	 * This method delegates to {@link JavaComputations#autoCenterTelescopeCheck(AutoCenterTelConfig, FloatPoint, FloatPoint)}
	 * 
	 * @param autoCenterTelConfig configuration thresholds for moving the telescope, re-taking frames, or telescope move too large
	 * @param deltaAzEl the delta Azimuth and Elevation telescope moves
	 * @param lastMove the delta Azimuth and Elevation of the previous telescope move
	 * @return result object with recommendations for moving telescope and re-taking frame, along with text to display if either of these recomendations 
	 * requires user input.
	 */
	public AutoCenterTelCheckResult autoCenterTelescopeCheck(AutoCenterTelConfig autoCenterTelConfig, FloatPoint deltaAzEl, FloatPoint lastMove) {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "autoCenterTelescopeCheck"));
		
		AutoCenterTelCheckResult result = JavaComputations.autoCenterTelescopeCheck(autoCenterTelConfig, deltaAzEl, lastMove);

		logger.info(MessageGenerator.generateMessage("computation.success", "autoCenterTelescopeCheck"));
		
		return result;
	}

	/**
	 * Checks the subimages intensities against the passed threshold and throws a {@link org.tmt.aps.peas.procedure.exception.NonLinearIntensitiesException}
	 * if any subimages exceed the passed threshold.
	 * This method delegates to {@link JavaComputations#autoCenterTelescopeCheck(AutoCenterTelConfig, FloatPoint, FloatPoint)}
	 * @param centroidMap centroid map object containing the array of subimage peak intensities
	 * @param threshold the threshold to test against
	 * @throws NonLinearIntensitiesException when a subimage exceeds the threshold
	 */
	public void checkSubimageIntensities(CentroidMap centroidMap, double threshold) throws NonLinearIntensitiesException, Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "checkSubimageIntensities"));

		JavaComputations.checkSubimageIntensities(centroidMap, threshold);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "checkSubimageIntensities"));
		
	}
	
	/**
	 * Calculates the median value of an array
	 * This method delegates to {@link JavaComputations#autoCenterTelescopeCheck(AutoCenterTelConfig, FloatPoint, FloatPoint)}
	 * @param inputs the array of inputs to calculate the median of
	 * @return the median value
	 */
	public float getMedianValue(float[] inputs) throws Exception {
		logger.info(MessageGenerator.generateMessage("computation.start", "getMedianValue"));
	
		float result = JavaComputations.getMedianValue(inputs);
		
		logger.info(MessageGenerator.generateMessage("computation.success", "getMedianValue"));
		
		return result;
	}

	/**
	 * Calculates the pupil registration commands automatically determining which mechanism to use (fine or coarse).  Automatically 
	 * offloads fine mechanism when thresholds are passed.
	 * 
	 * @param centerPupil if false, return a null result
	 * @param desiredCenterPupilMech desired mechanism to use: fine, coarse or auto determine.  Specifying the fine mechanism is not a guarantee 
	 * that the fine will be used, as this function will offload to the coarse mirror when limits are exceeded.
	 * @param pupilRegErrorResult the pupil registration error to correct
	 * @param pupilRegErrorConfig configuration parameters including large/small thresholds and gain factors for coarse and fine mirrors
	 * @param fineTiltMirror fine tilt mirror configuration parameters including offload thresholds, and current position
	 * @param coarseTiltMirror coarse tilt mirror configuration parameters and current position
	 * @return result object containing coarse and fine mirror commands to send and if the fine mirror is being offloaded.
	 */
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


	/**
	 * Calculate M2 Piston, Tip, Tilt and segment Tip/tilts from Keck Fine Screen centroid offsets using ray trace.
	 * 
	 * @param findCentroidsResult result object containing centroid locations
	 * @param centroidOffsetsResult result object containing centroid offsets
	 * @param subimagesForM2Calc array of flags indicating which subimages to use for M2 calc
	 * @param m2PistonUnitPertibation unit perturbation for M2 piston, to be used for creating Ray Trace model matrix
	 * @param m2TTUnitPertibation unit perturbation for M2 tip/tilt, to be used for creating Ray Trace model matrix
	 * @param fineScreenSpotCoords fine screen spot locations on primary mirror in primary mirror coordinates
	 * @param nspotTypes array of flags indicating if the spot is interior or peripheral
	 * @param telescopeConstants telescope constants data structure containing number of segments, back focal distance, m1 focal length, telescope focal length and m1 curvature radius
	 * @param secPerPixel arcseconds per pixel for the mask used
	 * @param m2TtCorrectionFactor M2 tip/tilt fudge factor, needed because M2 does not rotate about its vertex
	 * @param pupilMaskType pupil mask type data structure containing CCD to cartesian pixel conversion
	 * @return result object containing m2 piston and tip/tilt, centroid residual and m1 offsets corrected for m2
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public CalcM2M1Result calculateM2M1RayTrace(FindCentroidsResult findCentroidsResult, CentroidOffsetsResult centroidOffsetsResult, int[] subimagesForM2Calc,
			float m2PistonUnitPertibation, float m2TTUnitPertibation, FloatPoint[][] fineScreenSpotCoords, int[] nspotTypes, TelescopeConstants telescopeConstants, float secPerPixel,
			float m2TtCorrectionFactor, PupilMaskType pupilMaskType) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calculateM2M1RayTrace"));

		JcalculateM2M1RayTrace jcalculateM2M1RayTrace = new JcalculateM2M1RayTrace();
		RetVal retVal = new RetVal();

		// logger.debug("findCent::  " + guess + ", value = " + frame[(int)guess.x][(int)guess.y]);

		// add one to each guess to acccount for fortran indicies starting at 1, not zero.
		
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
	
	/**
	 * Calculate M2 Piston, Tip, Tilt and segment Tip/tilts from Keck Fine Screen centroid offsets using analytical approximations.
	 * 
	 * @param findCentroidsResult result object containing centroid locations
	 * @param centroidOffsetsResult result object containing centroid offsets
	 * @param subimagesForM2Calc array of flags indicating which subimages to use for M2 calc
	 * @param fineScreenSpotCoords fine screen spot locations on primary mirror in primary mirror coordinates
	 * @param nspotTypes array of flags indicating if the spot is interior or peripheral
	 * @param telescopeConstants telescope constants data structure containing number of segments, back focal distance, m1 focal length, telescope focal length and m1 curvature radius
	 * @param secPerPixel arcseconds per pixel for the mask used
	 * @param m2TtCorrectionFactor M2 tip/tilt fudge factor, needed because M2 does not rotate about its vertex
	 * @param aHex segment side length in m
	 * @param startSegNum segment Number to start M2 calculations at
	 * @param endSegNum segment Number to stop M2 calculations at
	 * @param pupilMaskType pupil mask type data structure containing CCD to cartesian pixel conversion
	 * @return result object containing m2 piston and tip/tilt, centroid residual and m1 offsets corrected for m2
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public CalcM2M1Result calculateM2M1Analytical(FindCentroidsResult findCentroidsResult, CentroidOffsetsResult centroidOffsetsResult, int[] subimagesForM2Calc,
			FloatPoint[][] fineScreenSpotCoords, int[] nspotTypes, TelescopeConstants telescopeConstants, float secPerPixel, float m2TtCorrectionFactor,
			float aHex, int startSegNum, int endSegNum, PupilMaskType pupilMaskType) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "calculateM2M1Analytical"));

		JcalculateM2M1Analytical jcalculateM2M1Analytical = new JcalculateM2M1Analytical();
		RetVal retVal = new RetVal();

		// logger.debug("findCent::  " + guess + ", value = " + frame[(int)guess.x][(int)guess.y]);

		// add one to each guess to acccount for fortran indicies starting at 1, not zero.

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

	/**
	 * Calculate the optimal pistons associated with the calculated actuators (minimizes the changes to the edges). 
	 * Note that this routine just determines the optimal pistons; if you want to add these on to the tip/tilt pistons, 
	 * you need to do it yourself.
	 * 
	 * @param controlMatrix M1 control system A-Matrix
	 * @param tipTiltActs pure tip/tilt actuators
	 * @return result object containing piston actuators, desired actuator deltas, rms values for both plus rms of focus mode and no focus mode
	 */
	@Computation
	public CalcDesiredActCommandsResult calcDesiredActCommands(float[][] controlMatrix, float[][] tipTiltActs, int[] mirrorConfig) throws Exception {
		
		float[][] pistonActs = optimalPistons(controlMatrix, tipTiltActs, mirrorConfig);
		
		// TODO: print out pistonActs

		// calculate RMS of the actuator cmds
		float pistonActsRms = calcRms(pistonActs);

		
		// combine tip/tilt and piston commands
		/*****************************************************/
		/*               calcDesiredActCommands              */
		/*****************************************************/
		float[][] desiredActDeltas = addMatricies(tipTiltActs, pistonActs);

		// calculate RMS of the actuator cmds
		float desiredActDeltasRms = calcRms(desiredActDeltas);
		
		
	
		float[] focusModeVector = calculateFocusModeVector(controlMatrix);
		
		// RMS of the focus mode component of the actuator commands
		float[] desiredActDeltasFlattened= JavaComputations.flatten2dArray(desiredActDeltas, 1);
		float desiredActDeltasFmRms = JavaComputations.getDotProdRms(desiredActDeltasFlattened, focusModeVector);
		
		float desiredActDeltasNoFmRms = (float)Math.sqrt(desiredActDeltasRms * desiredActDeltasRms - desiredActDeltasFmRms * desiredActDeltasFmRms);
		
		return new CalcDesiredActCommandsResult(pistonActs, pistonActsRms, desiredActDeltas, desiredActDeltasRms, desiredActDeltasFmRms, desiredActDeltasNoFmRms);
	}

	/**
	 * Calculate and return a vector of the M1CS actuators corresponding to focus mode, normalized so the RMS = 1
	 * 
	 * @param controlMatrix M1 control matrix
	 * @return vector of M1CS actuators corresponding to focus mode
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
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

	
	/**
	 * Convert the given M2 tip, tilt, and piston errors to the three M2 actuator commands.
	 * 
	 * @param meanM2PistonError input M2 piston error in m
	 * @param meanM2TipTiltError input M2 tip/tilt error in m
	 * @param m2ActuatorRadius M2 actuator radius in m
	 * @return result object containing computed M2 actuator commands in microns
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
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

	/**
	 * Calculates desired actuator deltas EOMs for act deltas, focus mode and no focus mode, given the RMS values over the iterations
	 * 
	 * @param desiredActDeltaRmsIterations array of desired actuator deltas RMS for each iteration
	 * @param desiredActDeltaFmRmsIterations array of focus mode actuator deltas RMS for each iteration
	 * @param desiredActDeltaNoFmRmsIterations array of no focus mode actuator deltas RMS for each iteration
	 * @return result object containing desired actuator deltas EOMs for act deltas RMS, focus mode RMS and no focus mode RMS
	 */
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

	/**
	 * Calculates the mean and EOM for m2 piston errors and m2 tip/tilt errors
	 * @param m2PistonErrors array of m2 piston errors
	 * @param m2TipTiltErrors array of m2 tip tilt errors
	 * @return result object containing the mean and EOM for the m2 piston errors and m2 tip/tilt errors
	 */
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

	/**
	 * Calculates the mean of tip/tilts for M1 segments over a number of iterations
	 * 
	 * @param m1SegmentTipTiltErrors 2-d array of m1 segment tip tilt errors over all segments and all iterations
	 * @return result object containing an array of mean segment tip/tilt errors for all segments
	 */
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

	/**
	 * Calculates the average of centroid offsets for a set of iterations
	 * @param offsetsIterations an array of {@link CentroidOffsetsResult} objects, for each iteration
	 * @param goodSpots good spots array (value == 1 if spot is to be used)
	 * @return a single {@link CentroidOffsetsResult} object containing the averaged centroid offsets 
	 */
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

	/**
	 * Returns the findCentStatus for the averaging of multiple trials.  A spot is only considered good if it was good for every iteration.
	 * @param findCentStatusIterations findCentStatus array for each spot over multiple iterations.
	 * @return an array of good spots that are equal to one if the spot is good for every iteration
	 */
	public int[] calculateAvgFindCentStatus(int[][] findCentStatusIterations) {
		
		// for now, a spot is good only if it was good every time
		int[] avgFindCentStatus = new int[findCentStatusIterations[0].length];
			for (int j=0; j<findCentStatusIterations[0].length; j++) {
				boolean status = true;
				for (int i=0; i<findCentStatusIterations.length; i++) {
				
				// if all iterations are success, then it is a good spot, otherwise not
				 if (findCentStatusIterations[i][j] != Constants.FIND_CENT_STATUS_SUCCESS && 
						 findCentStatusIterations[i][j] != Constants.FIND_CENT_STATUS_GAUSS_FIT_FAILED_FALLBACK &&
						 findCentStatusIterations[i][j] != Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_X &&
						 findCentStatusIterations[i][j] != Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_Y
						 ) status = false;
			}
			avgFindCentStatus[j] =	status ? 1: 0;
		}
		
		return avgFindCentStatus;
	}
	
	/**
	 * Create monochromatic templates for phasing.
	 * @param phasingSubimageFftSize Number of pixels across the subaperture pupil function (ie the size of the FFT)
	 * @param phasingTemplateCount the number of templates to calculate
	 * @param findCentConfig contains irad, iterMax, iMargin and nGauss inputs
	 * @param pupilMask contains plate scale in arcseconds per pixel for the mask, cross hair diameter for the mask and interior spot diameter for the mask
	 * @param filter contains the input filter wavelength
	 * @return result object containing the output template sequence for all piston steps and edge angles. This is a 4-dim array: dim-1/2: X,Y 
	 * should nominally be of size 2*irad+1, dim-3: number of templates to calculate, dim-4: 3 for the three edge angles
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public MakeTemplateResult makeTemplate(int phasingSubimageFftSize, int phasingTemplateCount, FindCentConfig findCentConfig, 
			PupilMask pupilMask, Filter filter, float arcsecPerPixel) throws Exception {
		
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
				Constants.SPOT_TYPE_INTERIOR, findCentConfig.getIrad(), Constants.TEMPLATE_CENTROID_CALC_METHOD_FIND_CENT, arcsecPerPixel, filter.getWavelength() * Constants.NM_TO_MICRONS, 
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
	
	/**
	 * Calculate coherence parameters from a single broadband phasing exposure.
	 * 
	 * @param frame full image array containing subimages to be analyzed
	 * @param findCentroidsResult centroid result object containing the list of centroid locations and found subimage flags
	 * @param edgeAngle edge angles (0, 120, 240 deg) defined for each phasing edge
	 * @param templateArray template sequence of ideal subimages.
	 * @param numberOfSegments the number of segments in the primary mirror
	 * @return result object containing the calculated coherence parameters for all edges in the frame and interpolated best index 
	 * corresponding to the maximum cross correlation coefficient
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
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
	
	/**
	 * Fit the coherence curves to determine the edge heights and do the SVD to determine the pistons.
	 * 
	 * @param edgeAngle array of edge angles defined for each phasing edge (0, 120, or 240 degrees).
	 * @param edgeColor array of three-Color-Mode edge color defined for each phasing edge (1, 2, or 3) 
	 * @param coherenceArraySet coherence parameters for all phasing spots and all exposures
	 * @param stepSize phasing step size
	 * @param numSegments number of primary mirror segments
	 * @param plusPiston used to create the phasing control matrix (nedges x nsegments)
	 * @param minusPiston used to create the phasing control matrix (nedges x nsegments)
	 * @param ringModeCorrectionFactor the magnitude of ring-mode correction to apply to the measured edge heights
	 * @param mirrorConfig logical array indicating active segments
	 * @param filter contains the input sigma of coherence parameter
	 * @param bbPhasingFracInterval the fractional interval size to use for chi-square analysis
	 * @param ringMode array of ring-mode edges with unit rms (where the averaging is over the 78 edges from 7 thru 84).
	 * @param numSteps number of phasing steps
	 * @param useForAnalysis array of flags indicating if a spot can be used for analysis
	 * @param goodSpots array of flags indicating if the spot was found
	 * @return result object containing: measured edge heights after correcting for the dispersion effect of the prisms, 
	 * computed segment piston commands, Predicted residual edge heights that are expected after applying the calculated 
	 * segment piston commands, array of flags specifying which edges had good edge height measurements (0=bad, 1=good), 
	 * number of constrained segments and RMS of segment piston commands, Best fit coherences for each edge (microns),
	 * Mean of the best fit coherences for good edges (microns) 
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public BbAnalyzeSequenceResult bbAnalyzeSequence(int[] edgeAngle, int[] edgeColor, float[][] coherenceArraySet, float stepSize, int numSegments, 
			int[] plusPiston, int[] minusPiston, float ringModeCorrectionFactor, int[] mirrorConfig,  Filter filter, float bbPhasingFracInterval,
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

		float[] bestFitCoherences = new float[numEdges];

		
		Object[] result = jbbAnalyzeSequence.jbbAnalyzeSequence(retVal, coherenceTable, sigmaMicrons, stepSize, bbPhasingFracInterval, asca, edgeAngle, edgeColor, 
				rowFlagIn, ringMode, ringModeCorrectionFactor, mirrorConfig, stepCorr, actCalc, resid, rowFlagOut, bestFitCoherences);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("bbAnalyzeSequence calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		int  constrainedSegmentCount = (Integer)result[0];
		float segmentPistonRms = (Float)result[1]; 
		float meanBestFitCoherence = (Float)result[2];
		
		BbAnalyzeSequenceResult bbAnalyzeSequenceResult = new BbAnalyzeSequenceResult(stepCorr, actCalc, resid, rowFlagIn, rowFlagOut, constrainedSegmentCount, segmentPistonRms,
				bestFitCoherences, meanBestFitCoherence);


		// End of code for findCent unit testing
		logger.info(MessageGenerator.generateMessage("computation.success", "jbbAnalyzeSequence"));

		return bbAnalyzeSequenceResult;
		
	}
	
	/**
	 * Given a vactor of segment pistons remove the best fit plane.  Return the actuator values and RMS of them.
	 * @param actuatorPositions coordinates of the segment actuators 
	 * @param actCalc input segment piston values
	 * @param mirrorConfig logical array of active segments
	 * @return result object containing the pistonRaw values expanded to 3*numSegment values, the actRaw values with the 
	 * best fit plane removed and RMS of the actuator values with the best fit plane removed
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public FixPistonsResult fixPistons(FloatPoint[] actuatorPositions, float[] actCalc, int[] mirrorConfig) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "fixPistons"));

		JfixPistons jfixPistons = new JfixPistons();
		RetVal retVal = new RetVal();

		float[] pistonRaw = actCalc;
		
		float[] actuatorPositionsX = FloatPointListEncoder.extractXArray(Arrays.asList(actuatorPositions));
		float[] actuatorPositionsY = FloatPointListEncoder.extractYArray(Arrays.asList(actuatorPositions));

		float[] actRaw = new float[actuatorPositions.length];
		float[] actFixed = new float[actuatorPositions.length];
		
		
		Object[] result = jfixPistons.jfixPistons(retVal, pistonRaw, actuatorPositionsX, actuatorPositionsY, mirrorConfig, actRaw, actFixed);

		
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
	
	/**
	 * Calculates the desired actuator commands given a {@link FixPistonsResult}.  The desired actuator deltas are the values of the 
	 * actuators with best fit plane removed with the sign reversed and converted from microns to nanometers.
	 * @param fixPistonsResult the input fix pistons result 
	 * @return result object containing the desired actuator deltas and the RMS of the desired actuator deltas
	 */
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
	
	/**
	 * 
	 * @param rowFlagIn array of flags specifying which edges to use in the Broadband Phasing calculation.
	 * @param rowFlagOut array of flags specifying which edges had good edge height measurements (0=bad, 1=good).
	 * @param stepCorr measured edge heights after correcting for the dispersion effect of the prisms
	 * @param stepResid predicted residual edge heights that are expected after applying the calculated segment piston commands
	 * @return result object containing max and rss values for measured edge heights and residual edge heights
	 */
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
	
	/**
	 * Calculate the segment color steps for each color step for phasing
	 * @param stepCount number of desired steps
	 * @param stepSizeMicrons size of each step (surface)
	 * @return result object containing the color step array: A 2-dimensional array where the first dimension is the color step.
	 * There are n+1, steps as the last step restores the mirror back to it's initial state. The 2nd dimension is 3 long and 
	 * describes the step for that color segment.
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
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
	
	/**
	 * Given the desired segment color steps return a vector of commands to send to ACS/M1CS.
	 * @param colors step size for each segment color
	 * @param segmentColors the color of each segment
	 * @return result object containing the actuator commands to send to M1
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
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
	
	/**
	 * Calculates the SUFS segment centroids for each segment in the SUFS group
	 * @param findCentroidsResult the centroids found in mask coordinates/numbering
	 * @param sufsGroupSegmentToMask mapping of sufs group segment spot number to mask spot number
	 * @return a result object containing an array of 7 {@link FindCentroidsResult} objects, for each segment with segment specific ordering.
	 */
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
	
	/**
	 * Reorders any input integer array that corresonds to mask spot ordering into a 2-d array of segment ordered spot arrays.  The first index
	 * is the number of SUFS segments in the group (7) and the second index is the segment spot number.
	 * @param input the input integer array in mask order
	 * @param sufsGroupSegmentToMask mapping of sufs group segment spot number to mask spot number
	 * @return the 2-d array of sufs segment spot numbered values
	 */
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
	
	/**
	 * Reorders any input FloatPoint array that corresonds to mask spot ordering into a 2-d array of segment ordered spot arrays.  The first index
	 * is the number of SUFS segments in the group (7) and the second index is the segment spot number.
	 * @param input the input FloatPoint array in mask order
	 * @param sufsGroupSegmentToMask mapping of sufs group segment spot number to mask spot number
	 * @return the 2-d array of sufs segment spot numbered values
	 */
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
	
	/**
	 * Calculates SUFS centroid offsets for each of the segments using segment numbering and coordinates.  Also determines jumped spots.
	 * 
	 * @param findCentroidsResult mask coordinate and numbered centroids
	 * @param refMapCentroidsResult mask coordinate and numbered reference map centroids
	 * @param centroidOffsetsConfig input configuration for centroid offsets, passed to {@link #calcCentroidOffsets}, which is called for 
	 * each segment
	 * @param pupilMaskType passed to {@link #calcCentroidOffsets}, which is called for each segment
	 * @param nspotTypes array of spot types (interior vs peripheral) ordered by mask numbering
	 * @param missingSpotFlags array of missing spot flags ordered by mask numbering
	 * @param sufsGroupSegmentToMask mapping of sufs group segment spot number to mask spot number
	 * @param spotJumpedThreshold threshold in pixels to determine if a spot has jumped
	 * @return result object containing the same results as {@link #calcCentroidOffsets} but for each segment, plus 2-d arrays (per segment, per spot)
	 * of jumped spot flags and valid offsets flags
	 */
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
	
	/**
	 * SUFS calculation of centroid statistics.  Calls {@link #calcCentroidStats(FloatPoint[], int[], int[])} for each of the segments
	 * 
	 * @param sufsSegmentOffsetsResult SUFS segment offsets result object containing offsets for each segment
	 * @param findCentStatusList findCent status flags ordered by mask numbering
	 * @param nspotTypes spot types (interior vs peripheral) flags ordered by mask numbering
	 * @param missingSpotFlags missing spots flags ordered by mask numbering
	 * @param sufsGroupSegmentToMask mapping of sufs group segment spot number to mask spot number, used to reorder mask numbered arrays into
	 * seven segment spot numbered arrays
	 * @return result object containing the exact information returned from {@link #calcCentroidStats(FloatPoint[], int[], int[])}, but for each segment
	 * in the SUFS group
	 */
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
	
	/**
	 * Calculates the SUFS centroid statistics for the averages of the segment offsets results, by calling {@link #calcCentroidStats(FloatPoint[], int[], int[])}
	 * for the average offsets for each segment
	 * 
	 * @param sufsAvgSegmentOffsetsResult average segment offsets for each segment in the SUFS group
	 * @param avgGoodSpotMask averaging all good spots in mask numbering, a spot is good if it was good over all iterations
	 * @param nspotTypes spot types (interior vs peripheral) flags ordered by mask numbering
	 * @param sufsGroupSegmentToMask mapping of sufs group segment spot number to mask spot number, used to reorder mask numbered arrays into
	 * seven segment spot numbered arrays
	 * @return result object containing the exact information returned from {@link #calcCentroidStats(FloatPoint[], int[], int[])}, but for the
	 * average of each centroid offset and for each segment in the SUFS group
	 */
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
	
	/**
	 * Calculates the average SUFS centroid offsets given the centroid offsets for a set of iterations.
	 * 
	 * @param sufsSegmentOffsetsResultIterations an array of iterations of SUFS segment offsets results
	 * @return result object containing the average of the centroid offsets for each segment
	 */
	@Computation
	public SufsSegmentOffsetsResult calcAvgSufsCentroidOffsets(SufsSegmentOffsetsResult[] sufsSegmentOffsetsResultIterations) throws Exception {

		logger.info(MessageGenerator.generateMessage("computation.start", "calcAvgSufsCentroidOffsets"));

		
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

		List<FloatPoint> theoreticalOffsetList = FloatPointListEncoder.constructFromXandY(theoreticalOffsetsX, theoreticalOffsetsY);
		FloatPoint[] theoreticalOffsets = theoreticalOffsetList.toArray(new FloatPoint[0]);
		return new SufsZernikeResult(bestFitZernikes, theoreticalOffsets, whFactor);
		
	}
	
	/**
	 * Calculates SUFS Zernikes for each segment in the SUFS group.
	 * 
	 * @param sufsSegmentIdealSpotLocations ideal subaperature locations for a segment
	 * @param segmentOffsets centroid offsets for each spot of each of the seven SUFS group segments
	 * @param aHex hexagon side length in meters
	 * @param secPerPixel converts pixels to arcseconds
	 * @param validOffsets flag specifying which subapertures per segment to use in the Zernike calculation
	 * @param sufsGroupSegmentToMask mapping of sufs group segment spot number to mask spot number, used to reorder mask numbered arrays into
	 * seven segment spot numbered arrays
	 * @param sufsZernikeOrder array of zernike orders to calculate ordered by segment number
	 * @param groupSegmentNumbers array of segment numbers for this SUFS group
	 * @return result object containing the following zernike related information for each segment: the best-fit Zernike coefficients computed 
	 * from the given centroid offsets, The fraction of the RMS-squared centroid offsets (i.e. power) that is represented by the fitted Zernike 
	 * coefficients and the theoretical centroid offsets corresponding to the fitted Zernike coefficients.
	 */
	@Computation
	public SufsSegmentZernikeResult calculateSufsZernikes(FloatPoint[] sufsSegmentIdealSpotLocations, FloatPoint[][] segmentOffsets, float aHex, float secPerPixel,
			int[][] validOffsets, int[][] sufsGroupSegmentToMask, int[] sufsZernikeOrder,
			int[] groupSegmentNumbers) throws Exception {

		logger.info(MessageGenerator.generateMessage("computation.start", "calculateSufsZernikes"));
		
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

	/**
	 * Calculates SUFS Zernike statistics given the {@link SufsSegmentZernikeResult} over a set of iterations.  Calculates the 
	 * zernike means and EOMs for each segment.
	 * 
	 * @param sufsSegmentZernikeResultIterations an array of zernike results over a set of iterations
	 * @return a result object containing zernike means and EOMs for all zernikes calculated for all segments in the SUFS group
	 */
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

	/**
	 * Given the X and Y coarse mirror motions that are about to be sent to the instrument, calculate the desired telescope commands
	 * to keep the telescope centered.  These are calculated as approx using on-sky data.to keep the telescope centered.  
	 * 
	 * @param coarseMirrorOffsets The x,y motion about to be applied to the coarse mirror in microns
	 * @param telPerCoarseMotion Telescope motion (in arcsecs) for a 1 micron coarse mirror move
	 * 
	 * @return Required telescope motion (az,el) in arcseconds
	 */
	public FloatPoint coarseOffsetsToTelMoves(Point coarseMirrorOffsets, float telPerCoarseMotion) {

		float sin30 = 0.5f;
		float cos30 = 0.866025404f;
		
		float az = -1.0f * telPerCoarseMotion * (sin30 * coarseMirrorOffsets.x - cos30 * coarseMirrorOffsets.y);
		float el = -1.0f * telPerCoarseMotion * (cos30 * coarseMirrorOffsets.x + sin30 * coarseMirrorOffsets.y);

		return new FloatPoint(az, el);
	}
	
	
	//**************************************//
	//   Narrow Band Phasing Computations   //
	//**************************************//

	/**
	 * Single frame analysis for NB phasing
	 * 
	 * 
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public NbAnalyzeFrameResult nbAnalyzeFrame(float[][] frame, FloatPoint[] centroids, int[] goodSpots, int[] edgeAngle,
			float[][][][] template, int numberOfSegments) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "nbAnalyzeFrame"));

		JnbAnalyzeFrame jnbAnalyzeFrame = new JnbAnalyzeFrame();
		RetVal retVal = new RetVal();

		int edgeCount = edgeAngle.length;
		
		
		List<FloatPoint> edgeCentroidList = Arrays.asList(centroids).subList(numberOfSegments, numberOfSegments+edgeCount);
		
		float[] xCentroids = FloatPointListEncoder.extractXArray(edgeCentroidList);
		float[] yCentroids = FloatPointListEncoder.extractYArray(edgeCentroidList);
		
		int[] goodEdgeSpots = Arrays.copyOfRange(goodSpots, numberOfSegments, numberOfSegments+edgeCount);

		
	    float[] coherenceOut = new float[edgeCount];
	    float[] bestCorrelationIndex = new float[edgeCount];
	    float[] aFit = new float[edgeCount];
	    float[] bFit  = new float[edgeCount];
	    float[] phiFit  = new float[edgeCount];
	    float[] chisqF = new float[edgeCount];

	
		Object[] result = jnbAnalyzeFrame.jnbAnalyzeFrame(retVal, frame, xCentroids, yCentroids, goodEdgeSpots, edgeAngle, template, 
	            coherenceOut, bestCorrelationIndex, aFit, bFit, phiFit, chisqF);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("nbAnalyzeFrame calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		NbAnalyzeFrameResult nbAnalyzeFrameResult = new NbAnalyzeFrameResult(coherenceOut, bestCorrelationIndex, aFit, bFit, phiFit, chisqF);

		
		logger.info(MessageGenerator.generateMessage("computation.success", "nbAnalyzeFrame"));

		return nbAnalyzeFrameResult;
		
	}
    
	/**
	 * Take the data from the individual phasing frames and extract the phases (whether one-shot or several exposure steps).
	 * 
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public NbAnalyzeStepSequenceResult nbAnalyzeStepSequence(float[] bestCorrelationIndex, float[] coherenceOut, float xlambda0,
			int[] missingSpotFlags, int[] findCentStatusList, int[] edgeColor, int templateCount, int numberOfSegments, 
			float nbSingleFilterCoherenceThreshold) throws Exception {
		
	
		logger.info(MessageGenerator.generateMessage("computation.start", "nbAnalyzeStepSequence"));

		JnbAnalyzeStepSequence jnbAnalyzeStepSequence = new JnbAnalyzeStepSequence();
		RetVal retVal = new RetVal();

		
		int edgeCount = edgeColor.length;

		// spots that can be used (found without errors and should be used for analysis)
		int[] goodSpots = goodCentroidsFound(missingSpotFlags, findCentStatusList);

		int[] goodEdgeSpots = Arrays.copyOfRange(goodSpots, numberOfSegments, numberOfSegments+edgeCount);


		float[][] nbTableT = new float[1][edgeCount];
		nbTableT[0] = bestCorrelationIndex;
		float[][] nbTable = JavaComputations.transpose2dArray(nbTableT);
		
		float[][] corrTableT = new float[1][edgeCount];
		corrTableT[0] = coherenceOut;
		float[][] corrTable = JavaComputations.transpose2dArray(corrTableT);
		
		
		int[] rowFlagOut = new int[edgeCount];
		float[] stepTable = new float[edgeCount];
		float[][][] indexTable = new float[edgeCount][1][2];

		Object[] result = jnbAnalyzeStepSequence.jnbAnalyzeStepSequence(retVal, nbTable, corrTable, xlambda0,
				goodEdgeSpots, edgeColor, templateCount, nbSingleFilterCoherenceThreshold, rowFlagOut, stepTable, indexTable);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("nbAnalyzeStepSequence calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		float edgeErrorSteps = (Float)result[0];
		float edgeErrorMicrons = (Float)result[1];
		float lineSlopeAvg = (Float)result[2];
		
		
		NbAnalyzeStepSequenceResult nbAnalyzeStepSequenceResult = new NbAnalyzeStepSequenceResult(rowFlagOut, stepTable, indexTable, 
				edgeErrorSteps, edgeErrorMicrons, lineSlopeAvg);

		logger.info(MessageGenerator.generateMessage("computation.success", "nbAnalyzeStepSequence"));

		return nbAnalyzeStepSequenceResult;
		
	}
    
	/**
	 * This routine combines the results of the individual filter measurements
	 * (or sequences of the various filter measurements) to extract the edge 
	 * heights.  It must be called even if only a single filter is used.
	 * CHI2_NM(I) is only calculated when there are two or more filters. 
	 * Otherwise it is 0.
	 * 
	 * what is the difference between stepTable and nbTable from the analyzeStepSequence function?
	 * 
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public NbAnalyzeFilterSequenceResult nbAnalyzeFilterSequence(int[][] rowFlagIn, float[][] stepTable, float[] filterWavelengthMicrons,
			float range, float rInt) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "nbAnalyzeFilterSequence"));

		JnbAnalyzeFilterSequence jnbAnalyzeFilterSequence = new JnbAnalyzeFilterSequence();
		RetVal retVal = new RetVal();

		
		// TODO: invert corrTable, stepTable
		
		float[][] stepTableT = JavaComputations.transpose2dArray(stepTable);
		int[][] rowFlagInT = JavaComputations.transpose2dArray(rowFlagIn);


		int numEdges = rowFlagInT.length;
		int numFilters = rowFlagInT[0].length;

		float[] chi2nm = new float[numEdges];
		float[] nbStep = new float[numEdges];
		int[] rowFlagOut = new int[numEdges];
		float[][] nbStepBestFit = new float[numEdges][numFilters];

		Object[] result = jnbAnalyzeFilterSequence.jnbAnalyzeFilterSequence(retVal, rowFlagInT, stepTableT, filterWavelengthMicrons, range, 
				rInt, chi2nm, nbStep, rowFlagOut, nbStepBestFit);
	            

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("nbAnalyzeFilterSequence calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}

		NbAnalyzeFilterSequenceResult nbAnalyzeFilterSequenceResult = new NbAnalyzeFilterSequenceResult(chi2nm, nbStep, rowFlagOut, nbStepBestFit);


		logger.info(MessageGenerator.generateMessage("computation.success", "nbAnalyzeFilterSequence"));

		return nbAnalyzeFilterSequenceResult;
		
	}   
    	
	/**
	 * Given the measured NB edge steps and the flags that indicate whether they 
	 * are good or not, solve for actuators (pistons), and residuals (predicted 
	 * steps).
	 * 
	 * @throws ComputationException if the Fortran routine returns an error code
	 */
	@Computation
	public NbActuatorsResult nbActuators(float[] nbStep, int[] rowFlag, int[] colFlag,
			int[] plusPiston, int[] minusPiston, int numSegments, int[] mirrorConfig) throws Exception {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "nbActuators"));

		JnbActuators jnbActuators = new JnbActuators();
		RetVal retVal = new RetVal();


		int numEdges = nbStep.length;
		
		float[] actCalc = new float[numSegments];
		float[] resid = new float[numEdges];
		
		float[][] acsa = JavaComputations.generatePhasingInteractionMatrix(numEdges, numSegments, plusPiston, minusPiston);
		
		Object[] result = jnbActuators.jnbActuators(retVal, nbStep, rowFlag, colFlag, acsa, mirrorConfig, actCalc,  resid);

		
		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("nbActuators calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}
		
		
		

		int constrainedSegmentCount = (Integer)result[0];
		float segmentPistonRms = (Float)result[1];
		
		
		NbActuatorsResult nbActuatorsResult = new NbActuatorsResult(actCalc, resid, constrainedSegmentCount, segmentPistonRms);


		logger.info(MessageGenerator.generateMessage("computation.success", "nbActuators"));

		return nbActuatorsResult;
		
	}    
    

	/**
	 * 
	 * determineMissingSegmentSubimgages
	 * 
	 * @param subaperatureLocations x,y locations of all subaperatures in m at M1
	 * @param segmentCenters x,y locations of all segment centers in m at M1
	 * @param aHex hexagon side length in m at M1
	 * @param segmentList boolean array numbered according to segment number: true if segment is present, false otherwise
	 * 
	 * @return boolean array of length subaperature count: true if subaperature is present, false otherwise
	 */
	public boolean[] determineMissingSegmentSubaperatures(FloatPoint[] subaperatureLocations, FloatPoint[] segmentCenters, float aHex, Integer[] segmentList) 
			throws ComputationException {
		
		return JavaComputations.determineMissingSegmentSubaperatures(subaperatureLocations, segmentCenters, aHex, segmentList);
	}

	/**
	 * 
	 * This method returns subimages that are missing for analysis only due to an incomplete mirror.  These are edge subimages
	 * for which there should be two adjoining segments, but one is missing.  Note: if both segments sharing an edge are missing
	 * then the spot will already be accounted for in F&I missing spots
	 * 
	 * @param subaperatureLocations x,y locations of all subaperatures in m at M1
	 * @param segmentCenters x,y locations of all segment centers in m at M1
	 * @param aHex hexagon side length in m at M1
	 * @param segmentList boolean array numbered according to segment number: true if segment is present, false otherwise
	 * 
	 * @return boolean array of length subaperature count: true if subaperature is present, false otherwise
	 */
	public boolean[] determineMissingSegmentAnalysisSubimages(FloatPoint[] subaperatureLocations, FloatPoint[] segmentCenters, float aHex, Integer[] segmentList) 
		throws ComputationException {
		
		return JavaComputations.determineMissingSegmentAnalysisSubimages(subaperatureLocations, segmentCenters, aHex, segmentList);
	}

    
	/**
	 * @author gchanan 7/13/2016 Original Version
	 * @author cohara  9/05/2016 Port to PEAS
	 * @author smichaels 1/6/17 Rewritten in Java
	 * 
	 * Given subaperture centers (XAP,YAP) and segment centers (XSEG,YSEG), 
	 * determine if the subaperture lies within the hexagonal segment or not.
	 * 
	 * Determine whether the subaperture center (XAP, YAP) lies in a
	 * hexagon with center at (XSEG, YSEG).  The hexagon side length is AHEX.  
	 * No particular units are assumed, but they must be consistent.  If the 
	 * subaperture center does lie in the hexagon, then true will be returned; 
	 * otherwise false. 
	 * Note ahex may be oversized for subaps on the boundary of a segment.
	 * 
	 * @param aperaturePos The coordinates of the given subaperture center.
	 * @param segCenter The coordinates of a hexagonal segment center
	 * @param ahex Hexagon side length
	 * 
	 * @return true if subaperature center lies within the hexagonal segment, false otherwise
	 * 
	 */	
	public boolean doesSubapLieInSeg(FloatPoint aperaturePos, FloatPoint segCenter, float ahex) {
		return JavaComputations.doesSubapLieInSeg(aperaturePos, segCenter, ahex);
	}
	
	/**
	 * Generates an a-Matrix for incomplete mirror configurations given the complete mirror a-matrix and incomplete mirror config
	 * @param aMatrix
	 * @param mirrorConfig boolean array numbered according to segment number: true if segment is present, false otherwise
	 * @return a modified control matrix where elements corresponding to missing segments are set to zero
	 */
	public float[][] generateIncompleteMirrorAMatrix(float[][] aMatrix, Integer[] mirrorConfig) {
		return JavaComputations.generateIncompleteMirrorAMatrix(aMatrix, mirrorConfig);
	}

	/**
	 * Given the segment raw piston values (not the plane-removed actuators), calculate the x and y terrace mode components in piston space 
	 * and in plane-removed actuator space.
	 * NOTE:  Unit TM is defined to be 1 in actuator space; It will be 7.6 in piston space.
	 * 
	 * @param primaryActPos coordinates of the M1 actuators
	 * @param actCalc Segment raw pistons (not plane removed)
	 * @param mirrorListInt list of mirrors present (1=present, 0=missing)
	 * @return Terrace Mode components in piston space and actuator space
	 */
	@Computation
	public TerraceModeComponentsResult terraceModeComponents(FloatPoint[] primaryActPos, float[] actCalc, int[] mirrorListInt) throws ComputationException {
		
		logger.info(MessageGenerator.generateMessage("computation.start", "terraceModeComponents"));

		JterraceModeComponents jterraceModeComponents = new JterraceModeComponents();
		RetVal retVal = new RetVal();
		
		
		float[] xact = FloatPointListEncoder.extractXArray(Arrays.asList(primaryActPos));
		float[] yact = FloatPointListEncoder.extractYArray(Arrays.asList(primaryActPos));
		
		Object[] result = jterraceModeComponents.jterraceModeComponents(retVal, xact, yact, actCalc, mirrorListInt);

		if (retVal.getCode() > 0) {
			statusLogger.log(retVal);
			throw new ComputationException("terraceModeComponents calcuation error.  " + MessageGenerator.generateErrorMessage(retVal) + ".  ");
		}
		
		float xTerracePiston = (Float)result[0];
		float yTerracePiston = (Float)result[1];
		float xTerraceActuator = (Float)result[2];
		float yTerraceActuator = (Float)result[3];
		
		TerraceModeComponentsResult terraceModeComponentsResult = new TerraceModeComponentsResult(new FloatPoint(xTerracePiston, yTerracePiston), new FloatPoint(xTerraceActuator, yTerraceActuator));

		logger.info(MessageGenerator.generateMessage("computation.success", "terraceModeComponents"));

		return terraceModeComponentsResult;
	}
	
	
	/**
	 * Generates startup values that depend upon the procedure, ccd, pupil mask and other factors
	 * 
	 * @param arcsecPerMeter
	 * @param pixelSize
	 * @return value containing arcsecPerPixel 
	 */
	@Computation
	public StartupComputationsResult startupComputations(float arcsecPerMeter, float pixelSize) {
		float arcsecPerPixel = JavaComputations.calcArcSecPerPixel(arcsecPerMeter, pixelSize);
		return new StartupComputationsResult(arcsecPerPixel);
	}
	
}


