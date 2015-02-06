package org.tmt.aps.peas.computation.java;


import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatPoint;

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
	
}
