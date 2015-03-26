package org.tmt.aps.peas.visualization.model;

import org.tmt.aps.peas.common.FloatPoint;

public interface CentroidOffsetsDisplayValues {

	public FloatPoint getTranslationFromRefBeam();
	public float getRotationFromRefBeam();
	public float getScaleChangeFromRefBeam();
	public float getScaleError();
	public float getRmsOffset();
	
	// arcsec per pixel
	
	public FloatPoint[] getCcdCentroidOffsets();
	public FloatPoint[] getCartesianCentroidOffsets();


	public int getMaxSpotNum();
	public float getMaxOffset();
	public float getEnclosedEnergy80();

	
	
}
