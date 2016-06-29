package org.tmt.aps.peas.computation.model;

import java.util.ArrayList;
import java.util.List;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;

/**
 * Computation data result class for <b>calculateCentroidOffsets</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calculateCentroidOffsets(FloatPoint[], FloatPoint[], org.tmt.aps.peas.config.model.CentroidOffsetsConfig, org.tmt.aps.peas.instrument.model.PupilMaskType, int[], int[], int[])
 */
public class CentroidOffsetsResult {

	FloatPoint imageTranslation;
	float imageScale;
	float imageRotation;
	private FloatPoint[] ccdCentroidOffsets;
	private FloatPoint[] cartesianCentroidOffsets;
	int[] goodSpots;

	public CentroidOffsetsResult(float[][] ccdOffsetsArray, float[][] cartesianOffsetsArray, FloatPoint imageTranslation, float imageScale, float imageRotation, int[] goodSpots) {
		
		this.ccdCentroidOffsets = FloatPointListEncoder.convertFromNby2Array(ccdOffsetsArray).toArray(new FloatPoint[0]);
		this.cartesianCentroidOffsets = FloatPointListEncoder.convertFromNby2Array(cartesianOffsetsArray).toArray(new FloatPoint[0]);
		this.imageTranslation = imageTranslation;
		this.imageScale = imageScale;
		this.imageRotation = imageRotation;
		this.goodSpots = goodSpots;
	}

	public CentroidOffsetsResult(FloatPoint imageTranslation, float imageScale, float imageRotation, 
			FloatPoint[] ccdCentroidOffsets, FloatPoint[] cartesianCentroidOffsets, int[] goodSpots) {

		this.ccdCentroidOffsets = ccdCentroidOffsets;
		this.cartesianCentroidOffsets = cartesianCentroidOffsets;
		this.imageTranslation = imageTranslation;
		this.imageScale = imageScale;
		this.imageRotation = imageRotation;
		this.goodSpots = goodSpots;
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

	public int[] getGoodSpots() {
		return goodSpots;
	}

	public void setGoodSpots(int[] goodSpots) {
		this.goodSpots = goodSpots;
	}

}
