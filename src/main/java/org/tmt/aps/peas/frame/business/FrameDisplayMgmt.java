/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.business;


import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;

@Singleton
@Lock(LockType.READ)
public class FrameDisplayMgmt implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	private boolean pendingDisplay;
	private boolean pendingMarkedDisplay;
	private boolean pendingMarkAction;

	@PostConstruct
	public void init() {
		pendingDisplay = false;
		pendingMarkedDisplay = false;
		pendingMarkAction = false;
	}
	
	@Lock(LockType.READ)
	public boolean getPendingDisplay() {
		return pendingDisplay;
	}

	@Lock(LockType.READ)
	public boolean getPendingMarkedDisplay() {
		return pendingMarkedDisplay;
	}

	@Lock(LockType.READ)
	public boolean getPendingMarkAction() {
		return pendingMarkAction;
	}
	
	public void displayFrame() {
		pendingDisplay = true;
		
	}
	
	public void displayMarkedFrame() {
		pendingMarkedDisplay = true;
		pendingDisplay = false;
		
	}

	public void setPendingDisplay(boolean b) {
		pendingDisplay = b;
	}
	
	public void setPendingMarkedDisplay(boolean b) {
		pendingMarkedDisplay = b;
	}
	
	public void setPendingMarkAction(boolean b) {
		pendingMarkAction = b;
	}


}
