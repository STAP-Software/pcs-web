/**
 * @author Scott Michaels
 * Copyright (C) 2015 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.ui;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.model.PassiveTiltProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.VisualizationDisplayMgmt;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;

@Named
@SessionScoped
public class VisualizationController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	CentroidMapMgmt centroidMapMgmt;
	@EJB
	VisualizationDisplayMgmt visualizationDisplayMgmt;
	@EJB
	GraphicDisplayMgmt graphicDisplayMgmt;
	@EJB
	ConstantsCache constantsCache;

	@Inject
	ProcedureController procedureController;

	String centDefPassiveTiltXs;
	String centDefPassiveTiltYs;

	List<FloatPoint> refDefValueListPassiveTilt;

	boolean showSegments = true;
	boolean showSegNums = true;
	boolean showActNums = true;
	boolean showActVals = true;
	float offsetScale = 100.0f;

	boolean centroidDisplayEnabled;
	boolean centroidOffsetDisplayEnabled;
	boolean avgCentroidOffsetDisplayEnabled;
	boolean actuatorDeltaDisplayEnabled;

	String act1Pos;
	String actDeltaHeats;

	@PostConstruct
	private void init() {

		try {
			// TODO: do other queries as each new procedure type is added
			RefBeamMap refDefMapPassiveTilt = centroidMapMgmt.getRefBeamDefMap(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
			List<FloatPoint> refDefValueListPassiveTilt = refDefMapPassiveTilt.getCentroidMap().getValues();
			float[] xArray = FloatPointListEncoder.extractXArray(refDefValueListPassiveTilt);
			centDefPassiveTiltXs = FloatListEncoder.encodeList(xArray);
			float[] yArray = FloatPointListEncoder.extractYArray(refDefValueListPassiveTilt);
			centDefPassiveTiltYs = FloatListEncoder.encodeList(yArray);

			act1Pos = FloatListEncoder.encodeList(constantsCache.getPrimaryMirrorConstants().getAct1Pos());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCentDefXs() {
		// determine which procedure type we are in
		if (procedureController.getProcedureType() != null) {
			if (procedureController.getProcedureType().isPassiveTilt()) {
				return centDefPassiveTiltXs;
			}
		}
		return centDefPassiveTiltXs; // default
	}

	public void setCentDefXs(String str) {
	}

	public String getCentDefYs() {
		// determine which procedure type we are in
		if (procedureController.getProcedureType() != null) {
			if (procedureController.getProcedureType().isPassiveTilt()) {
				return centDefPassiveTiltYs;
			}
		}
		return centDefPassiveTiltXs; // default
	}

	public void setCentDefYs(String str) {
	}

	public boolean isShowSegments() {
		return showSegments;
	}

	public void setShowSegments(boolean showSegments) {
		this.showSegments = showSegments;
	}

	public boolean isShowSegNums() {
		return showSegNums;
	}

	public void setShowSegNums(boolean showSegNums) {
		this.showSegNums = showSegNums;
	}

	public boolean isShowActNums() {
		return showActNums;
	}

	public void setShowActNums(boolean showActNums) {
		this.showActNums = showActNums;
	}

	public boolean isShowActVals() {
		return showActVals;
	}

	public void setShowActVals(boolean showActVals) {
		this.showActVals = showActVals;
	}

	public boolean isCentroidDisplayEnabled() {
		return centroidDisplayEnabled;
	}

	public boolean isCentroidOffsetDisplayEnabled() {
		return centroidOffsetDisplayEnabled;
	}

	public boolean isAvgCentroidOffsetDisplayEnabled() {
		return avgCentroidOffsetDisplayEnabled;
	}

	public boolean isActuatorDeltaDisplayEnabled() {
		return actuatorDeltaDisplayEnabled;
	}

	public String getCentroidXs() {
		return graphicDisplayMgmt.getCentroidXs();
	}

	public void setCentroidXs(String centroidXs) {
	}

	public String getCentroidYs() {
		return graphicDisplayMgmt.getCentroidYs();
	}

	public void setCentroidYs(String centroidYs) {
	}

	public String getCentroidOffsetXs() {
		return graphicDisplayMgmt.getCentroidOffsetXs();
	}

	public void setCentroidOffsetXs(String centroidXs) {
	}

	public String getCentroidOffsetYs() {
		return graphicDisplayMgmt.getCentroidOffsetYs();
	}

	public void setCentroidOffsetYs(String centroidYs) {
	}

	public String getCentroidNbrs() {
		return graphicDisplayMgmt.getCentroidNbrs();
	}

	public void setCentroidNbrs(String centroidNbrs) {
		graphicDisplayMgmt.setCentroidNbrs(centroidNbrs);
	}

	public float getOffsetScale() {
		return offsetScale;
	}

	public void setOffsetScale(float offsetScale) {
		this.offsetScale = offsetScale;
	}

	public CentroidOffsetsDisplayValues getCentroidOffsetsDisplayValues() {
		return graphicDisplayMgmt.getCentroidOffsetsDisplayValues();
	}

	public void setCentroidOffsetsDisplayValues(CentroidOffsetsDisplayValues centroidOffsetsDisplayValues) {
	}

	public String getAct1Pos() {
		return act1Pos;
	}

	public void setAct1Pos(String act1Pos) {
	};

	public String getActuatorDeltas() {
		return graphicDisplayMgmt.getActuatorDeltas();
	}

	public void setActuatorDeltas(String z) {
	};

	public String getActDeltaHeats() {
		return actDeltaHeats;
	}

	public void setActDeltaHeats(String actDeltaHeats) {
	}

	public void initVisualizationDisplays(Long procedureTypeId) {

		centroidDisplayEnabled = false;
		centroidOffsetDisplayEnabled = false;
		avgCentroidOffsetDisplayEnabled = false;
		actuatorDeltaDisplayEnabled = false;

		List<VisualizationDisplay> visualizationDisplayList = visualizationDisplayMgmt.findVisualizationDisplays(procedureTypeId);

		for (VisualizationDisplay visualizationDisplay : visualizationDisplayList) {

			switch (visualizationDisplay.getVisualizationDisplayId().intValue()) {
			case VisualizationDisplay.DISPLAY_TYPE_CENTROIDS:
				centroidDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_CENTROID_OFFSETS:
				centroidOffsetDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_AVG_CENTROID_OFFSETS:
				avgCentroidOffsetDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_ACTUATOR_DELTAS:
				actuatorDeltaDisplayEnabled = true;
				break;
			}
		}
	}

	public void updateCentroidOffsetDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawCentroidOffsets(" + showSegments + ", " + showSegNums + ", " + showActNums + ", " + showActVals + ")");
	}

	public void updateActDeltaDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawActDeltas(" + showSegments + ", " + showSegNums + ", " + showActNums + ", " + showActVals + ")");
	}

	public void doPopulateCentroidDisplay() {

		// FIXME - for now, just the first iteration
		Procedure procedure = procedureController.getProcedure();

		if (procedure.getProcedureCcdFrameList() != null) {

			// centroids
			CentroidMap centroidMap = procedure.getProcedureCcdFrameList().get(0).getCentroidMap();
			List<FloatPoint> centroids = FloatPointListEncoder.decodeList(centroidMap.getCentroidMapData());
			graphicDisplayMgmt.setCentroidXs(FloatPointListEncoder.encodeXList(centroids));
			graphicDisplayMgmt.setCentroidYs(FloatPointListEncoder.encodeYList(centroids));

		}
	}

	public void doPopulateCentroidOffsetDisplay() {
		offsetScale = 100.0f; // initialize at 100%

		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		// TODO: add logic for other procedures as they arrive
		if (procedureOutput instanceof PassiveTiltProcedureOutput) {

			PassiveTiltProcedureOutput ptpo = (PassiveTiltProcedureOutput) procedureOutput;
			graphicDisplayMgmt.setCentroidOffsetsDisplayValues((CentroidOffsetsDisplayValues) ptpo);
		}
		List<FloatPoint> centroidOffsets = Arrays.asList(graphicDisplayMgmt.getCentroidOffsetsDisplayValues().getCentroidOffsets());
		graphicDisplayMgmt.setCentroidOffsetXs(FloatPointListEncoder.encodeXList(centroidOffsets));
		graphicDisplayMgmt.setCentroidOffsetYs(FloatPointListEncoder.encodeYList(centroidOffsets));

	}

	public void doPopulateAvgCentroidOffsetDisplay() {
		// TODO: implement
	}

	public void doPopulateActuatorDeltaDisplay() {
		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		if (procedureOutput instanceof PassiveTiltProcedureOutput) {

			PassiveTiltProcedureOutput ptpo = (PassiveTiltProcedureOutput) procedureOutput;
			String actDeltas = FloatListEncoder.encodeList(ptpo.getM1ActuatorCmds());
			graphicDisplayMgmt.setActuatorDeltas(actDeltas);
			actDeltaHeats = heatMap(ptpo.getM1ActuatorCmds());

		}

	}

	private String heatMap(float[][] actDeltas) {

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