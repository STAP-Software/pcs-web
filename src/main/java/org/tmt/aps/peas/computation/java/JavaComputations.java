package org.tmt.aps.peas.computation.java;


import java.util.Date;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.computation.business.ComputationException;
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
			String text = MessageGenerator.generateMessage("autorefmap.temp_change_limit_exceeded", ccdTemperature, cameraState.getCcdTemp());
			throw new AutoRefMapCheckException(text);
		}
			
		if (numIterations >= autoRefMapConfig.getNumTrialsLimit()) {
			String text = MessageGenerator.generateMessage("autorefmap.num_trials_limit_exceeded", numIterations, autoRefMapConfig.getNumTrialsLimit());
			throw new AutoRefMapCheckException(text);
		}
		
		if (Math.abs(currentPosition.x - cameraState.getSteeringMirrorX()) > autoRefMapConfig.getCoarseTiltChangeThresh()) {
			String text = MessageGenerator.generateMessage("autorefmap.coarse_x_change_limit_exceeded", currentPosition.x, cameraState.getSteeringMirrorX());
			throw new AutoRefMapCheckException(text);
		}
		
		if (Math.abs(currentPosition.y - cameraState.getSteeringMirrorY()) > autoRefMapConfig.getCoarseTiltChangeThresh()) {
			String text = MessageGenerator.generateMessage("autorefmap.coarse_y_change_limit_exceeded", currentPosition.y, cameraState.getSteeringMirrorY());
			throw new AutoRefMapCheckException(text);
		}
		
		// time threshold comparison, expire age thresh in hours
		long delta = currentDate.getTime() - currentRefMap.getCreateDate().getTime(); 
		if (delta > (autoRefMapConfig.getRefMapExpirationAge() * Constants.MS_PER_HOUR)) {
			String text = MessageGenerator.generateMessage("autorefmap.refmap_age_limit_exceeded", delta/Constants.MS_PER_HOUR);
			throw new AutoRefMapCheckException(text);
		}
		
	}

	
}
