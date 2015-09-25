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
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.java.JavaComputations;
import org.tmt.aps.peas.computation.model.AutoCenterTelCheckResult;
import org.tmt.aps.peas.computation.model.CalcM2M1Result;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.computation.model.ScaleError;
import org.tmt.aps.peas.computation.model.Subimage;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.AutoRefMapConfig;
import org.tmt.aps.peas.config.model.CalcM2M1Config;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfig;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.PupilRegErrorConfig;
import org.tmt.aps.peas.config.model.SubimageDef;
import org.tmt.aps.peas.config.model.TelescopeConstants;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.lang.interop.RetVal;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.exception.UserAssistRequiredException;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
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

	


	
	
	
	
	
	
	public FIResult findAndIdentify(float[][] frame, int numSpots, FIConfig fiConfig, RefBeamMap currentRefMap,
			List<FloatPoint> refDefCentroids, int[] missingSpotFlags, boolean isRefMap) throws ComputationException {
		
		RetVal retVal = new RetVal();
		
		float[][] centroids = new float[numSpots][2];
		
		
		int nsp = -1; // segment number of current group
		int ngp = -1; // sufs group number
		float frame_avg = 0.0f; // average background of frame (TODO) from backgroundStats
		float frame_sigma = 20.0f; // frame background sigma (TODO) from backgroundStats
		
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
	
	public float calcRms(float[][] data) {
		return JavaComputations.calcRms(data);
	}


	@Override
	public Subimage findCent(float[][] frame, FloatPoint guess, FindCentConfig findCentConfig, int nspotType) throws ComputationException {
		// TODO Auto-generated method stub
		logger.debug("findCentConfig = " + findCentConfig);
		return new Subimage(new FloatPoint(guess.x - 10.0f, guess.y - 10.0f), 0.0f, 0.0f, 0);
	}
	
	public FindCentroidsResult findCentroids(float[][] frame, FIResult fiResult, FindCentConfig findCentConfigInterior,  FindCentConfig findCentConfigPeripheral, int[] nspotTypes,
			int[] missingSpotFlags, boolean isRefMap) throws ComputationException {

		
		return null;
		
	}

	public int[][] removeBadPixels(int[][] frame, List<Rect> badPixelList) throws ComputationException {
		
		return frame;
		
	}
	
	public void evalFiResult(FIResult fiResult, FIConfig fiConfig, ProcedureConfig procedureConfig) throws UserAssistRequiredException, AbortProcedureException {
		
		
	}

	@Override
	public CentroidOffsetsResult calculateCentroidOffsets(List<FloatPoint> centroids, List<FloatPoint> refMapCentroids,
			CentroidOffsetsConfig centroidOffsetsConfig, PupilMaskType pupilMaskType, int[] nspotTypes, int[] missingSpotFlags,
			int[] findCentStatusList) throws ComputationException {
		
		
		List<FloatPoint> offsets = new ArrayList<FloatPoint>();
		for (int i=0; i< centroids.size(); i++) {
			FloatPoint centroid = centroids.get(i);
			FloatPoint refMapCentroid = refMapCentroids.get(i);
			
			FloatPoint offset = centroid.subtract(refMapCentroid);
			
			offsets.add(offset);
		}
		
		return new CentroidOffsetsResult(FloatPointListEncoder.convertToNby2Array(offsets), FloatPointListEncoder.convertToNby2Array(offsets), new FloatPoint(1.0f, 2.0f), 1.1f, 0.1f  );
		
	}
	
	
	@Override
	public PupilRegErrorResult calculatePupilRegError(PupilRegErrorConfig pupilRegErrorConfig, CentroidMap centroidMap, int numSpots,
			float[] peripheralSpotPerp, float[] peripheralSpotParallel, float[] peripheralSpotTheta, float aHex, float spotDiameter,
			int[] nspotTypes, int[] missingSpotFlags, int[] findCentStatusList) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public CentroidStatsResult calculateCentroidStats(List<FloatPoint> centroidOffsets, int[] nspotTypes, int[] missingSpotFlags,
			int[] findCentStatusList) throws ComputationException {
		// TODO Auto-generated method stub
		return new CentroidStatsResult(23, 1.2f, 0.42f, 0.8f, 0.5f);
	}

	@Override
	public ScaleError passiveTiltScaleError(List<FloatPoint> centroidOffsets, List<FloatPoint> centerSpot) {
		// TODO Auto-generated method stub
		return new ScaleError(1.1f, 2.2f);
	}

	@Override
	public float[][] ttOffsetsToActs(List<FloatPoint> actuatorPositions, float imageScale, List<FloatPoint> centroidOffsets)
			throws ComputationException {
		// TODO Auto-generated method stub
		return new float[36][3];
	}

	@Override
	public DecomposeActsResult decomposeActs(float[][] actuatorPositions) throws ComputationException {
		// TODO Auto-generated method stub
		return new DecomposeActsResult(actuatorPositions, actuatorPositions);
	}

	@Override
	public float[][] optimalPistons(float[][] controlMatrix, float[][] tipTiltActs) throws ComputationException {
		// TODO Auto-generated method stub
		return tipTiltActs;
	}

	@Override
	public float[][] addMatricies(float[][] matrix1, float[][] matrix2) throws ComputationException {
		return JavaComputations.addMatricies(matrix1, matrix2);
	}

	public void autoRefMapCheck(AutoRefMapConfig autoRefMapConfig, Point currentCoarsePosition, Point currentFinePosition, float temperature, 
			int numIterations, Date currentDate, RefBeamMap currentRefMap) throws ComputationException, AutoRefMapCheckException {
		
		JavaComputations.autoRefMapCheck(autoRefMapConfig, currentCoarsePosition, currentFinePosition, temperature,  
				numIterations, currentDate, currentRefMap);
	}

	public AutoCenterTelCheckResult autoCenterTelescopeCheck(AutoCenterTelConfig autoCenterTelConfig, FloatPoint deltaAzEl, FloatPoint lastMove) {
		return JavaComputations.autoCenterTelescopeCheck(autoCenterTelConfig, deltaAzEl, lastMove);
	}

	public void checkSubimageIntensities(CentroidMap centroidMap, double threshold) throws Exception {
		JavaComputations.checkSubimageIntensities(centroidMap, threshold);
	}

	public float getMedianValue(float[] inputs) throws Exception {
	
		return JavaComputations.getMedianValue(inputs);
		
	}


	@Override
	public CalcPrCommandsResult calcPrCommands(boolean centerPupil, int desiredCenterPupilMech, PupilRegErrorResult pupilRegErrorResult,
			PupilRegErrorConfig pupilRegErrorConfig, FineTiltMirror fineTiltMirror, CoarseTiltMirror coarseTiltMirror)
					throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScaleError fineScreenScaleError(List<FloatPoint> centroidOffsets, List<FloatPoint> centerSpots, int[] nspotTypes,
			int[] missingSpotFlags, int[] findCentStatusList) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point calcCoarseMirrorCmds(FloatPoint desiredMotion, FloatPoint leverCoarse, float oraFactor) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CalcM2M1Result calculateM2M1RayTrace(FindCentroidsResult findCentroidsResult,  CentroidOffsetsResult centroidOffsetsResult, int[] subimagesForM2Calc,
			CalcM2M1Config calcM2M1Config, FloatPoint[][] fineScreenSpotCoords, TelescopeConstants telescopeConstants) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
