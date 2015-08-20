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
	
	public void displayFrame(int frameNumber) {
		pendingDisplay = true;
		this.frameInstructions = null;
		this.frameInstructionImageName = "";	
		this.frameNumber = frameNumber;
	}
	
	public void displayFrame(String frameInstructions) {
		pendingDisplay = true;
		this.frameInstructions = frameInstructions.replace("\n", "<br/>");
		this.frameInstructionImageName = "";
	}
	
	public void displayFrame(String frameInstructions, String imageName) {
		pendingDisplay = true;
		this.frameInstructions = frameInstructions.replace("\n", "<br/>");
		this.frameInstructionImageName = imageName;
	}
	
	public void displayMarkedFrame() {
		pendingMarkedDisplay = true;
		pendingDisplay = false;
		this.frameInstructions = null;
		this.frameInstructionImageName = "";
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

	public void setMarking(List<Float> xList, List<Float> yList) {
		markList = new ArrayList<FloatPoint>();
		for (int i=0; i< xList.size(); i++) {
			FloatPoint fp = new FloatPoint(xList.get(i), yList.get(i));
			markList.add(fp);
		}
	}
	
	public void setMarking(FloatPoint centroid) {
		markList = new ArrayList<FloatPoint>();
		markList.add(centroid);
	}
	
	public void setMarking(List<FloatPoint> centroids) {
		markList = new ArrayList<FloatPoint>();
		for (FloatPoint point : centroids) {
			markList.add(point);
		}
	}
	
	public void clearMarking() {
		markList = null;
	}
	
	public List<FloatPoint> getMarkList() {
		return markList;
	}
	
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
