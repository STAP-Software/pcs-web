package org.tmt.aps.peas.computation.model;

import java.util.ArrayList;
import java.util.List;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;

public class CentroidOffsetsResult {

	FloatPoint imageTranslation;
	float imageScale;
	float imageRotation;
	private FloatPoint[] ccdCentroidOffsets;
	private FloatPoint[] cartesianCentroidOffsets;

	public CentroidOffsetsResult(float[][] ccdOffsetsArray, float[][] cartesianOffsetsArray, FloatPoint imageTranslation, float imageScale, float imageRotation) {
		
		this.ccdCentroidOffsets = FloatPointListEncoder.convertFromNby2Array(ccdOffsetsArray).toArray(new FloatPoint[0]);
		this.cartesianCentroidOffsets = FloatPointListEncoder.convertFromNby2Array(cartesianOffsetsArray).toArray(new FloatPoint[0]);
		this.imageTranslation = imageTranslation;
		this.imageScale = imageScale;
		this.imageRotation = imageRotation;
	}

	public CentroidOffsetsResult() {};

	public FloatPoint[] getCcdCentroidOffsets() {
		return ccdCentroidOffsets;
	}

	public void setCcdCentroidOffsets(FloatPoint[] ccdCentroidOffsets) {
		this.ccdCentroidOffsets = ccdCentroidOffsets;
	}

	public FloatPoint[] getCartesianCentroidOffsets() {
		return cartesianCentroidOffsets;
	}

	public FloatPoint[] getCartesianInteriorCentroidOffsets(int[] nspotTypes) {
		List<FloatPoint> result = new ArrayList<FloatPoint>();
		for (int i=0; i<cartesianCentroidOffsets.length; i++) {
			if (nspotTypes[i] == Constants.SPOT_TYPE_INTERIOR) {
				result.add(cartesianCentroidOffsets[i]);
			}
		}
		return result.toArray(new FloatPoint[0]);
	}

	public void setCartesianCentroidOffsets(FloatPoint[] cartesianCentroidOffsets) {
		this.cartesianCentroidOffsets = cartesianCentroidOffsets;
	}


	public FloatPoint getImageTranslation() {
		return imageTranslation;
	}

	public void setImageTranslation(FloatPoint imageTranslation) {
		this.imageTranslation = imageTranslation;
	}

	public float getImageScale() {
		return imageScale;
	}

	public void setImageScale(float imageScale) {
		this.imageScale = imageScale;
	}

	public float getImageRotation() {
		return imageRotation;
	}

	public void setImageRotation(float imageRotation) {
		this.imageRotation = imageRotation;
	}

}
