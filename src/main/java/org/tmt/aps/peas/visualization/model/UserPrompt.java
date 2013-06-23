package org.tmt.aps.peas.visualization.model;

public class UserPrompt {

	public static final int PROMPT_TYPE_YES_NO = 1;
	// TODO: add the others
	
	
	int promptType;
	
	
	public UserPrompt(int type) {
		this.promptType = type;
	}

	public int getPromptType() {
		return promptType;
	}

	public void setPromptType(int promptType) {
		this.promptType = promptType;
	}
	
	
	
}
