/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.extinf.TimeoutException;

/**
 * EJB Session bean for the PCS CCD command interface. 
 * This EJB is the single entry point to the PCS CCD interface called from executors and diagnostic user interfaces. 
 * All calls are delegated to the {@link ExtInfFactory} which will delegate to either the actual RPC client interface or a simulator.
 * @author smichaels
 */
@Stateless
public class CcdMgmt {

	Logger logger = Logger.getLogger(this.getClass());
	
	@EJB
	ExtInfFactory extInfFactory;

	// All Camera Commands should be defined here



	public void fastWipeCcd() throws Exception {
		extInfFactory.getCcdCommand().fastWipe();
	}

	public void wipeOn() throws Exception {
		extInfFactory.getCcdCommand().wipeOn();
	}

	
	public int[][] getImage() throws Exception {
		return extInfFactory.getCcdCommand().getImage();	
	}

	public void setGain(int channel, double gain) throws Exception {
		extInfFactory.getInstrumentCommand().setGain(channel, gain);
		
	}

	public void setOffset(int channel, double offset) throws Exception {
		
		extInfFactory.getInstrumentCommand().setOffset(channel, offset);
	}

	public int getImageWidth() throws Exception {
		
		return extInfFactory.getInstrumentCommand().getImageWidth();
	}

	public int getImageHeight() throws Exception {
		
		return extInfFactory.getInstrumentCommand().getImageHeight();
	}

	public double getPlateScale() throws Exception {
		
		return extInfFactory.getInstrumentCommand().getPlateScale();
	}

	public void setBinning(int x, int y) throws Exception {
		
		extInfFactory.getInstrumentCommand().setBinning(x, y);
	}

	public int[][] getImage(double exposureTime, boolean useShutter) throws Exception,
			TimeoutException {
		
		
		return extInfFactory.getInstrumentCommand().getImage(exposureTime, useShutter);
		
		
	}

}
