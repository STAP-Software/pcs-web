/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.business;


import java.util.List;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.RegistrationDelta;

@Stateless
public class ImageProcessor {

	Logger logger = Logger.getLogger(this.getClass());



	public RegistrationDelta pupilRegistration36(List<Point> subimageList) {
		// TODO Auto-generated method stub
		return null;
	}

	public void calculateCentroidStats() {
		// TODO Auto-generated method stub
		
	}
	
	public List<Point> calculateCentroidOffsets(List<Point> subimageList, List<Point> referenceSubimageList) {
		// TODO Auto-generated method stub
		return null;
	}


}
