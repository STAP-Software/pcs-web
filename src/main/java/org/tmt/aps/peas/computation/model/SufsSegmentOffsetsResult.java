package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

public class SufsSegmentOffsetsResult {

	FloatPoint[] imageTranslation;
	float[] imageScale;
	float[] imageRotation;
	private FloatPoint[][] ccdCentroidOffsets;
	private FloatPoint[][] cartesianCentroidOffsets;
	
	public SufsSegmentOffsetsResult(CentroidOffsetsResult[] segmentCentroidOffsetsResults) {
		
		int offsetCount = segmentCentroidOffsetsResults[0].getCartesianCentroidOffsets().length;
		imageTranslation = new FloatPoint[segmentCentroidOffsetsResults.length];
		imageScale = new float[segmentCentroidOffsetsResults.length];
		imageRotation = new float[segmentCentroidOffsetsResults.length];
		ccdCentroidOffsets = new FloatPoint[segmentCentroidOffsetsResults.length][offsetCount];
		cartesianCentroidOffsets = new FloatPoint[segmentCentroidOffsetsResults.length][offsetCount];
		
		for (int i=0; i<segmentCentroidOffsetsResults.length; i++) {
			imageTranslation[i] = segmentCentroidOffsetsResults[i].getImageTranslation();
			imageScale[i] = segmentCentroidOffsetsResults[i].getImageScale();
			imageRotation[i] = segmentCentroidOffsetsResults[i].getImageRotation();
			ccdCentroidOffsets[i] = segmentCentroidOffsetsResults[i].getCcdCentroidOffsets();
			cartesianCentroidOffsets[i] = segmentCentroidOffsetsResults[i].getCartesianCentroidOffsets();
		}
	}

	public SufsSegmentOffsetsResult() {
		
	}
	
	public FloatPoint[] getImageTranslation() {
		return imageTranslation;
	}

	public void setImageTranslation(FloatPoint[] imageTranslation) {
		this.imageTranslation = imageTranslation;
	}

	public float[] getImageScale() {
		return imageScale;
	}

	public void setImageScale(float[] imageScale) {
		this.imageScale = imageScale;
	}

	public float[] getImageRotation() {
		return imageRotation;
	}

	public void setImageRotation(float[] imageRotation) {
		this.imageRotation = imageRotation;
	}

	public FloatPoint[][] getCcdCentroidOffsets() {
		return ccdCentroidOffsets;
	}

	public void setCcdCentroidOffsets(FloatPoint[][] ccdCentroidOffsets) {
		this.ccdCentroidOffsets = ccdCentroidOffsets;
	}

	public FloatPoint[][] getCartesianCentroidOffsets() {
		return cartesianCentroidOffsets;
	}

	public void setCartesianCentroidOffsets(FloatPoint[][] cartesianCentroidOffsets) {
		this.cartesianCentroidOffsets = cartesianCentroidOffsets;
	}

	public CentroidOffsetsResult extractCentroidOffsetsResult(int segmentNumber) {
		return new CentroidOffsetsResult(imageTranslation[segmentNumber], imageScale[segmentNumber], imageRotation[segmentNumber], 
				ccdCentroidOffsets[segmentNumber], cartesianCentroidOffsets[segmentNumber]);
	}


	
	
}
