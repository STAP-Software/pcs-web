/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;


import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.extinf.CameraQueryResult;

@Stateless
public class CameraMgmtAsync {

	// Used because JBoss bug that requires an EJB boundary to have methods tagged @Asynchronous  
	// execute in a separate thread.  CameraMgmt nested @Asynchronous calls call methods here.
	
	Logger logger = Logger.getLogger(this.getClass());
	
	@EJB
	ExtInfFactory extInfFactory;


	
	@Asynchronous
	public Future<Integer> commandFineTiltMirrorX(int cmd) throws Exception {
		int xValue = extInfFactory.getCameraCommand().commandXTiltPlate(cmd);
		return new AsyncResult<Integer>(xValue);
	}
	
	@Asynchronous
	public Future<Integer> commandFineTiltMirrorY(int cmd) throws Exception {
		int yValue = extInfFactory.getCameraCommand().commandYTiltPlate(cmd);
		return new AsyncResult<Integer>(yValue);
	}
	

	@Asynchronous
	public Future<Integer> commandCoarseTiltMirrorX(int cmd) throws Exception {
		int xValue = extInfFactory.getCameraCommand().commandXSteeringMirror(cmd);
		return new AsyncResult<Integer>(xValue);
	}
	
	@Asynchronous
	public Future<Integer> commandCoarseTiltMirrorY(int cmd) throws Exception {
		int yValue = extInfFactory.getCameraCommand().commandYSteeringMirror(cmd);
		return new AsyncResult<Integer>(yValue);
	}


}
