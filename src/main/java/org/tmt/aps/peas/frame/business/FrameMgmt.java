package org.tmt.aps.peas.frame.business;

import javax.ejb.Stateless;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.frame.model.ImageFrame;

@Stateless
public class FrameMgmt {

	public ImageFrame getCorrectedFrame(int frameSource) {
		
		if (frameSource == Constants.FRAME_SOURCE_CCD) {
			// get the frame from CCD or from file, depending on the called type
			
			// if we get from CCD, store into a file
			
		}
		
		// get the frame from the file and return it
		
		return null;
	}

}
