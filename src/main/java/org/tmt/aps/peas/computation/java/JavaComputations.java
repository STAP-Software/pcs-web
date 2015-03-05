package org.tmt.aps.peas.computation.java;


import java.util.Date;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.TriState;
import org.tmt.aps.peas.computation.business.ComputationException;
import org.tmt.aps.peas.computation.model.AutoCenterTelCheckResult;
import org.tmt.aps.peas.config.model.AutoCenterTelConfig;
import org.tmt.aps.peas.config.model.AutoRefMapConfig;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

public class JavaComputations {

	static Logger logger = Logger.getLogger(JavaComputations.class);
	
	public static FloatPoint pixLocationToDeltaArcSeconds(FloatPoint measuredPix, FloatPoint desiredPix, double secPerPixel) {

		FloatPoint deltaPix = new FloatPoint(measuredPix.x - desiredPix.x, measuredPix.y - desiredPix.y);
				
		logger.debug("deltaPix = " + deltaPix + ", secPerPixel = " + secPerPixel);
		
		// Convert offsets to arc sec.
		float deltaEl = (float)((0.866 * deltaPix.x + 0.500 * deltaPix.y) * secPerPixel);
		float deltaAz = (float)((0.500 * deltaPix.x - 0.866 * deltaPix.y) * secPerPixel);

		FloatPoint deltaAzEl = new FloatPoint(deltaAz, deltaEl);
				
		return deltaAzEl;
	}
	
	public static float calcRms(float[][] data) {
		double sum2 = 0.0;
		for (int i=0; i<data.length; i++) {
			for (int j=0; j<data[0].length; j++) {
				sum2 += data[i][j] * data[i][j];
			}
		}
		return (float)Math.sqrt(sum2/(data.length*data[0].length));
	}

	public static float[][] addMatricies(float[][] matrix1, float[][] matrix2) throws ComputationException {
		try {
		float[][] result = new float[matrix1.length][matrix1[0].length];
		for (int i=0; i<matrix1.length; i++) {
			for (int j=0; j<matrix1[0].length; j++) {
				result[i][j] = matrix1[i][j] + matrix2[i][j];
			}
		}
		return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComputationException(e + "");
		}
	}

	public static void autoRefMapCheck(AutoRefMapConfig autoRefMapConfig, Point currentPosition, float ccdTemperature, int numIterations,
			Date currentDate, RefBeamMap currentRefMap) throws AutoRefMapCheckException {
		
		CameraState cameraState = currentRefMap.getProcedureRefBeamMap().getProcedure().getLatestProcedureCcdFrame().getCcdFrame().getCameraState();
		
		
		if (Math.abs(ccdTemperature - cameraState.getCcdTemp()) > autoRefMapConfig.getCcdTempChangeThresh()) {
			throw new AutoRefMapCheckException("autorefmap.temp_change_limit_exceeded", ccdTemperature, cameraState.getCcdTemp());
			
		}
			
		if (numIterations >= autoRefMapConfig.getNumTrialsLimit()) {
			throw new AutoRefMapCheckException("autorefmap.num_trials_limit_exceeded", numIterations, autoRefMapConfig.getNumTrialsLimit());

		}
		
		if (Math.abs(currentPosition.x - cameraState.getSteeringMirrorX()) > autoRefMapConfig.getCoarseTiltChangeThresh()) {
			throw new AutoRefMapCheckException("autorefmap.coarse_x_change_limit_exceeded", currentPosition.x, cameraState.getSteeringMirrorX());

		}
		
		if (Math.abs(currentPosition.y - cameraState.getSteeringMirrorY()) > autoRefMapConfig.getCoarseTiltChangeThresh()) {
			throw new AutoRefMapCheckException("autorefmap.coarse_y_change_limit_exceeded", currentPosition.y, cameraState.getSteeringMirrorY());

		}
		
		// time threshold comparison, expire age thresh in hours
		long delta = currentDate.getTime() - currentRefMap.getCreateDate().getTime(); 
		if (delta > (autoRefMapConfig.getRefMapExpirationAge() * Constants.MS_PER_HOUR)) {
			throw new AutoRefMapCheckException("autorefmap.refmap_age_limit_exceeded", delta/Constants.MS_PER_HOUR, "");

		}
		
	}
	
	public static AutoCenterTelCheckResult autoCenterTelescopeCheck(AutoCenterTelConfig autoCenterTelConfig, FloatPoint deltaAzEl, FloatPoint lastMove) {
		/*
		Computation that does the following:
			Rules: logic for if centering is necessary:
			if move is < thresh1, do nothing
			if move > thresh1 and < thresh2 send cmds
			if move > thresh2 and < thresh3 send cmds and retake frame
			If move > thresh3 prompt user
		*/
		
		Object[] args = new Object[6];
		
		args[0] = deltaAzEl.x;
		args[1] = deltaAzEl.y;
		args[2] = autoCenterTelConfig.getMoveTelFrameOkThreshold();
		args[3] = autoCenterTelConfig.getTelMoveTooLargeThreshold();
		
		// if both axes are less than tolerance
		if (Math.abs(deltaAzEl.x) < autoCenterTelConfig.getMoveTelFrameOkThreshold() && Math.abs(deltaAzEl.y) < autoCenterTelConfig.getMoveTelFrameOkThreshold()) {
			// no need to move
			return new AutoCenterTelCheckResult(TriState.NO, TriState.NO, "autocentertel.move_too_small", args);
			
		// if either axis is gt tolerance
		} else if (Math.abs(deltaAzEl.x) > autoCenterTelConfig.getTelMoveTooLargeThreshold() || Math.abs(deltaAzEl.y) > autoCenterTelConfig.getTelMoveTooLargeThreshold()) {
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

}
