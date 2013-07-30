package org.tmt.aps.peas.frame.business;


import java.util.List;

import javax.ejb.Stateless;

import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.frame.model.ImageFrame;
import org.tmt.aps.peas.frame.model.RegistrationDelta;

@Stateless
public class ImageProcessor {

	public List<Point> findAndIdentify(ImageFrame frame) {
		// TODO Auto-generated method stub
		return null;
	}

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
