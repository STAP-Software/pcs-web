/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.Instrument;

@Singleton
public class FrameSimulator {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	FrameMgmt frameMgmt;
	@EJB
	PhysicalModel physicalModel;
	
	private List<CcdFrame> frameList;
	
	public void init(List<FitsFilename> fitsFilenameList) throws Exception {
		frameList = new ArrayList<CcdFrame>();
		for (FitsFilename fitsFilename : fitsFilenameList) {
			CcdFrame ccdFrame = frameMgmt.loadFitsFrame(fitsFilename.getFileName());
			
			byte[] falseColorPng = frameMgmt.loadPng(ccdFrame, true);
			ccdFrame.setFalseColorPng(falseColorPng);
			
			frameList.add(ccdFrame);
		}
	}
	
	public CcdFrame getFrame(int index) {
		
		CcdFrame ccdFrame = frameList.get(index);
		
		// simulate the camera state too
		CameraState cameraState = new CameraState();
		cameraState.setCcdTemp(44.4f);
		ccdFrame.setCameraState(cameraState);
		Instrument instrument = physicalModel.getInstrument();
		ccdFrame.setInstrumentId(instrument.getInstrumentId());

		
		return ccdFrame;
	}




	
	
	
}
