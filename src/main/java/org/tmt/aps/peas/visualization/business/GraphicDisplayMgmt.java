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
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;

@Singleton
@Lock(LockType.READ)
public class GraphicDisplayMgmt implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	private VisualizationDisplay pendingDisplay;
	private Integer returnState;
	
	String centroidXs;
	String centroidYs;
	String centroidNbrs;


	public String getCentroidXs() {
		return centroidXs;
	}

	public void setCentroidXs(String centroidXs) {
		this.centroidXs = centroidXs;
	}

	public String getCentroidYs() {
		logger.debug(">>>>> getting <<<<< : " + centroidYs);
		return centroidYs;
	}

	public void setCentroidYs(String centroidYs) {
		this.centroidYs = centroidYs;
	}

	public String getCentroidNbrs() {
		return centroidNbrs;
	}

	public void setCentroidNbrs(String centroidNbrs) {
		this.centroidNbrs = centroidNbrs;
	}

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
		
		StringBuffer xBuf = new StringBuffer();
		StringBuffer yBuf = new StringBuffer();
		StringBuffer nBuf = new StringBuffer();
		for (int i=0; i<subimageList.size(); i++) {
			xBuf.append(subimageList.get(i).x + ",");
			yBuf.append(subimageList.get(i).y + ",");
			nBuf.append((i+1) + ",");
		}
		centroidXs = xBuf.substring(0, xBuf.length()-1);
		centroidYs = yBuf.substring(0, yBuf.length()-1);
		centroidNbrs = nBuf.substring(0, nBuf.length()-1);
		
		logger.debug("centroidNbrs = " + centroidNbrs);
		logger.debug("centroidXs = " + centroidXs);
		logger.debug("centroidYs = " + centroidYs);

		pendingDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROIDS);
		
		waitForReturnState();
	}

	public void displayCentroidOffsets(List<FloatPoint> centroidOffsets) {
		
		StringBuffer xBuf = new StringBuffer();
		StringBuffer yBuf = new StringBuffer();
		for (int i=0; i<centroidOffsets.size(); i++) {
			xBuf.append(centroidOffsets.get(i).x + ",");
			yBuf.append(centroidOffsets.get(i).y + ",");
		}
		centroidXs = xBuf.substring(0, xBuf.length()-1);
		centroidYs = yBuf.substring(0, yBuf.length()-1);
		
		logger.debug("centroidNbrs = " + centroidNbrs);
		logger.debug("centroidXs = " + centroidXs);
		
		pendingDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROID_OFFSETS);
		
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
