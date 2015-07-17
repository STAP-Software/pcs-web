package org.tmt.aps.peas.computation.model;

import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;

public class FindCentResult {

	FloatPoint centroid;
	
	float subimageIntensity;
	float peakIntensity;
	
	public FindCentResult(FloatPoint centroid, float subimageIntensity, float peakIntensity) {
		this.centroid = centroid;
		this.subimageIntensity = subimageIntensity;
		this.peakIntensity = peakIntensity;
	}
	
	public FloatPoint getCentroid() {
		return centroid;
	}
	public void setCentroid(FloatPoint centroid) {
		this.centroid = centroid;
	}
	public float getSubimageIntensity() {
		return subimageIntensity;
	}
	public void setSubimageIntensity(float subimageIntensity) {
		this.subimageIntensity = subimageIntensity;
	}
	public float getPeakIntensity() {
		return peakIntensity;
	}
	public void setPeakIntensity(float peakIntensity) {
		this.peakIntensity = peakIntensity;
	}
	
	
}
