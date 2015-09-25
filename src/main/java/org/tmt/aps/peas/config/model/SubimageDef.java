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
	int useForM2Calc;
		

	public SubimageDef(int subimageNumber, FloatPoint centroid, int spotType, int missingSpotType, int useForM2Calc) {
		this.subimageNumber = subimageNumber;
		this.centroid = centroid;
		this.spotType = spotType;
		this.missingSpotType = missingSpotType;
		this.useForM2Calc = useForM2Calc;
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

	public int getUseForM2Calc() {
		return useForM2Calc;
	}

	public void setUseForM2Calc(int useForM2Calc) {
		this.useForM2Calc = useForM2Calc;
	}




}