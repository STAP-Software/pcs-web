package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.FloatPoint;

public class Subimage {

	FloatPoint centroid;
	
	float subimageIntensity;
	float peakIntensity;
	int findCentStatus;
	
	public Subimage(FloatPoint centroid, float subimageIntensity, float peakIntensity, int findCentStatus) {
		this.centroid = centroid;
		this.subimageIntensity = subimageIntensity;
		this.peakIntensity = peakIntensity;
		this.findCentStatus = findCentStatus;
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

	public int getFindCentStatus() {
		return findCentStatus;
	}

	public void setFindCentStatus(int findCentStatus) {
		this.findCentStatus = findCentStatus;
	}
	
	public boolean isGoodCentroid() {
		return findCentStatus == 0;
	}
	
}
