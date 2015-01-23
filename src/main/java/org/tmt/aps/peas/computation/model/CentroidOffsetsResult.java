package org.tmt.aps.peas.computation.model;

import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;

public class CentroidOffsetsResult {

	List<FloatPoint> centroidOffsets;
	FloatPoint imageTranslation;
	float imageScale;
	float imageRotation;

	public CentroidOffsetsResult(List<FloatPoint> centroidOffsets, FloatPoint imageTranslation, float imageScale, float imageRotation) {
		this.centroidOffsets = centroidOffsets;
		this.imageTranslation = imageTranslation;
		this.imageScale = imageScale;
		this.imageRotation = imageRotation;
	}

	public List<FloatPoint> getCentroidOffsets() {
		return centroidOffsets;
	}

	public void setCentroidOffsets(List<FloatPoint> centroidOffsets) {
		this.centroidOffsets = centroidOffsets;
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
