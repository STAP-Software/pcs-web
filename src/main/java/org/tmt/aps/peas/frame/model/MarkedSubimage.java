package org.tmt.aps.peas.frame.model;

import org.tmt.aps.peas.common.FloatPoint;

// class used for marked subimage
public class MarkedSubimage {

	int markNumber;
	FloatPoint centroid; 
	float totalIntensity; 
	float peakIntensity; 
	FloatPoint firstSubimageDelta; 
	float firstSubimageDistance;
	float firstSubimageAngle;
	
	
	public MarkedSubimage(int markNumber, FloatPoint centroid, float totalIntensity, float peakIntensity, FloatPoint firstSubimageDelta, 
			float firstSubimageDistance, float firstSubimageAngle) {
		
		this.markNumber = markNumber;
		this.centroid = centroid;
		this.totalIntensity = totalIntensity;
		this.peakIntensity = peakIntensity;
		this.firstSubimageDelta = firstSubimageDelta;
		this.firstSubimageDistance = firstSubimageDistance;
		this.firstSubimageAngle = firstSubimageAngle;
	}
	
	public int getMarkNumber() {
		return markNumber;
	}
	public void setMarkNumber(int markNumber) {
		this.markNumber = markNumber;
	}
	public FloatPoint getCentroid() {
		return centroid;
	}
	public void setCentroid(FloatPoint centroid) {
		this.centroid = centroid;
	}
	public float getTotalIntensity() {
		return totalIntensity;
	}
	public void setTotalIntensity(float totalIntensity) {
		this.totalIntensity = totalIntensity;
	}
	public float getPeakIntensity() {
		return peakIntensity;
	}
	public void setPeakIntensity(float peakIntensity) {
		this.peakIntensity = peakIntensity;
	}
	
	public FloatPoint getFirstSubimageDelta() {
		return firstSubimageDelta;
	}
	public void setFirstSubimageDelta(FloatPoint firstSubimageDelta) {
		this.firstSubimageDelta = firstSubimageDelta;
	}
	public float getFirstSubimageDistance() {
		return firstSubimageDistance;
	}
	public void setFirstSubimageDistance(float firstSubimageDistance) {
		this.firstSubimageDistance = firstSubimageDistance;
	}
	public float getFirstSubimageAngle() {
		return firstSubimageAngle;
	}
	public void setFirstSubimageAngle(float firstSubimageAngle) {
		this.firstSubimageAngle = firstSubimageAngle;
	}
	
	
}
