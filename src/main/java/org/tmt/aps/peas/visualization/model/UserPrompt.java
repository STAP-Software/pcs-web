/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.model;

public class UserPrompt {

	public static final int PROMPT_TYPE_YES_NO = 1;
	// TODO: add the others
	
	
	int promptType;
	String message;
	
	public UserPrompt(int type, String message) {
		this.promptType = type;
		this.message = message;
	}

	public int getPromptType() {
		return promptType;
	}

	public void setPromptType(int promptType) {
		this.promptType = promptType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
