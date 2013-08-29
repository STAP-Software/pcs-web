/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;


import javax.ejb.Stateless;

import org.apache.log4j.Logger;

@Stateless
public class CameraCommand {

	Logger logger = Logger.getLogger(this.getClass());

	// All Camera Commands should be defined here
	
	// each method should be annotated with @DeviceAccessible(type=DeviceAccessible.DEVICE_CAMERA)


}
