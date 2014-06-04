/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.extinf.CameraQueryResult;
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



}
