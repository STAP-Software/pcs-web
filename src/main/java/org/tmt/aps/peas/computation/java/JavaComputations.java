package org.tmt.aps.peas.computation.java;


import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatPoint;

public class JavaComputations {

	static Logger logger = Logger.getLogger(JavaComputations.class);
	
	public static FloatPoint pixOffsetsToArcSeconds(FloatPoint measuredPix, double secPerPixel) {

		// desired is 512,512 - the center of the image
		FloatPoint desiredPix = new FloatPoint(0, 0);
		FloatPoint deltaPix = new FloatPoint(measuredPix.x - desiredPix.x, measuredPix.y - desiredPix.y);
				
		logger.debug("deltaPix = " + deltaPix);
		
		float scale = (float)(1.0 / secPerPixel);

		// Convert offsets to arc sec.
		float deltaEl = (float)((0.866 * deltaPix.x + 0.500 * deltaPix.y) / scale);
		float deltaAz = (float)((0.500 * deltaPix.x + 0.866 * deltaPix.y) / scale);

		FloatPoint deltaAzEl = new FloatPoint(deltaAz, deltaEl);
				
		return deltaAzEl;
	}
	
}
