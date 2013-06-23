package org.tmt.aps.peas.frame.ui;

public class FrameTreeElement {


	private String displayName;
	
	private String fileName;

	public FrameTreeElement(String displayName, String fileName) {

		this.displayName = displayName;
		this.fileName = fileName;
	}
	


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
