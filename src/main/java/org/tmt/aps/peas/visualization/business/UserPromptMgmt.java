/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.business;

import java.io.Serializable;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.visualization.model.UserPrompt;

@Singleton
@Lock(LockType.READ)
public class UserPromptMgmt implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	private UserPrompt pendingPrompt;
	private Integer returnState;


	@Lock(LockType.READ)
	public UserPrompt getPendingPrompt() {
		return pendingPrompt;
	}

	@Lock(LockType.READ)
	public void setPendingPrompt(UserPrompt pendingPrompt) {
		this.pendingPrompt = pendingPrompt;
	}

	@Lock(LockType.READ)
	public Integer getReturnState() {
		return returnState;
	}

	@Lock(LockType.READ)
	public void setReturnState(Integer returnState) {
		this.returnState = returnState;
	}

	public void displayYesNoDialog(String text) {
		
		pendingPrompt = new UserPrompt(UserPrompt.PROMPT_TYPE_YES_NO);
		
		waitForReturnState();
	}
	
	private void waitForReturnState() {
		returnState = null;
		// here we wait until the return state changes
		while(returnState == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}

	}

}
