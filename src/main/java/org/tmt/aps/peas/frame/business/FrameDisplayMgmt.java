/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.business;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Utils;

/**
 * Singleton EJB that manages real-time frame display and marking.  Maintains the list of marked subimages and state machines for pending displays and marking actions.
 * This EJB works in concert with {@link org.tmt.aps.peas.procedure.ui.AsyncController} that is part of the polling mechanism to handle asynchronous event rendering.
 * @author smichaels
 * @see org.tmt.aps.peas.procedure.ui.AsyncController
 */
@Singleton
@Lock(LockType.READ)
public class FrameDisplayMgmt implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	private boolean pendingDisplay;
	private boolean pendingMarkedDisplay;
	private boolean pendingMarkAction;
	
	private List<FloatPoint> markList;
	private String frameInstructions;
	private String frameInstructionImageName;

	private int frameNumber;
	
	// marked centroids x and y
	String centroidXs;
	String centroidYs;

	
	@PostConstruct
	public void init() {
		pendingDisplay = false;
		pendingMarkedDisplay = false;
		pendingMarkAction = false;
	}
	
	/**
	 * @return true if a frame display request is pending
	 */
	@Lock(LockType.READ)
	public boolean getPendingDisplay() {
		return pendingDisplay;
	}

	/**
	 * @return true is a marked frame display request is pending
	 */
	@Lock(LockType.READ)
	public boolean getPendingMarkedDisplay() {
		return pendingMarkedDisplay;
	}

	/**
	 * @return true if a frame marking has been detected and is pending being displayed
	 */
	@Lock(LockType.READ)
	public boolean getPendingMarkAction() {
		return pendingMarkAction;
	}
	
	/**
	 * Sets up for displaying a frame
	 * @param frameNumber the frame number to display
	 */
	public void displayFrame(int frameNumber) {
		pendingDisplay = true;
		this.frameInstructions = null;
		this.frameInstructionImageName = "";	
		this.frameNumber = frameNumber;
	}
	
	/**
	 * Sets up to display a frame with additional marking instructions
	 * @param frameInstructions the text for frame marking instructions
	 */
	public void displayFrame(String frameInstructions) {
		pendingDisplay = true;
		this.frameInstructions = frameInstructions.replace("\n", "<br/>");
		this.frameInstructionImageName = "";
		this.frameNumber = 0;
	}
	
	/**
	 * Sets up to display frame with additional marking instructions and image name
	 * @param frameInstructions the text for frame marking instructions
	 * @param imageName the image name
	 */
	public void displayFrame(String frameInstructions, String imageName) {
		pendingDisplay = true;
		this.frameInstructions = frameInstructions.replace("\n", "<br/>");
		this.frameInstructionImageName = imageName;
	}
	
	/**
	 * Sets up to display a marked frame
	 */
	public void displayMarkedFrame() {
		pendingMarkedDisplay = true;
		pendingDisplay = false;
		this.frameInstructions = null;
		this.frameInstructionImageName = "";
	}

	/**
	 * Waits until no displays are pending
	 */
	public void waitForPendingDisplays() {
		
		int timeout = 0;
		
		// here we wait until the return state changes
		while(getPendingDisplay() || getPendingMarkedDisplay() || timeout > 10) {
						
			Utils.waitFor(1000);
			
			timeout++;
		}
		
	}
	
	/**
	 * Sets the pending display state
	 * @param b the pending state
	 */
	public void setPendingDisplay(boolean b) {
		pendingDisplay = b;
	}
	
	/**
	 * Sets the pending marked display state
	 * @param b the pending state
	 */
	public void setPendingMarkedDisplay(boolean b) {
		pendingMarkedDisplay = b;
	}
	
	/**
	 * Sets the pending mark action state
	 * @param b the pending state
	 */
	public void setPendingMarkAction(boolean b) {
		pendingMarkAction = b;
	}

	/**
	 * Sets the list of current frame marking
	 * @param xList a list of x coordinates of frame marking
	 * @param yList a list of y coordinates of frame marking
	 */
	public void setMarking(List<Float> xList, List<Float> yList) {
		markList = new ArrayList<FloatPoint>();
		for (int i=0; i< xList.size(); i++) {
			FloatPoint fp = new FloatPoint(xList.get(i), yList.get(i));
			markList.add(fp);
		}
	}
	
	/**
	 * Sets the list of current frame marking to a single centroid
	 * @param centroid the centroid to add to the cleared list
	 */
	public void setMarking(FloatPoint centroid) {
		markList = new ArrayList<FloatPoint>();
		markList.add(centroid);
	}
	
	/**
	 * Sets the list of current frame marking
	 * @param centroids the list of centroids 
	 */
	public void setMarking(List<FloatPoint> centroids) {
		markList = new ArrayList<FloatPoint>();
		for (FloatPoint point : centroids) {
			markList.add(point);
		}
	}
	
	/**
	 * Clears the marking list
	 */
	public void clearMarking() {
		markList = null;
	}
	
	/**
	 * @return the marking list
	 */
	public List<FloatPoint> getMarkList() {
		return markList;
	}
	
	/**
	 * @return the frame instructions
	 */
	public String getFrameInstructions() {
		return frameInstructions;
	}

	public String getCentroidXs() {
		return centroidXs;
	}

	public void setCentroidXs(String centroidXs) {
		this.centroidXs = centroidXs;
	}

	public String getCentroidYs() {
		return centroidYs;
	}

	public void setCentroidYs(String centroidYs) {
		this.centroidYs = centroidYs;
	}

	public String getFrameInstructionImageName() {
		return frameInstructionImageName;
	}
	
	public int getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}





}
