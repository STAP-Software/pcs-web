/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.computation.business;

import java.util.Date;
import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.Rect;
import org.tmt.aps.peas.computation.java.AutoRefMapCheckException;
import org.tmt.aps.peas.computation.model.AutoCenterTelCheckResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CentroidStatsResult;
import org.tmt.aps.peas.computation.model.DecomposeActsResult;
import org.tmt.aps.peas.computation.model.FIResult;
import org.tmt.aps.peas.computation.model.ScaleError;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.AutoRefMapConfig;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfig;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.procedure.exception.AbortProcedureException;
import org.tmt.aps.peas.procedure.exception.UserAssistRequiredException;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;


public interface ComputationLibrary {

	public float actuatorLengths(float a, float b) throws ComputationException;
	
	public FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel);  // local java routine
	
	public float calcRms(float[][] data);
	
	public FloatPoint findCent(float[][] frame, FloatPoint guess, FindCentConfig findCentConfig, int spotType) throws ComputationException;

	public List<FloatPoint> findCentroids(float[][] frame, FIResult fiResult, FindCentConfig findCentConfig) throws ComputationException;
	
	public int[][] removeBadPixels(int[][] frame, List<Rect> badPixelList) throws ComputationException;
	
	public FIResult findAndIdentify(float[][] frame, int numSpots, FIConfig fiConfig, RefBeamMap currentRefMap, RefBeamMap refDefMap) throws ComputationException;
	
	public void evalFiResult(FIResult fiResult, FIConfig fiConfig, ProcedureConfig procedureConfig) throws UserAssistRequiredException, AbortProcedureException;

	public CentroidOffsetsResult  calculateCentroidOffsets(List<FloatPoint> centroids, List<FloatPoint> refMapCentroids, 
			CentroidOffsetsConfig centroidOffsetsConfig ) throws ComputationException;
	
	
	public CentroidStatsResult calculateCentroidStats(List<FloatPoint> centroidOffsets) throws ComputationException;
	
	public ScaleError passiveTiltScaleError(List<FloatPoint> centroidOffsets, RefBeamMap refDefMap) throws ComputationException;
	
	public float[][] ttOffsetsToActs(List<FloatPoint> actuatorPositions, float imageScale, List<FloatPoint> centroidOffsets) throws ComputationException;
	
	
	public DecomposeActsResult decomposeActs(float[][] actuatorPositions) throws ComputationException;
	
	public float[][] optimalPistons(float[][] controlMatrix, float[][] tipTiltActs) throws ComputationException;
	
	public float[][] addMatricies(float[][] matrix1, float[][] matrix2) throws ComputationException;

	public void autoRefMapCheck(AutoRefMapConfig autoRefMapConfig, Point currentPosition, float temperature, int numIterations,
			Date currentDate, RefBeamMap currentRefMap) throws ComputationException, AutoRefMapCheckException;
	
	public AutoCenterTelCheckResult autoCenterTelescopeCheck(AutoCenterTelConfig autoCenterTelConfig, FloatPoint deltaAzEl, FloatPoint lastMove);

}
