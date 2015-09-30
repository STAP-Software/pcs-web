/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.business;


import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;

@Singleton
@Lock(LockType.READ)
public class GraphicDisplayMgmt implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	private VisualizationDisplay pendingDisplay;
	private Integer returnState;
	private int waitingForSecs;
	
	String centroidXs;
	String centroidYs;
	String centroidOffsetXs;
	String centroidOffsetYs;
	

	CentroidOffsetsDisplayValues centroidOffsetsDisplayValues;
	ActuatorDeltasDisplayValues actuatorDeltasDisplayValues;
	String actDeltaHeats;

	String centroidNbrs;
	String actuatorDeltas;


	public int getWaitingForSecs() {
		return waitingForSecs;
	}

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

	public String getCentroidOffsetXs() {
		return centroidOffsetXs;
	}

	public void setCentroidOffsetXs(String centroidOffsetXs) {
		this.centroidOffsetXs = centroidOffsetXs;
	}

	public String getCentroidOffsetYs() {
		return centroidOffsetYs;
	}

	public void setCentroidOffsetYs(String centroidOffsetYs) {
		this.centroidOffsetYs = centroidOffsetYs;
	}

	public String getCentroidNbrs() {
		return centroidNbrs;
	}

	public void setCentroidNbrs(String centroidNbrs) {
		this.centroidNbrs = centroidNbrs;
	}

	public String getActDeltaHeats() {
		return actDeltaHeats;
	}

	public void setActDeltaHeats(String actDeltaHeats) {
		this.actDeltaHeats = actDeltaHeats;
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

	@Lock(LockType.READ)
	public CentroidOffsetsDisplayValues getCentroidOffsetsDisplayValues() {
		return centroidOffsetsDisplayValues;
	}

	public void setCentroidOffsetsDisplayValues(CentroidOffsetsDisplayValues centroidOffsetsDisplayValues) {
		this.centroidOffsetsDisplayValues = centroidOffsetsDisplayValues;
	}
	
	public void setAndEncodeCentroidOffsetsDisplayValues(CentroidOffsetsDisplayValues centroidOffsetsDisplayValues) {
		
		this.centroidOffsetsDisplayValues = centroidOffsetsDisplayValues;
		
		// encode centroid offsets
		List<FloatPoint> centroidOffsets = Arrays.asList(getCentroidOffsetsDisplayValues().getCartesianCentroidOffsets());
		setCentroidOffsetXs(FloatPointListEncoder.encodeXList(centroidOffsets));
		setCentroidOffsetYs(FloatPointListEncoder.encodeYList(centroidOffsets));
		
	}


	@Lock(LockType.READ)
	public ActuatorDeltasDisplayValues getActuatorDeltasDisplayValues() {
		return actuatorDeltasDisplayValues;
	}

	public void setActuatorDeltasDisplayValues(ActuatorDeltasDisplayValues actuatorDeltasDisplayValues) {
		this.actuatorDeltasDisplayValues = actuatorDeltasDisplayValues;
	}

	public void setAndEncodeActuatorDeltasDisplayValues(ActuatorDeltasDisplayValues actuatorDeltasDisplayValues) {
		this.actuatorDeltasDisplayValues = actuatorDeltasDisplayValues;
		
		String actDeltas = FloatListEncoder.encodeList(actuatorDeltasDisplayValues.getM1ActuatorCmds());
		setActuatorDeltas(actDeltas);
		String actDeltaHeats = heatMap(actuatorDeltasDisplayValues.getM1ActuatorCmds());
		setActDeltaHeats(actDeltaHeats);
		
	}
	
	public void setAndEncodeCentroidMap(CentroidMap centroidMap) {
		
		List<FloatPoint> centroids = FloatPointListEncoder.decodeList(centroidMap.getCentroidMapData());
		setCentroidXs(FloatPointListEncoder.encodeXList(centroids));
		setCentroidYs(FloatPointListEncoder.encodeYList(centroids));
	}
	
	public void displaySubimageCentroids(CentroidMap centroidMap) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displaySubimageCentroids"));
		
		setAndEncodeCentroidMap(centroidMap);
				
		pendingDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROIDS);
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displaySubimageCentroids"));

		
	}

	public boolean displaySubimageCentroids(CentroidMap centroidMap, int type, String message) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displaySubimageCentroids"));

		setAndEncodeCentroidMap(centroidMap);
				
		pendingDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROIDS, type, message);
		
		waitForReturnState();

		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displaySubimageCentroids"));

		return (returnState.intValue() == 1) ? true : false; 
	}

	public void displayCentroidOffsets(CentroidOffsetsDisplayValues centroidOffsetsDisplayValues) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayCentroidOffsets"));
		// set the offset display values, this also encodes
		setAndEncodeCentroidOffsetsDisplayValues(centroidOffsetsDisplayValues);
					
		// set the pending display and wait for return
		pendingDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROID_OFFSETS);
		
		waitForReturnState();	

		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayCentroidOffsets"));
	}
	
	public void displayActuatorDeltas(ActuatorDeltasDisplayValues actuatorDeltasDisplayValues) {
		
		logger.info(MessageGenerator.generateMessage("waitForUser.start", "displayActuatorDeltas"));

		setAndEncodeActuatorDeltasDisplayValues(actuatorDeltasDisplayValues);
				
		pendingDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_ACTUATOR_DELTAS);
		
		waitForReturnState();
		
		logger.info(MessageGenerator.generateMessage("waitForUser.success", "displayActuatorDeltas"));
		
	}

	private void waitForReturnState() {
		
		returnState = null;
		// here we wait until the return state changes
		while(returnState == null) {
			
			waitingForSecs++;
			
			Utils.waitFor(1000);
		}
		
		waitingForSecs = 0;

	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getActuatorDeltas() {
		return actuatorDeltas;
	}

	public void setActuatorDeltas(String actuatorDeltas) {
		this.actuatorDeltas = actuatorDeltas;
	}


	public String heatMap(float[][] actDeltas) {

		// zero: #ffffff

		// red: light to dark
		// #fee0d2 - 10%
		// #fc9272 - 50%
		// #de2d26 - 100%

		// blue: light to dark
		// #deebf7 - 10%
		// #9ecae1 - 50%
		// #3182bd - 100%

		// create a map of % to red, % to blue, % to green for each of the positive and negative values


		float max = 0.0f;
		for (float[] fArray : actDeltas) {
			for (float act : fArray) {
				max = Math.abs(act) > max ? Math.abs(act) : max;
			}
		}

		StringBuffer buf = new StringBuffer();
		// each value is compared to max to get the relative heat
		for (float[] fArray : actDeltas) {
			for (float act : fArray) {

				float percent = act / max;
				boolean isNeg = percent < 0;
				percent = Math.abs(percent);
				String heat = "#000000";
				// positive heat map
				if (percent <= 0.1f) {
					heat = interpolate(percent, isNeg, 0, 1, 0.0f, 0.1f);
				} else if (percent <= 0.5f) {
					heat = interpolate(percent, isNeg, 1, 2, 0.1f, 0.5f);
				} else {
					heat = interpolate(percent, isNeg, 2, 3, 0.5f, 1.0f);
				}

				buf.append(heat + ",");
			}
		}

		buf.deleteCharAt(buf.length()-1);
		return buf.toString();
	}

	public String interpolate(float value, boolean isNeg, int startIndex, int endIndex, float startValue, float endValue) {
		
		int rValPos[] = { 0xff, 0xfe, 0xfc, 0xde };
		int gValPos[] = { 0xff, 0xe0, 0x92, 0x2d };
		int bValPos[] = { 0xff, 0xd2, 0x72, 0x26 };

		int rValNeg[] = { 0xff, 0xde, 0x9e, 0x31 };
		int gValNeg[] = { 0xff, 0xeb, 0xca, 0x82 };
		int bValNeg[] = { 0xff, 0xf7, 0xe1, 0xbd };

		
		// value is what percent of its range?
		float percent = (value - startValue)/(endValue - startValue);
		
		int rVal = 0;
		int gVal = 0;
		int bVal = 0;
		
		if (isNeg) {
			rVal = (int)(rValNeg[startIndex] + (rValNeg[endIndex] - rValNeg[startIndex]) * percent); 
			gVal = (int)(gValNeg[startIndex] + (gValNeg[endIndex] - gValNeg[startIndex]) * percent); 
			bVal = (int)(bValNeg[startIndex] + (bValNeg[endIndex] - bValNeg[startIndex]) * percent); 
		} else {
			rVal = (int)(rValPos[startIndex] + (rValPos[endIndex] - rValPos[startIndex]) * percent); 
			gVal = (int)(gValPos[startIndex] + (gValPos[endIndex] - gValPos[startIndex]) * percent); 
			bVal = (int)(bValPos[startIndex] + (bValPos[endIndex] - bValPos[startIndex]) * percent); 			
		}
		
		return "#" + String.format("%02X", rVal) + String.format("%02X", gVal) + String.format("%02X", bVal);
		
	}
}
