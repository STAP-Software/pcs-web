/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.ui;

import org.jboss.logging.Logger;

/**
 * JSF class to support tree display in frame tools frame browser window
 * @author smichaels
 */
public class FrameTreeElement {

	Logger logger = Logger.getLogger(this.getClass());

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
