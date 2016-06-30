/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.CameraState;
import org.tmt.aps.peas.instrument.model.Instrument;

/**
 * Singleton EJB handling frame simulation (frame from file), including support for multiple frames.
 * @author smichaels
 *
 */
@Singleton
public class FrameSimulator {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	FrameMgmt frameMgmt;
	@EJB
	PhysicalModel physicalModel;
	
	private List<CcdFrame> frameList;
	
	/**
	 * Initialization method called from {@link org.tmt.aps.peas.procedure.business.ProcedureExecutionMgmt#performProcedureStartup(org.tmt.aps.peas.procedure.model.Procedure, List)}, 
	 * takes a list of frames from file, loads the CCD frames and generates the PNG files for display.
	 * @param fitsFilenameList the list of fits files to use
	 */
	public void init(List<FitsFilename> fitsFilenameList) throws Exception {
		frameList = new ArrayList<CcdFrame>();
		
		// reorder filename list according to fits filename numbering
		Collections.sort(fitsFilenameList, new BeanComparator("phasingStep"));
		Collections.sort(fitsFilenameList, new BeanComparator("iteration"));
		
		
		for (FitsFilename fitsFilename : fitsFilenameList) {
			CcdFrame ccdFrame = frameMgmt.loadFitsFrame(fitsFilename.getFileName());
			
			byte[] falseColorPng = frameMgmt.loadPng(ccdFrame, true);
			ccdFrame.setFalseColorPng(falseColorPng);
			
			frameList.add(ccdFrame);
		}
	}
	
	/**
	 * Returns the frame with the given sequence index 
	 * @param index the index of the frame in the frame sequence 
	 * @return the CcdFrame from file
	 */
	public CcdFrame getFrame(int index) {
		
		CcdFrame ccdFrame = frameList.get(index);
		
		// simulate the camera state too
		CameraState cameraState = new CameraState();
		cameraState.setCcdTemp(44.4f);
		ccdFrame.setCameraState(cameraState);
		Instrument instrument = physicalModel.getInstrument();
		ccdFrame.setInstrumentId(instrument.getInstrumentId());
		ccdFrame.setIntTime(0.0f);

		//Utils.waitFor(1000);
		
		return ccdFrame;
	}




	
	
	
}
