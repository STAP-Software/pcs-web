package org.tmt.aps.peas.computation.java;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.jboss.logging.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.TriState;
import org.tmt.aps.peas.computation.business.ComputationException;
import org.tmt.aps.peas.computation.model.AutoCenterTelCheckResult;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CorrectOverscanDarkResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.AutoRefMapConfig;
import org.tmt.aps.peas.config.model.PupilRegErrorConfig;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.CcdState;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.procedure.exception.NonLinearIntensitiesException;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

/**
 * Computation implementation using Java.  This class implements functions that are called by ComputationLibraryImpl that are implemented in Java rather than FORTRAN.
 * @author smichaels
 */
public class JavaComputations {

	static Logger logger = Logger.getLogger(JavaComputations.class);

	public static FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {

		FloatPoint deltaPix = new FloatPoint(measuredPix.x - desiredPix.x, measuredPix.y - desiredPix.y);

		logger.debug("deltaPix = " + deltaPix + ", secPerPixel = " + secPerPixel);

		// Convert offsets to arc sec.
		float deltaEl = (float) ((0.866 * deltaPix.x + 0.500 * deltaPix.y) * secPerPixel);
		float deltaAz = (float) ((0.500 * deltaPix.x - 0.866 * deltaPix.y) * secPerPixel);

		FloatPoint deltaAzEl = new FloatPoint(deltaAz, deltaEl);

		return deltaAzEl;
	}

	public static float calcRms(float[][] data) {
		double sum2 = 0.0;
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				sum2 += data[i][j] * data[i][j];
			}
		}
		return (float) Math.sqrt(sum2 / (data.length * data[0].length));
	}

	public static float[][] addMatricies(float[][] matrix1, float[][] matrix2) throws ComputationException {
		try {
			float[][] result = new float[matrix1.length][matrix1[0].length];
			for (int i = 0; i < matrix1.length; i++) {
				for (int j = 0; j < matrix1[0].length; j++) {
					result[i][j] = matrix1[i][j] + matrix2[i][j];
				}
			}
			return result;
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			throw new ComputationException(e + "");
		}
	}

	public static void autoRefMapCheck(AutoRefMapConfig autoRefMapConfig, Point currentCoarsePosition, Point currentFinePosition, 
			float ccdLeftTemperature, float ccdRightTemperature, int numIterations,
			Date currentDate, RefBeamMap currentRefMap) throws AutoRefMapCheckException {

		CcdFrame refMapFrame = currentRefMap.getProcedureRefBeamMap().getProcedure().getLatestProcedureCcdFrame().getCcdFrame();
		
		CameraState cameraState = refMapFrame.getCameraState();
		CcdState ccdState = refMapFrame.getCcdState();

		if (Math.abs(ccdLeftTemperature - ccdState.getLeftTemperature()) > autoRefMapConfig.getCcdTempChangeThresh()) {
			throw new AutoRefMapCheckException("autorefmap.temp_change_limit_exceeded", ccdLeftTemperature, ccdState.getLeftTemperature());
		}

		if (Math.abs(ccdRightTemperature - ccdState.getRightTemperature()) > autoRefMapConfig.getCcdTempChangeThresh()) {
			throw new AutoRefMapCheckException("autorefmap.temp_change_limit_exceeded", ccdRightTemperature, ccdState.getRightTemperature());
		}
		
		if (numIterations >= autoRefMapConfig.getNumTrialsLimit()) {
			throw new AutoRefMapCheckException("autorefmap.num_trials_limit_exceeded", numIterations, autoRefMapConfig.getNumTrialsLimit());

		}

		if (Math.abs(currentCoarsePosition.x - cameraState.getSteeringMirrorX()) > autoRefMapConfig.getCoarseTiltChangeThresh()) {
			throw new AutoRefMapCheckException("autorefmap.coarse_x_change_limit_exceeded", currentCoarsePosition.x,
					cameraState.getSteeringMirrorX());
		}

		if (Math.abs(currentCoarsePosition.y - cameraState.getSteeringMirrorY()) > autoRefMapConfig.getCoarseTiltChangeThresh()) {
			throw new AutoRefMapCheckException("autorefmap.coarse_y_change_limit_exceeded", currentCoarsePosition.y,
					cameraState.getSteeringMirrorY());
		}

		if (Math.abs(currentFinePosition.x - cameraState.getTiltPlateX()) > autoRefMapConfig.getFineTiltChangeThresh()) {
			throw new AutoRefMapCheckException("autorefmap.fine_x_change_limit_exceeded", currentFinePosition.x,
					cameraState.getTiltPlateX());
		}

		if (Math.abs(currentFinePosition.y - cameraState.getTiltPlateY()) > autoRefMapConfig.getFineTiltChangeThresh()) {
			throw new AutoRefMapCheckException("autorefmap.fine_y_change_limit_exceeded", currentFinePosition.y,
					cameraState.getTiltPlateY());
		}


		// time threshold comparison, expire age thresh in hours
		long delta = currentDate.getTime() - currentRefMap.getCreateDate().getTime();
		if (delta > (autoRefMapConfig.getRefMapExpirationAge() * Constants.MS_PER_HOUR)) {
			throw new AutoRefMapCheckException("autorefmap.refmap_age_limit_exceeded", delta / Constants.MS_PER_HOUR, "");

		}

	}

	public static AutoCenterTelCheckResult autoCenterTelescopeCheck(AutoCenterTelConfig autoCenterTelConfig, FloatPoint deltaAzEl,
			FloatPoint lastMove) {
		/*
		 * Computation that does the following: Rules: logic for if centering is necessary: if move is < thresh1, do nothing if move >
		 * thresh1 and < thresh2 send cmds if move > thresh2 and < thresh3 send cmds and retake frame If move > thresh3 prompt user
		 */

		Object[] args = new Object[6];

		args[0] = deltaAzEl.x;
		args[1] = deltaAzEl.y;
		args[2] = autoCenterTelConfig.getMoveTelFrameOkThreshold();
		args[3] = autoCenterTelConfig.getTelMoveTooLargeThreshold();

		// if both axes are less than tolerance
		if (Math.abs(deltaAzEl.x) < autoCenterTelConfig.getMoveTelFrameOkThreshold()
				&& Math.abs(deltaAzEl.y) < autoCenterTelConfig.getMoveTelFrameOkThreshold()) {
			// no need to move
			return new AutoCenterTelCheckResult(TriState.NO, TriState.NO, "autocentertel.move_too_small", args);

			// if either axis is gt tolerance
		} else if (Math.abs(deltaAzEl.x) > autoCenterTelConfig.getTelMoveTooLargeThreshold()
				|| Math.abs(deltaAzEl.y) > autoCenterTelConfig.getTelMoveTooLargeThreshold()) {
			// the calculated move is too much, prompt the user
			return new AutoCenterTelCheckResult(TriState.PROMPT, TriState.YES, "autocentertel.move_too_large", args);

		} else {
			// telescope needs to be moved
			if (deltaAzEl.mag() > autoCenterTelConfig.getRetakeFrameThreshold()) {
				// retake frame
				if (lastMove != null && lastMove.mag() > autoCenterTelConfig.getRetakeFrameThreshold()) {
					args[4] = lastMove.x;
					args[5] = lastMove.y;
					// too many consecutive large moves (large enough to need to re-take frames), ask user first
					return new AutoCenterTelCheckResult(TriState.PROMPT, TriState.YES, "autocentertel.consecutive_large_moves", args);
				} else {
					// auto-center and retake frame
					return new AutoCenterTelCheckResult(TriState.YES, TriState.YES, "autocentertel.move_tel", args);
				}

			} else {
				// move telescope, but do not retake frame
				return new AutoCenterTelCheckResult(TriState.YES, TriState.NO, "autocentertel.move_tel", args);
			}
		}
	}

	public static List<Integer> checkSubimageIntensities(CentroidMap centroidMap, double threshold, int n) throws Exception {
		
		// determines for the 'n' top valued peaks, if they all exceed the threshold for non-linear intensities
		
		
		float[] maxList = topN(centroidMap.getFindCentroidsResult().getRawPeakList(), n);
		float min = calcMin(maxList);
		float max = calcMax(maxList);
		
		if (min > threshold) {
			throw new NonLinearIntensitiesException(max, (float)threshold, n);
		}
		
		List<Integer> resultList = new ArrayList<Integer>();
		for (float peakIntensity : maxList) {
			if (peakIntensity > threshold) {
				resultList = getMatchingValueIndexes(centroidMap.getFindCentroidsResult().getRawPeakList(), peakIntensity);
				
				for (Integer index : resultList) {
					// if less than n found but > 0, we want to change the value of the Subimage.findCentStatus to FIND_CENT_STATUS_NON_LINEAR.
					
					centroidMap.getFindCentroidsResult().setSubimageFindCentStatus(index, Constants.FIND_CENT_STATUS_NON_LINEAR);

					logger.info("ignoring non-linear subimage at index = " + index);
				}
				
			}
		}
		return resultList;
	}
	
	public static List<Integer> getMatchingValueIndexes(float[] values, float matchValue) {
		List<Integer> resultList = new ArrayList<Integer>();
		for (int i=0; i<values.length; i++) {
			if (values[i] == matchValue) {
				resultList.add(i);
			}
		}
		return resultList;
	}

	public static float getMedianValue(float[] inputs) {
		
		if (inputs.length == 0 ) return 0.0f;
		
		// clone the array 
		float[] values = inputs.clone();
		
		Arrays.sort(values);
		float median;
		if (values.length % 2 == 0)
		    median = ((float)values[values.length/2] + (float)values[values.length/2 - 1])/2;
		else
		    median = (float) values[values.length/2];
		
		return median;
	}
	
	public static double[] convertToDoubleArray(Float[] input) {
		double[] output = new double[input.length];
	    int i=0;
	    for (Float f : input) {
	        output[i] = f.floatValue();
	        i++;
	    }
	    return output;
	}
	
	public static double[] convertToDoubleArray(float[] input) {
		double[] output = new double[input.length];
	    int i=0;
	    for (Float f : input) {
	        output[i] = f.floatValue();
	        i++;
	    }
	    return output;
	}

	public static float getMean(double[] inputs) {
		
		return (float)StatUtils.mean(inputs);
	}

	public static double[] floatArrayToDouble(float[] input) {
		double[] result = new double[input.length];
		for (int i=0; i<input.length; i++) {
			result[i] = input[i];
		}
		return result;
	}
	
	public static float getMean(Float[] inputs) {
		
		return (float)StatUtils.mean(convertToDoubleArray(inputs));
	}

	public static FloatPoint getMean(FloatPoint[] inputs) {
		
		float[] xArray = FloatPointListEncoder.extractXArray(Arrays.asList(inputs));
		float[] yArray = FloatPointListEncoder.extractYArray(Arrays.asList(inputs));
		
		float xMean = (float)StatUtils.mean(convertToDoubleArray(xArray));
		float yMean = (float)StatUtils.mean(convertToDoubleArray(yArray));
		
		return new FloatPoint(xMean, yMean);
	}

	public static float getStd(double[] inputs) {
		float mean = getMean(inputs);
		float sum = 0.0f;
		for(double input : inputs) {
			sum += (input - mean) * (input - mean);
		}
		return inputs.length < 2 ? 0.0f : (float)Math.sqrt(sum / (inputs.length - 1));
	}
	
	public static float getEom(double[] inputs) {
		
		double std = getStd(inputs);
		return inputs.length < 1 ? 0.0f : (float) (std/Math.sqrt(inputs.length));
	}
	
	public static float getStd(Float[] inputs) {
		double[] array = convertToDoubleArray(inputs);
		
		return (float)getStd(array);
	}
	
	public static float getEom(Float[] inputs) {
		double[] array = convertToDoubleArray(inputs);
		
		return (float)getEom(array);
	}

	
	public static FloatPoint getStd(FloatPoint[] inputs) {
		double[] xArray = convertToDoubleArray(FloatPointListEncoder.extractXArray(Arrays.asList(inputs)));
		double[] yArray = convertToDoubleArray(FloatPointListEncoder.extractYArray(Arrays.asList(inputs)));
		
		float xStd = getStd(xArray);
		float yStd = getStd(yArray);
		
		return new FloatPoint(xStd, yStd);
	}
	
	public static FloatPoint getEom(FloatPoint[] inputs) {
		double[] xArray = convertToDoubleArray(FloatPointListEncoder.extractXArray(Arrays.asList(inputs)));
		double[] yArray = convertToDoubleArray(FloatPointListEncoder.extractYArray(Arrays.asList(inputs)));
		
		float xEom = getEom(xArray);
		float yEom = getEom(yArray);
		
		return new FloatPoint(xEom, yEom);
	}
	
	public static float[] flatten2dArray(float[][] input, int fastIndex) {
		float[] result = new float[input.length * input[0].length];
		int k = 0;
		if (fastIndex == 0) {
			for (int i=0; i<input[0].length; i++) {
				for (int j=0; j<input.length; j++) {
					result[k++] = input[j][i];
				}
			}
		} else {
			for (int i=0; i<input.length; i++) {
				for (int j=0; j<input[0].length; j++) {
					result[k++] = input[i][j];
				}
			}			
		}
		return result;
	}
	
	public static float[][] expandTo2dArray(float[] input, int minorIndexSize) {
		float[][] result = new float[input.length/minorIndexSize][minorIndexSize];
		for (int i = 0; i<input.length/minorIndexSize; i++) {
			for (int j=0; j < minorIndexSize; j++) {
				result[i][j] = input[i*minorIndexSize + j]; 
			}
		}
		return result;
	}
	
	
	
	public static float getDotProdRms(float[] testVector, float template[]) {
		// rms of test vector dotted with the template vector
		
		// take the dot product
		float sumTestTemplate = 0;
		float sumTemplateTemplate = 0;
		for (int i=0; i< testVector.length; i++) {
			sumTestTemplate += testVector[i] * template[i];
			sumTemplateTemplate += template[i] * template[i];
		}
		
		float rms = (float)(sumTestTemplate / (Math.sqrt(sumTemplateTemplate) * Math.sqrt((double)testVector.length)));
		
		return rms;
	}
		
	public static <T> T[][] transpose2dArray(T[][] matrix)
	{
		int idx1 = matrix.length;
		int idx2 = matrix[0].length;
		Class<T> arrayType = (Class<T>) matrix[0][0].getClass();
		
	    T[][] transpose = (T[][]) Array.newInstance(arrayType, idx2, idx1);
	    for (int x = 0; x < idx2; x++)
	    {
	        for (int y = 0; y < idx1; y++)
	        {
	            transpose[x][y] = matrix[y][x];
	        }
	    }
	    return transpose;
	}

	public static float[][] transpose2dArray(float[][] matrix)
	{
		int idx1 = matrix.length;
		int idx2 = matrix[0].length;
		
	    float[][] transpose = new float[idx2][idx1];
	    for (int x = 0; x < idx2; x++)
	    {
	        for (int y = 0; y < idx1; y++)
	        {
	            transpose[x][y] = matrix[y][x];
	        }
	    }
	    return transpose;
	}

	public static int[][] transpose2dArray(int[][] matrix)
	{
		int idx1 = matrix.length;
		int idx2 = matrix[0].length;
		
	    int[][] transpose = new int[idx2][idx1];
	    for (int x = 0; x < idx2; x++)
	    {
	        for (int y = 0; y < idx1; y++)
	        {
	            transpose[x][y] = matrix[y][x];
	        }
	    }
	    return transpose;
	}

	public static float[][][] transpose3dArray(float[][][] matrix)
	{
		int idx1 = matrix.length;
		int idx2 = matrix[0].length;
		int idx3 = matrix[0][0].length;
		
	    float[][][] transpose = new float[idx3][idx2][idx1];
	    for (int z = 0; z < idx3; z++) {
		    for (int y = 0; y < idx2; y++)
		    {
		        for (int x = 0; x < idx1; x++)
		        {
		            transpose[z][y][x] = matrix[x][y][z];
		        }
		    }
	    }
	    return transpose;
	}

	public static float calcMax(float[] input, int[] useValue) {
		// sum absolute values of inputs for which useValue = 1
		float max = 0.0f;
		for (int i=0; i<input.length; i++) {
			if (useValue[i] == 1) max = Math.max(max, Math.abs(input[i]));
		}
		return max;
	}
	
	public static float calcMax(float[] input) {
		// find the max value in an array
		float max = 0.0f;
		for (int i=0; i<input.length; i++) {
			max = Math.max(max, input[i]);
		}
		return max;
	}
	
	public static float calcMin(float[] input) {
		// find the max value in an array
		float min = Float.MAX_VALUE;
		for (int i=0; i<input.length; i++) {
			min = Math.min(min, input[i]);
		}
		return min;
	}
	
	public static float[] topN(float[] input, int n) {
		// returns the top n values in an array
		
		List<Float> list = new ArrayList<Float>();
		for (float element : input) {
			list.add(Float.valueOf(element));
		}
		
		Collections.sort(list);
		List<Float> topNList = new ArrayList<Float>(list.subList(list.size() -n, list.size()));
		
		
		float[] returnVal = new float[n];
		int i=0;
		for (Float obj : topNList) {
			returnVal[i++] = obj.floatValue();
		}
		
		return returnVal;
		
	}
	
	public static float calcRss(float[] input, int[] useValue) {
		// sum absolute values of inputs for which useValue = 1
		// sqrt( ( sum of a^2 over all elements)/n). for which useValue = 1
		float sumOfSquares = 0.0f;
		int count = 0;
		for (int i=0; i<input.length; i++) {
			if (useValue[i] == 1) {
				sumOfSquares += (input[i] * useValue[i]) * (input[i] * useValue[i]);
				count ++;
			}
		}
		
		return (count == 0) ? 0.0f :(float)Math.sqrt(sumOfSquares/count);
		
	}
	
	
	
	public static CalcPrCommandsResult calcPrCommands(boolean centerPupil, int desiredCenterPupilMech, PupilRegErrorResult pupilRegErrorResult,
			PupilRegErrorConfig pupilRegErrorConfig, FineTiltMirror fineTiltMirror, CoarseTiltMirror coarseTiltMirror)
					throws Exception {
	
		
		if (!centerPupil) {
			return new CalcPrCommandsResult(null, null, null, null);
		}
	
		// 8/26/2015 we are overshooting use a scale factor here
		// the desired correction is typically the negative of the pupil reg error result in x and y
		
		float gainFactor = (pupilRegErrorResult.getRegErrorX() > pupilRegErrorConfig.getSmallLargeCommandThreshold() || 
				pupilRegErrorResult.getRegErrorY() > pupilRegErrorConfig.getSmallLargeCommandThreshold()) ? 
				pupilRegErrorConfig.getLargeCommandGainFactor() : 
				pupilRegErrorConfig.getSmallCommandGainFactor();
		
		FloatPoint desiredCorrection = new FloatPoint(-pupilRegErrorResult.getRegErrorX()*gainFactor, -pupilRegErrorResult.getRegErrorY()*gainFactor);
		
	
		
		// if desiredCenterPupilMech is FineTilt, then check to see if it is outside limits.  If it is outside limits, offload to coarse.
		if (desiredCenterPupilMech == Constants.AUTO_CENTER_PUPIL_MECH_FINE) {
			// Fine desired, if either x or y move exceeds limits, offload to coarse mirror
			
			return checkFineForOffloading(desiredCorrection, fineTiltMirror, coarseTiltMirror);
			
			
		} else if (desiredCenterPupilMech == Constants.AUTO_CENTER_PUPIL_MECH_COARSE) {
			// coarse desired, only move coarse mirror
			
			Point coarseMirrorPosDelta = calcCoarseMirrorCmds(desiredCorrection, coarseTiltMirror.getMechanismLeverArm(), coarseTiltMirror.getOrafactor());
			
			Point coarseMirrorPosCmds = Point.add(coarseTiltMirror.getCurrentPosition(), coarseMirrorPosDelta);
			
			return new CalcPrCommandsResult(coarseMirrorPosCmds, null, coarseMirrorPosDelta, null);
			
		} else {
			// Auto case: if pupilRegError x or y exceeds large move threshold, use coarse, otherwise fine
			
			Point coarseMirrorPosDelta = calcCoarseMirrorCmds(desiredCorrection, coarseTiltMirror.getMechanismLeverArm(), coarseTiltMirror.getOrafactor());
			
			if (Math.abs(coarseMirrorPosDelta.x) > coarseTiltMirror.getMinMove() || Math.abs(coarseMirrorPosDelta.y) > coarseTiltMirror.getMinMove()) {
				
				// only move coarse				
				Point coarseMirrorPosCmds = Point.add(coarseTiltMirror.getCurrentPosition(), coarseMirrorPosDelta);
				
				return new CalcPrCommandsResult(coarseMirrorPosCmds, null, coarseMirrorPosDelta, null);
				
			} else {
				
				return checkFineForOffloading(desiredCorrection, fineTiltMirror, coarseTiltMirror);
			}
			
		}

	}
	
	
	private static CalcPrCommandsResult checkFineForOffloading(FloatPoint desiredCorrection, FineTiltMirror fineTiltMirror, CoarseTiltMirror coarseTiltMirror) {
		
		// get the commands we would require to move only the fine mech
		Point fineMirrorDeltas = calcFineMirrorCmds(desiredCorrection, fineTiltMirror.getMechanismLeverArm(), fineTiltMirror.getWindowThickness(), fineTiltMirror.getXbk7(), fineTiltMirror.getPupilMagnification());
		
		Point fineMirrorPosCmds = Point.add(fineTiltMirror.getCurrentPosition(), fineMirrorDeltas);
		
		// check commands against limits
		if (Math.abs(fineMirrorPosCmds.x)  > fineTiltMirror.getOffloadThreshold() || Math.abs(fineMirrorPosCmds.y)  > fineTiltMirror.getOffloadThreshold()) {
			
			// determine offloading values
			Point offloadedCoarseDelta = offloadFineToCoarse(fineMirrorPosCmds, coarseTiltMirror, fineTiltMirror);

			Point coarseMirrorPosCmds = Point.add(coarseTiltMirror.getCurrentPosition(), offloadedCoarseDelta);

			Point fineMirrorDelta = Point.multiply(fineTiltMirror.getCurrentPosition(), -1);
			
			// return commands for coarse mirror and send fine to 0,0
			return new CalcPrCommandsResult(coarseMirrorPosCmds, new Point(0,0), offloadedCoarseDelta, fineMirrorDelta, true);
			
		} else {
			// just command fine mechanism
			return new CalcPrCommandsResult(null, fineMirrorPosCmds, null, fineMirrorDeltas, false);
		}

	}
	
	
	/*
	Offloading from Tilt plate to Coarse Mirror
		Given the current X,Y tilt plate positions, calculate the needed X/Y coarse mirror commands to send in order to “zero” the tilt plate.  The releaiveX/Y coarse mirror commands from this routine should be added to the current X/Y coarse mirror positions and the tilt plate should be send to 0,0.  Note that this offload will introduce an ~5mm pupil registration error, as we only command the mechanisms to the nearest micron.
		Relevant parameters:none
			Inputs:
				currentXTiltPos
					Description: current position of X tilt motor mike
					Units: microns
				currentYTiltPos
					Description: current position of X tilt motor mike
					Units: microns
			Output:
				relativeXCoarseCmd
					Description: X coarse mirror motion to zero X  tilt plate
					Units: microns
					Data type: Integer  
				relativeYCoarseCmd
					Description: Y coarse mirror motion to zero Y tilt plate
					Units: microns
					Data type: Integer
			Calculations
				Calculate needed sensitivities
					calcCoarseMirrorCmd(1000,1000,xCoarseSens, yCoarseSens)
					calcFineTiltCmd(1000,1000,xTiltSens, yTiltSens)
				Calculate output
					relativeXCoarseCmd = round(xCoarseSens/xTiltSens*currentXTiltPos)
					relativeYCoarseCmd = round(yCoarseSens/yTiltSens*currentYTiltPos)
	 */
	
	private static Point offloadFineToCoarse(Point fineMirrorCmds, CoarseTiltMirror coarseTiltMirror, FineTiltMirror fineTiltMirror) {
		
		Point coarseSensitivity = calcCoarseMirrorCmds(new FloatPoint(1.0f, 1.0f), coarseTiltMirror.getMechanismLeverArm(), coarseTiltMirror.getOrafactor());		
		Point fineSensitivity = calcFineMirrorCmds(new FloatPoint(1.0f, 1.0f), fineTiltMirror.getMechanismLeverArm(), fineTiltMirror.getWindowThickness(), fineTiltMirror.getXbk7(), fineTiltMirror.getPupilMagnification());
		
		int relativeXCoarseCmd = Math.round((float)coarseSensitivity.x / (float)fineSensitivity.x * (float)fineMirrorCmds.x);
		int relativeYCoarseCmd = Math.round((float)coarseSensitivity.y / (float)fineSensitivity.y * (float)fineMirrorCmds.y);

		return new Point(relativeXCoarseCmd, relativeYCoarseCmd);
	}
	
	
	/*
		Coarse Mirror Commands (calcCoarseMirrorCmd)
		Relevant Parameters:
			ORAFactor
				Description: Radians of mirror tilt per mm of primary mirror motion
				Units: Radians of mirror/mm of M1
			xLeverCoarse
				Description: X Lever arm of coarse mirror mike
				Units: microns
			yLeverCoarse
				Description: Y Lever arm of coarse mirror mike
				Units: microns
		Inputs:
			desiredXMotion
				Description: The desired motion in X of the pupil at the mask in PCS. Note this would typically be the negative of the error calculated by calculatePupilRegError
				Units: m at M1
			desiredYMotion
				Description: The desired motion in Y of the pupil at the mask in PCS. Note this would typically be the negative of the error calculated by calculatePupilRegError
				Units: m at M1
		Outputs:
			relativeXCoarseCmd
				Description: How far to move the X coarse mike
				Units: microns
				Data type: integer
			relativeYCoarseCmd
				Description: How far to move the Y coarse mike
				Units: microns
				Data type: integer
		Calculations:
			relativeXCoarseCmd = round(desiredXMotion*ORAFactor*xLeverCoarse)
			relativeYCoarseCmd = round(desiredYMotion*ORAFactor*yLeverCoarse)

		Notes/comments/todo’s
			SUFS has a negative sign compared to these values, I think?
			Currently parameters are the same for PCS 1 and 2, but in theory could be different
			If want to steer to a new segment, then I think we should input the desired motion to steer to that segment, which is not how the current code handles things.

*/
	public static Point calcCoarseMirrorCmds(FloatPoint desiredMotion, FloatPoint leverCoarse, float oraFactor) {
		
		
		
		int relativeXCoarseCmd = Math.round(desiredMotion.x * 1000.0f * oraFactor * leverCoarse.x);
		int relativeYCoarseCmd = Math.round(-1.0f * desiredMotion.y * 1000.0f * oraFactor * leverCoarse.y);

		
		return new Point(relativeXCoarseCmd, relativeYCoarseCmd);
	}
	
	/*
	 
	Tilt plate Commands (calcFineTiltCmd)
		Relevant Parameters:
		
		windowThickness
			Value: 18800.0
			Description: Thickness of window in Tilt plate mechanism
			Units:  microns
		xbk7Index
			Value: 1.515
			Description: Index of BK7 tilt window at 633 nm
			Units: N/A
		pupilDemag
			Value: 201.34
			Description: Demagnification of primary pupil within PCS
			Units: N/A
		xLeverFine
			Value:55600.0
			Description: X Lever arm of coarse mirror mike
			Units: microns
		yLeverFine
			Value: 47300.0
			Description: Y Lever arm of coarse mirror mike
			Units: microns

	Inputs:
		desiredXMotion
			Description: The desired motion in X of the pupil at the mask in PCS. Note this would typically be the negative of the error calculated by calculatePupilRegError
			Units: m at M1
			desiredYMotion
			Description: The desired motion in Y of the pupil at the mask in PCS. Note this would typically be the negative of the error calculated by calculatePupilRegError
			Units: m at M1
	Outputs:
		relativeXTiltCmd
			Description: How far to move the X tilt mike
			Units: microns
			Data type: integer
		relativeYTiltCmd
			Description: How far to move the Y tilt mike
			Units: microns
			Data type: integer

	Calculations:
		Convert desired motion at primary in mm to motion at mask in microns
			xMask = desiredXMotion*1000.0/pupilDemag
			yMask = desiredYMotion*1000.0/pupilDemag
			relativeXTiltCmd = round(xMask*xLeverFine/windowThickness * xbk7Index/(xbk7Index-1))
			relativeYTiltCmd = round(yMask*yLeverFine/windowThickness * xbk7Index/(xbk7Index-1))

	Notes/comments/todo’s
		SUFS has a negative sign compared to these values, I think? However, it never uses the fine tile plate
		Currently parameters are the same for PCS 1 and 2, but in theory could be different

	 */
	
	public static Point calcFineMirrorCmds(FloatPoint desiredMotion, FloatPoint leverFine, float windowThickness, float xbk7Index, float pupilDemag) {
	
		float xMask = desiredMotion.x * 1000.0f * 1000.0f / pupilDemag;
		float yMask = desiredMotion.y * 1000.0f * 1000.0f / pupilDemag;
		int relativeXTiltCmd = Math.round(xMask * leverFine.x / windowThickness * xbk7Index/(xbk7Index-1));
		int relativeYTiltCmd = Math.round(-1.0f * yMask * leverFine.y / windowThickness * xbk7Index/(xbk7Index-1));

		return new Point(relativeXTiltCmd, relativeYTiltCmd);
	}

	
	public static float[][] generatePhasingInteractionMatrix(int nedges, int nsegments, int[] plusPiston, int[] minusPiston) {
		
		float[][] matrix = new float[nedges][nsegments];
		
		for (int i=0; i<nedges; i++) {
			int jp = plusPiston[i];
			int jn = minusPiston[i];
			
			matrix[i][jp-1] = 1.0f;
			matrix[i][jn-1] = -1.0f;
		}
		
		return matrix;
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
	public static boolean doesSubapLieInSeg(FloatPoint aperaturePos, FloatPoint segCenter, float ahex) {
		
        // We consider the 3 sets of parallel lines that define the hexagon, but 
        // extended to infinity.  To pass each of the three tests, the point must
        // lie between the corresponding set of parallel lines.  If it fails one 
        // test, it is out.

        float sqrt3 = (float)Math.sqrt(3.0);
        float bhex = 0.5f * sqrt3 * ahex;

        float y0 = aperaturePos.y;
        float y1 = 0.5f * (aperaturePos.y + sqrt3 * aperaturePos.x);
        float y2 = 0.5f * (aperaturePos.y - sqrt3 * aperaturePos.x);

        float zz0 = segCenter.y;
        float zz1 =  0.5f * (segCenter.y + sqrt3 * segCenter.x);
        float zz2 =  0.5f * (segCenter.y - sqrt3 * segCenter.x);

        // if lies below bottom or lies above top return false
        if (y0 < (zz0 - bhex) || y0 >= (zz0 + bhex)) return false;       

        // if outside either side, return false
        if (y1 < (zz1 - bhex) || y1 >= (zz1 + bhex) || y2 < (zz2 - bhex) || y2 >= (zz2 + bhex)) return false;

		// must be inside the hexagon
		return true;
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
	public static boolean[] determineMissingSegmentSubaperatures(FloatPoint[] subaperatureLocations, FloatPoint[] segmentCenters, float aHex, Integer[] segmentList) {
		
		// Start by assuming the subaperture is not there.
	    // Do NOT test for whether missing segment lies in the SUFS group or not.
	    // It may be a neighbor to the group still visible on the screen and we
	    // need to exclude that.  Rather than keeping track of group members
	    // and their neighbors, just test ALL missing segments.

		boolean[] subaperatureFlgs = new boolean[subaperatureLocations.length];
		
		for (int i=0; i<subaperatureLocations.length; i++) {
			
			FloatPoint subaperatureLocation = subaperatureLocations[i];
			
			for (int j=0; j<segmentList.length; j++) {
				
				boolean segmentPresent = segmentList[j].intValue() == 1;
				FloatPoint segmentCenter = segmentCenters[j];
				
				if (segmentPresent) {
					
					
					// Some subaps are on the boundary. Oversize the radius by 1.02:
	                if (doesSubapLieInSeg(subaperatureLocation, segmentCenter, 1.02f * aHex)) {
	                	subaperatureFlgs[i] = true; // The subaperture is there.
	                	break; // No point testing other segments
	                }
				}
				
				
			}
			
		}
		
		return subaperatureFlgs;
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
	public static boolean[] determineMissingSegmentAnalysisSubimages(FloatPoint[] subaperatureLocations, FloatPoint[] segmentCenters, float aHex, Integer[] segmentList) {
		
		// spots which are not intersegment edges are flagged 'true'
		// each edge is tested for all theoretical segments, if it is contained in two segments, 
		// it is then tested against the incomplete mirror segments.  If that test result in only 
		// one segment then return false for that subimage, this is the case where a subimage will 
		// be found by F&I but needs to be removed from analysis

		boolean[] subaperatureFlgs = new boolean[subaperatureLocations.length];
		
		for (int i=0; i<subaperatureLocations.length; i++) {
			
			FloatPoint subaperatureLocation = subaperatureLocations[i];
			
			// determine if this is an edge spot.  It must be contained by exactly two segments in a complete mirror
			int segmentCount = 0;
			for (int j=0; j<segmentList.length; j++) {
				
				FloatPoint segmentCenter = segmentCenters[j];
							
				// Some subaps are on the boundary. Oversize the radius by 1.02:
                if (doesSubapLieInSeg(subaperatureLocation, segmentCenter, 1.02f * aHex)) {
                	segmentCount++;
                }
				
			}
			
			if (segmentCount == 2) {
			
				for (int j=0; j<segmentList.length; j++) {
					
					boolean segmentPresent = segmentList[j].intValue() == 1;
					FloatPoint segmentCenter = segmentCenters[j];
					
					if (segmentPresent) {
											
						// Some subaps are on the boundary. Oversize the radius by 1.02:
		                if (doesSubapLieInSeg(subaperatureLocation, segmentCenter, 1.02f * aHex)) {
		                	segmentCount--;
		                }
					}					
				}
				
				// if segmentCount is one, then this spot is missing for analysis
				subaperatureFlgs[i] = segmentCount != 1;
				
			
			} else {
				// not an intersegment edge
				subaperatureFlgs[i] = true;
			}
			
		}
		
		return subaperatureFlgs;
	}

	/**
	 * Generates an a-Matrix for incomplete mirror configurations given the complete mirror a-matrix and incomplete mirror config
	 * @param aMatrix
	 * @param mirrorConfig boolean array numbered according to segment number: true if segment is present, false otherwise
	 * @return a modified control matrix where elements corresponding to missing segments are set to zero
	 */
	public static float[][] generateIncompleteMirrorAMatrix(float[][] aMatrix, Integer[] mirrorConfig) {
		
		
		// modify the aMatrix for an incomplete mirror (168/108)
		float[][] modifiedControlMatrix = new float[168][108];
		for (int i=0; i<168; i++) {
			
			for (int j=0; j<36; j++) {
				
				if (mirrorConfig[j].intValue() == 1) {
				
					modifiedControlMatrix[i][j*3] = aMatrix[i][j*3];
					modifiedControlMatrix[i][j*3+1] = aMatrix[i][j*3+1];
					modifiedControlMatrix[i][j*3+2] = aMatrix[i][j*3+2];
					
				} else {
					
					// if any sensor is to be zeroed as a result of missing actuators, then its edge is now peripheral as a result of the missing segment
					// and the sensor must be ignored for all actuators
					
					if (aMatrix[i][j*3] != 0.0f || aMatrix[i][j*3+1] != 0.0f || aMatrix[i][j*3+2] != 0.0f) {
					
						// zero the entire row to ignore the sensor
						for (int k=0; k<36; k++) {
							modifiedControlMatrix[i][k*3] = 0.0f;
							modifiedControlMatrix[i][k*3+1] = 0.0f;
							modifiedControlMatrix[i][k*3+2] = 0.0f;
						}
					}

				}
			}
		}
				
		return modifiedControlMatrix;
	}

	/**
	 * Calculates arcsecPerPixel for a pupil mask given the arcsecPerMeter pupil mask factor and the ccd pixel size
	 * @param arcsecPerMeter
	 * @param pixelSize for the CCD in meters
	 * @return arcsec per pixel
	 */
	public static float calcArcSecPerPixel(float arcsecPerMeter, float pixelSize) {
		
		return arcsecPerMeter * pixelSize;
	}
	
	/**
	 * Calculates mean of frame between left and right dark current overscan areas
	 * 
	 * @param frame
	 * @param leftStartCol
	 * @param leftEndCol
	 * @param rightStartCol
	 * @param rightEndCol
	 * @param overscanSize the size of the overscan area in pixels for a half detector
	 * @return FloatPoint containing left and right overscans
	 */
	public static CorrectOverscanDarkResult correctOverscanFrameDarkOffsets(int[][] frame, int leftStartCol, int leftEndCol, int rightStartCol, int rightEndCol, int overscanSize) throws ComputationException  {
		
		// frame is [x][y] so first element is the column
		
		int leftMedian = calcMedianDarkOffset(frame, leftStartCol, leftEndCol);
		int rightMedian = calcMedianDarkOffset(frame, rightStartCol, rightEndCol);
	
		
		int totalOverscanFrameCols = frame.length;
		int totalCorrectedFrameCols = totalOverscanFrameCols - (overscanSize * 2);
		
		int deltaMedian = Math.abs(leftMedian - rightMedian);
		
		int correctionStartCol = (leftMedian < rightMedian) ? 0 : totalCorrectedFrameCols/2;
		int correctionEndCol = (leftMedian < rightMedian) ? totalCorrectedFrameCols/2 : totalCorrectedFrameCols;
		
		// cut off the frame overscan columns into newFrame
		int[][] newFrame = new int[totalCorrectedFrameCols][frame[0].length];
		System.arraycopy(frame, overscanSize, newFrame, 0, newFrame.length);
		
		// apply the correction median delta to the lower side
		for (int i=correctionStartCol; i<correctionEndCol; i++) {
			for (int j=0; j<newFrame[i].length; j++) {

				int sum =  newFrame[i][j] + deltaMedian;
				
				// so that saturated frames do not exceed max short values
				newFrame[i][j] = (int)Math.min(sum, Short.MAX_VALUE);
			}
		}
				
		return new CorrectOverscanDarkResult(newFrame, leftMedian, rightMedian);
	}
	
	
	/**
	 * Calculates the median value of pixels in a set of columns from startCol to endCol inclusive
	 * 
	 * @param frame
	 * @param startCol
	 * @param endCol
	 * @return
	 */
	public static int calcMedianDarkOffset(int[][] frame, int startCol, int endCol) throws ComputationException  {
		
		int[] allPixels = new int[0];
		for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
			allPixels = combine(allPixels, frame[colIndex]);
		}
		return getMedianValue(allPixels);
	}
	
	
	
	public static int[] combine(int[] a, int[] b) throws ComputationException {
        int length = a.length + b.length;
        int[] result = new int[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
  

	public static int getMedianValue(int[] inputs) throws ComputationException  {
		
		// clone the array 
		int[] values = inputs.clone();
		
		Arrays.sort(values);
		float median;
		if (values.length % 2 == 0)
		    median = ((float)values[values.length/2] + (float)values[values.length/2 - 1])/2;
		else
		    median = (float) values[values.length/2];
		
		return (int)Math.round(median);
	}


	
	
}
