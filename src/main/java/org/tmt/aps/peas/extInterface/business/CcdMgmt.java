/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

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
		// TODO Auto-generated method stub
		extInfFactory.getInstrumentCommand().setOffset(channel, offset);
	}

	public int getImageWidth() throws Exception {
		// TODO Auto-generated method stub
		return extInfFactory.getInstrumentCommand().getImageWidth();
	}

	public int getImageHeight() throws Exception {
		// TODO Auto-generated method stub
		return extInfFactory.getInstrumentCommand().getImageHeight();
	}

	public double getPlateScale() throws Exception {
		// TODO Auto-generated method stub
		return extInfFactory.getInstrumentCommand().getPlateScale();
	}

	public void setBinning(int x, int y) throws Exception {
		// TODO Auto-generated method stub
		extInfFactory.getInstrumentCommand().setBinning(x, y);
	}

	public int[][] getImage(double exposureTime, boolean useShutter) throws Exception,
			TimeoutException {
		// TODO Auto-generated method stub
		return extInfFactory.getInstrumentCommand().getImage(exposureTime, useShutter);
	}

}
