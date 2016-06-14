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
	public static final int PROMPT_TYPE_GENERIC_MULTI_CHOICE = 4;
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
	String header;
	
	int button1Value;
	int button2Value;
	int button3Value;
	
	int buttonCount;
		
	boolean supressAbort = false;
	
	public UserPrompt(String header) {
		this.promptType = PROMPT_TYPE_INFO;
		this.message = "";
		this.header = header;
	}
	
	public UserPrompt(String header, int type, String message) {
		this(header, type, message, false);
	}
	public UserPrompt(String header, int type, String message, boolean supressAbort) {
		this.promptType = type;	
		this.message = message;
		this.header = header;
		this.supressAbort = supressAbort;
		
		if (type == PROMPT_TYPE_INFO) {
			buttonCount = 1;
			button1Text = "Ok";
			button1Value = 0;
		}
		
		if (type == PROMPT_TYPE_YES_NO) {
			buttonCount = 2;
			button1Text = "Yes";
			button1Value = PROMPT_VALUE_YES_NO_YES;
			button2Text = "No";
			button2Value = PROMPT_VALUE_YES_NO_NO;
		}
		
		if (type == PROMPT_TYPE_FLOW_CONTROL_TRIFLOW) {
			buttonCount = 3;
			button1Text = "Continue";
			button1Value = PROMPT_VALUE_FLOW_CONTROL_CONTINUE;
			button2Text = "Try Again";
			button2Value = PROMPT_VALUE_FLOW_CONTROL_RETRY;
			button3Text = "Abort";
			button3Value = PROMPT_VALUE_FLOW_CONTROL_ABORT;
		}
		
		if (type == PROMPT_TYPE_FLOW_CONTROL_BIFLOW) {
			buttonCount = 2;
			button1Text = "Try Again";
			button1Value = PROMPT_VALUE_FLOW_CONTROL_RETRY;
			button2Text = "Abort";
			button2Value = PROMPT_VALUE_FLOW_CONTROL_ABORT;
		}
		
	}
	
	public UserPrompt(String header, int type, String message, String[] buttonTexts, int[] buttonValues) {
		this.promptType = type;	
		this.message = message;
		this.header = header;
	
		buttonCount = buttonTexts.length;

		button1Text = buttonTexts[0];
		button1Value = buttonValues[0];
		
		if (buttonCount > 1) {
			button2Text = buttonTexts[1];
			button2Value = buttonValues[1];			
		}
		
		if (buttonCount > 2) {
			button3Text = buttonTexts[2];
			button3Value = buttonValues[2];			
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

	public int getButton1Value() {
		return button1Value;
	}

	public void setButton1Value(int button1Value) {
		this.button1Value = button1Value;
	}

	public int getButton2Value() {
		return button2Value;
	}

	public void setButton2Value(int button2Value) {
		this.button2Value = button2Value;
	}

	public int getButton3Value() {
		return button3Value;
	}

	public void setButton3Value(int button3Value) {
		this.button3Value = button3Value;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	public boolean isSupressAbort() {
		return supressAbort;
	}

	public void setSupressAbort(boolean supressAbort) {
		this.supressAbort = supressAbort;
	}

	// if the user prompt already contains abort, do not render the auto abort
	public boolean isContainsAbort() {
		if (promptType == PROMPT_TYPE_FLOW_CONTROL_BIFLOW || promptType == PROMPT_TYPE_FLOW_CONTROL_TRIFLOW) return true;
		
		if (promptType == PROMPT_TYPE_INFO || promptType == PROMPT_TYPE_YES_NO) return false;
		
		return (button1Text.contains("Abort") || button2Text.contains("Abort") || button3Text.contains("Abort"));
	}
	
}
