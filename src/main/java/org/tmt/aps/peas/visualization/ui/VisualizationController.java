/**
 * @author Scott Michaels
 * Copyright (C) 2015 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.ui;

import java.io.Serializable;
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
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.model.PassiveTiltProcedureOutput;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureIterationOutput;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.VisualizationDisplayMgmt;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.AvgCentroidOffsetsDisplayValues;
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
	@EJB
	SubimageDefCache subimageDefCache;

	@Inject
	ProcedureController procedureController;

	List<FloatPoint> refDefValueListPassiveTilt;

	boolean showSegments = true;
	boolean showSegNums = true;
	boolean showHeat = true;
	boolean showHeatCircles = false;
	boolean showActVals = true;
	float offsetScale = 100.0f;
	float maxOffset = 0.0f;

	boolean centroidDisplayEnabled;
	boolean centroidOffsetDisplayEnabled;
	boolean avgCentroidOffsetDisplayEnabled;
	boolean actuatorDeltaDisplayEnabled;

	String act1Pos;
	
	VisualizationDisplay currentDisplay;

	@PostConstruct
	private void init() {

		try {
			
			act1Pos = FloatListEncoder.encodeList(constantsCache.getPrimaryMirrorConstants().getAct1Pos());

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}
	
	// segment centers
	public String getSegCentDefXs() {
		// determine which procedure type we are in
		SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
		return subimageDefList.getInteriorCentroidXsAsString();
	}
	public String getSegCentDefYs() {
		// determine which procedure type we are in
		SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
		return subimageDefList.getInteriorCentroidYsAsString();
	}


	public String getCentDefXs() {
		// determine which procedure type we are in
		// TODO: we are deciding that AvgCentroidOffset displays always use 36.  We may need to generalize this
		if (procedureController.getProcedure() != null && !procedureController.getProcedure().getProcedureType().isCenterTelescope() && currentDisplay != null && !currentDisplay.isDisplayTypeAvgCentroidOffsets()) {
			
			PupilMaskType pupilMaskType = procedureController.getProcedure().getProcedureConfigSet().getProcedureConfig().getPupilMaskType();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(pupilMaskType.getPupilMaskTypeId());
			return subimageDefList.getInteriorCentroidXsAsString();
		}
		SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
		return subimageDefList.getInteriorCentroidXsAsString(); // default
	}

	public String getCentDefYs() {
		// determine which procedure type we are in
		if (procedureController.getProcedure() != null && !procedureController.getProcedure().getProcedureType().isCenterTelescope() && currentDisplay != null  && !currentDisplay.isDisplayTypeAvgCentroidOffsets()) {
			
			PupilMaskType pupilMaskType = procedureController.getProcedure().getProcedureConfigSet().getProcedureConfig().getPupilMaskType();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(pupilMaskType.getPupilMaskTypeId());
			return subimageDefList.getInteriorCentroidYsAsString();
		}
		SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
		return subimageDefList.getInteriorCentroidYsAsString(); // default
	}

	public void setCentDefXs(String str) {}
	public void setCentDefYs(String str) {}
	public void setSegCentDefXs(String str) {}
	public void setSegCentDefYs(String str) {}

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

	public boolean isShowHeat() {
		return showHeat;
	}

	public void setShowHeat(boolean showHeat) {
		this.showHeat = showHeat;
	}

	public boolean isShowHeatCircles() {
		return showHeatCircles;
	}

	public void setShowHeatCircles(boolean showHeatCircles) {
		this.showHeatCircles = showHeatCircles;
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

	public String getAvgCentroidOffsetXs() {
		return graphicDisplayMgmt.getAvgCentroidOffsetXs();
	}

	public void setAvgCentroidOffsetXs(String centroidXs) {
	}

	public String getAvgCentroidOffsetYs() {
		return graphicDisplayMgmt.getAvgCentroidOffsetYs();
	}

	public void setAvgCentroidOffsetYs(String centroidYs) {
	}

	public String getCentroidNbrs() {
		return graphicDisplayMgmt.getCentroidNbrs();
	}

	public void setCentroidNbrs(String centroidNbrs) {
		graphicDisplayMgmt.setCentroidNbrs(centroidNbrs);
	}

	public void setMaxOffset(float maxOffset) {
		// TODO:
	}
	
	public float getMaxOffset() {
		if (graphicDisplayMgmt.getCentroidOffsetsDisplayValues() == null) return 0.0f;
		
		return graphicDisplayMgmt.getCentroidOffsetsDisplayValues().getCentroidStatsResult().getMaxOffset();
	}
	
	public void setMaxAvgOffset(float maxOffset) {
		// TODO:
	}
	
	public float getMaxAvgOffset() {
		if (graphicDisplayMgmt.getAvgCentroidOffsetsDisplayValues() == null) return 0.0f;
		
		return graphicDisplayMgmt.getAvgCentroidOffsetsDisplayValues().getAvgCentroidStatsResult().getMaxOffset();
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

	public void setCentroidOffsetsDisplayValues(CentroidOffsetsDisplayValues centroidOffsetsDisplayValues) {}

	public AvgCentroidOffsetsDisplayValues getAvgCentroidOffsetsDisplayValues() {
		return graphicDisplayMgmt.getAvgCentroidOffsetsDisplayValues();
	}

	public void setAvgCentroidOffsetsDisplayValues(AvgCentroidOffsetsDisplayValues avgCentroidOffsetsDisplayValues) {}

	public ActuatorDeltasDisplayValues getActuatorDeltasDisplayValues() {
		return graphicDisplayMgmt.getActuatorDeltasDisplayValues();
	}
	
	public void setActuatorDeltasDisplayValues(ActuatorDeltasDisplayValues values) {}
	
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
		return graphicDisplayMgmt.getActDeltaHeats();
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
		requestContext.execute("drawCentroidOffsets(" + showSegments + ", " + showSegNums + ")");
	}

	public void updateAvgCentroidOffsetDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawAvgCentroidOffsets(" + showSegments + ", " + showSegNums + ")");
	}

	public void updateActDeltaDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawActDeltas(" + showSegments + ", " + showSegNums + ", " + showActVals + ", " + showHeat  + ", " + showHeatCircles + ")");
	}

	public void doPopulateCentroidDisplay(int iteration) {

		// FIXME - for now, just the first iteration
		Procedure procedure = procedureController.getProcedure();

		if (procedure.getProcedureCcdFrameList() != null) {

			// centroids
			CentroidMap centroidMap = procedure.getProcedureCcdFrameList().get(iteration).getCentroidMap();
			
			graphicDisplayMgmt.setAndEncodeCentroidMap(centroidMap);
			
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROIDS);
	}

	
	public void doPopulateCentroidOffsetDisplay(int iteration) {
		offsetScale = 100.0f; // initialize at 100%

		ProcedureIterationOutput procedureIterationOutput = procedureController.getProcedure().getProcedureOutput().getProcedureIterationOutputList().get(iteration);

		if (procedureIterationOutput instanceof CentroidOffsetsDisplayValues) {

			graphicDisplayMgmt.setAndEncodeCentroidOffsetsDisplayValues((CentroidOffsetsDisplayValues) procedureIterationOutput);
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROID_OFFSETS);		
	}
	

	public void doPopulateAvgCentroidOffsetDisplay() {
		offsetScale = 100.0f; // initialize at 100%

		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		if (procedureOutput instanceof AvgCentroidOffsetsDisplayValues) {

			graphicDisplayMgmt.setAndEncodeAvgCentroidOffsetsDisplayValues((AvgCentroidOffsetsDisplayValues) procedureOutput);
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_AVG_CENTROID_OFFSETS);		
	}

	public void doPopulateActuatorDeltaDisplay() {
		
		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		if (procedureOutput instanceof PassiveTiltProcedureOutput) {

			PassiveTiltProcedureOutput ptpo = (PassiveTiltProcedureOutput) procedureOutput;
			
			graphicDisplayMgmt.setAndEncodeActuatorDeltasDisplayValues(ptpo);
			
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_ACTUATOR_DELTAS);		

	}

	public VisualizationDisplay getCurrentDisplay() {
		return currentDisplay;
	}

	public void setCurrentDisplay(VisualizationDisplay currentDisplay) {
		this.currentDisplay = currentDisplay;
	}




}