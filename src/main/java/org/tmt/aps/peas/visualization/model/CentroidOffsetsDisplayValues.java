package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.common.FloatPoint;

public interface CentroidOffsetsDisplayValues {

	public FloatPoint getTranslationFromRefBeam();
	public float getRotationFromRefBeam();
	public float getScaleChangeFromRefBeam();
	public float getCentroidOffsetsFocus();
	public float getCentroidOffsetsRms();
	
	// arcsec per pixel
	
	public FloatPoint[] getCentroidOffsets();



	
	
}
