/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import org.tmt.aps.peas.common.FloatPoint;

public class SubimageDef {

	
	// subimage def defines all the definition information for a subimage.
	// The 'ideal' ccd spot centroid
	// the spotType - interior, peripheral
	// the missingSpotType - good, missing, missing for analysis
	
	// The class Subimage will contain the actual centroid location, intensity, peak and findCentStatus, and replaces FindCentResult, 
	// and also contains a reference to the SubimageDef for that subimage
	
	int subimageNumber;

	FloatPoint centroid;
	int spotType; // 1 = interior, 2 = peripheral
	int missingSpotType;  // 1, 2 or 3
	
	// this class replaces FindCentResult
	// maybe this class should be called SubimageDef and stay here.
	// then a Subimage class could be created that inherits from this class, and that class would live where CentroidMap is created (refBeamMap package)
	
	// this is the class where we can put in spot type (peripheral or interior)
	// this is the class where we can put in the missing spot config:  good, missing for f&i, missing for analysis
	// this is the class where we can put all other find_cent values for a centroid.
	

	public SubimageDef(int subimageNumber, FloatPoint centroid, int spotType, int missingSpotType) {
		this.subimageNumber = subimageNumber;
		this.centroid = centroid;
	}
	
	public int getSubimageNumber() {
		return subimageNumber;
	}

	public void setSubimageNumber(int subimageNumber) {
		this.subimageNumber = subimageNumber;
	}

	public FloatPoint getCentroid() {
		return centroid;
	}

	public void setCentroid(FloatPoint centroid) {
		this.centroid = centroid;
	}

	public int getSpotType() {
		return spotType;
	}

	public void setSpotType(int spotType) {
		this.spotType = spotType;
	}

	public int getMissingSpotType() {
		return missingSpotType;
	}

	public void setMissingSpotType(int missingSpotType) {
		this.missingSpotType = missingSpotType;
	}




}