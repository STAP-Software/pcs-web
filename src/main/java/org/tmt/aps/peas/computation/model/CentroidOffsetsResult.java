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
	private List<FloatPoint> ccdCentroidOffsets;
	private List<FloatPoint> cartesianCentroidOffsets;

	public CentroidOffsetsResult(float[][] ccdOffsetsArray, float[][] cartesianOffsetsArray, FloatPoint imageTranslation, float imageScale, float imageRotation) {
		
		this.ccdCentroidOffsets = FloatPointListEncoder.convertFromNby2Array(ccdOffsetsArray);
		this.cartesianCentroidOffsets = FloatPointListEncoder.convertFromNby2Array(cartesianOffsetsArray);
		this.imageTranslation = imageTranslation;
		this.imageScale = imageScale;
		this.imageRotation = imageRotation;
	}


	public List<FloatPoint> getCcdCentroidOffsets() {
		return ccdCentroidOffsets;
	}

	public void setCcdCentroidOffsets(List<FloatPoint> ccdCentroidOffsets) {
		this.ccdCentroidOffsets = ccdCentroidOffsets;
	}

	public List<FloatPoint> getCartesianCentroidOffsets() {
		return cartesianCentroidOffsets;
	}

	public List<FloatPoint> getCartesianInteriorCentroidOffsets(int[] nspotTypes) {
		List<FloatPoint> result = new ArrayList<FloatPoint>();
		for (int i=0; i<cartesianCentroidOffsets.size(); i++) {
			if (nspotTypes[i] == Constants.SPOT_TYPE_INTERIOR) {
				result.add(cartesianCentroidOffsets.get(i));
			}
		}
		return result;
	}

	public void setCartesianCentroidOffsets(List<FloatPoint> cartesianCentroidOffsets) {
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
