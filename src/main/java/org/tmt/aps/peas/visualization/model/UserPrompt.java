/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.model;

public class UserPrompt {

	public static final int PROMPT_TYPE_INFO = 0;
	public static final int PROMPT_TYPE_YES_NO = 1;
	public static final int PROMPT_TYPE_FLOW_CONTROL_TRIFLOW = 2;  // continue, do over, abort
	public static final int PROMPT_TYPE_FLOW_CONTROL_BIFLOW = 3;  // do over, abort
	// TODO: add the others
	
	public static final int PROMPT_VALUE_YES_NO_YES = 1;
	public static final int PROMPT_VALUE_YES_NO_NO = 0; 
	
	public static final int PROMPT_VALUE_FLOW_CONTROL_CONTINUE = 0;
	public static final int PROMPT_VALUE_FLOW_CONTROL_RETRY = 1;
	public static final int PROMPT_VALUE_FLOW_CONTROL_ABORT = 2;
	
	int promptType;
	String message;
	String button1Text;
	String button2Text;
	String button3Text;
	
	int buttonCount;
	
	public UserPrompt() {
		this.promptType = PROMPT_TYPE_INFO;
		this.message = "";
	}
	
	public UserPrompt(int type, String message) {
		this.promptType = type;	
		this.message = message;
		
		if (type == PROMPT_TYPE_INFO) {
			buttonCount = 1;
			button1Text = "Ok";
		}
		
		if (type == PROMPT_TYPE_YES_NO) {
			buttonCount = 2;
			button1Text = "Yes";
			button2Text = "No";
		}
		
		if (type == PROMPT_TYPE_FLOW_CONTROL_TRIFLOW) {
			buttonCount = 3;
			button1Text = "Continue";
			button2Text = "Try Again";
			button3Text = "Abort";
		}
		
		if (type == PROMPT_TYPE_FLOW_CONTROL_BIFLOW) {
			buttonCount = 2;
			button1Text = "Try Again";
			button2Text = "Abort";
		}
		
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

	public String getButton1Text() {
		return button1Text;
	}

	public void setButton1Text(String button1Text) {
		this.button1Text = button1Text;
	}

	public String getButton2Text() {
		return button2Text;
	}

	public void setButton2Text(String button2Text) {
		this.button2Text = button2Text;
	}

	public String getButton3Text() {
		return button3Text;
	}

	public void setButton3Text(String button3Text) {
		this.button3Text = button3Text;
	}

	public int getButtonCount() {
		return buttonCount;
	}

	public void setButtonCount(int buttonCount) {
		this.buttonCount = buttonCount;
	}
	
	
	
}
