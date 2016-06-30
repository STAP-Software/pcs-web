/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Data class defines definition information for a single subimage. The definition includes: 'ideal' ccd spot centroid, the spotType (interior, peripheral),
 * the missingSpotType (good, missing, missing for analysis), and if the spot is used for M2 calculation.
 * Instances of this class are generally managed as a list that corresponds to a specific mask: {@link org.tmt.aps.peas.computation.model.SubimageDefList}.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.model.SubimageDefList
 */
public class SubimageDef {
	
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