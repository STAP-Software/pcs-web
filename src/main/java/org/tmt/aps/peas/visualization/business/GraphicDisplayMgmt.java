/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.business;


import java.io.Serializable;
import java.util.List;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;

@Singleton
@Lock(LockType.READ)
public class GraphicDisplayMgmt implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	private VisualizationDisplay pendingDisplay;
	private Integer returnState;

	@Lock(LockType.READ)
	public VisualizationDisplay getPendingDisplay() {
		return pendingDisplay;
	}

	@Lock(LockType.READ)
	public void setPendingDisplay(VisualizationDisplay pendingDisplay) {
		this.pendingDisplay = pendingDisplay;
	}

	@Lock(LockType.READ)
	public Integer getReturnState() {
		return returnState;
	}

	@Lock(LockType.READ)
	public void setReturnState(Integer returnState) {
		this.returnState = returnState;
	}

	public void displaySubimageCentroids(List<Point> subimageList) {
		
		pendingDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROIDS);
		
		waitForReturnState();
	}

	public void displayCentroidOffsets(List<Point> centroidOffsets) {
		// TODO Auto-generated method stub
		
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
