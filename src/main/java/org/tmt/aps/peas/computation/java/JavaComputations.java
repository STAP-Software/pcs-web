package org.tmt.aps.peas.computation.java;


import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatPoint;
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

	public static boolean autoRefMapCheck(AutoRefMapConfig autoRefMapConfig, Point currentPosition, float ccdTemperature, int numIterations,
			RefBeamMap currentRefMap) {
		
		CameraState cameraState = currentRefMap.getProcedureRefBeamMap().getProcedure().getLatestProcedureCcdFrame().getCcdFrame().getCameraState();
		
		
		if (Math.abs(ccdTemperature - cameraState.getCcdTemp()) > autoRefMapConfig.getCcdTempChangeThresh()) {
			return true;
		}
		
		if (numIterations >= autoRefMapConfig.getNumTrialsLimit()) {
			return true;
		}
		
		if (Math.abs(currentPosition.x - cameraState.getSteeringMirrorX()) > autoRefMapConfig.getCoarseTiltChangeThresh()) {
			return true;
		}
		if (Math.abs(currentPosition.y - cameraState.getSteeringMirrorY()) > autoRefMapConfig.getCoarseTiltChangeThresh()) {
			return true;
		}
		
		// TODO: add time threshold calculation
		
		
		
		return false;
	}

	
}
