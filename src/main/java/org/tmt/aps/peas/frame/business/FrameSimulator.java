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
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;

@Singleton
public class FrameSimulator {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	FrameMgmt frameMgmt;
	
	private List<CcdFrame> frameList;
	
	public void init(List<FitsFilename> fitsFilenameList) throws Exception {
		frameList = new ArrayList<CcdFrame>();
		for (FitsFilename fitsFilename : fitsFilenameList) {
			CcdFrame ccdFrame = frameMgmt.loadFitsFrame(fitsFilename.getFileName());
			
			byte[] falseColorPng = frameMgmt.loadPng(ccdFrame);
			ccdFrame.setFalseColorPng(falseColorPng);
			
			frameList.add(ccdFrame);
		}
	}
	
	public CcdFrame getFrame(int index) {
		return frameList.get(index);
	}




	
	
	
}
