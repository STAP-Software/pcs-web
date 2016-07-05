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
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.PointListEncoder;
import org.tmt.aps.peas.computation.model.SubimageDefList;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureIterationOutput;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.VisualizationDisplayMgmt;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.AvgFsCentroidOffsetsDisplayValues;
import org.tmt.aps.peas.visualization.model.AvgPtCentroidOffsetsDisplayValues;
import org.tmt.aps.peas.visualization.model.AvgSufsCentroidOffsetsDisplayValues;
import org.tmt.aps.peas.visualization.model.CentroidOffsetsDisplayValues;
import org.tmt.aps.peas.visualization.model.EdgeHeightsDisplayValues;
import org.tmt.aps.peas.visualization.model.SufsCentroidOffsetsDisplayValues;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;

/**
 * JSF Controller class that controls the rendering and functioning of visualization displays
 */
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
	boolean showEdgeNums = true;
	boolean showHeat = true;
	boolean showHeatCircles = false;
	boolean showActVals = true;
	float offsetScale = 100.0f;
	float maxOffset = 0.0f;

	boolean centroidDisplayEnabled;
	boolean centroidOffsetDisplayEnabled;
	boolean avgPtCentroidOffsetDisplayEnabled;
	boolean avgFsCentroidOffsetDisplayEnabled;
	boolean avgSufsCentroidOffsetDisplayEnabled;
	boolean actuatorDeltaDisplayEnabled;
	boolean edgeHeightsDisplayEnabled;
	boolean edgeResidualsDisplayEnabled;
	boolean sufsCentroidOffsetDisplayEnabled;

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
	
	/**
	 * @return the x-coordinates of the segment centers; used by javascript to render segments
	 */
	public String getSegCentDefXs() {
		// determine which procedure type we are in
		SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
		return subimageDefList.getInteriorCentroidXsAsString();
	}
	/**
	 * @return the y-coordinates of the segment centers; used by javascript to render segments
	 */
	public String getSegCentDefYs() {
		// determine which procedure type we are in
		SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
		return subimageDefList.getInteriorCentroidYsAsString();
	}

	/**
	 * @return the x coordinates of segment edge centers
	 */
	public String getEdgeXs() {
		// determine which procedure type we are in
		List<Point> nEdges = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getnEdge());
		return PointListEncoder.encodeXList(nEdges);
	}
	
	/**
	 * @return the y coordinates of segment edge centers
	 */
	public String getEdgeYs() {
		// determine which procedure type we are in
		List<Point> nEdges = Arrays.asList(constantsCache.getPrimaryMirrorConstants().getnEdge());
		return PointListEncoder.encodeYList(nEdges);
	}

	/**
	 * @return the angle of the normal to the edge for each segment edge
	 */
	public String getEdgeNormAngles() {
		int[] edgeNormAngles = constantsCache.getPrimaryMirrorConstants().getNormAngle();
		return IntegerListEncoder.encodeList(edgeNormAngles);
	}

	/**
	 * @return the x-coordinates of the ideal centroid locations for the mask type currently being displayed
	 */
	public String getCentDefXs() {
		// determine which procedure type we are in
		
		if (procedureController.getProcedure() != null && procedureController.getProcedure().getProcedureType().isSufs()) {
			FloatPoint[] coords = constantsCache.getPrimaryMirrorSegmentConstants().getSufsSpotCoordinates();
			return FloatPointListEncoder.encodeXList(coords);
			
		} else if (procedureController.getProcedure() != null && !procedureController.getProcedure().getProcedureType().isCenterTelescope() && currentDisplay != null && !currentDisplay.isDisplayTypeAvgPtCentroidOffsets()) {
			
			PupilMaskType pupilMaskType = procedureController.getProcedure().getProcedureConfigSet().getProcedureConfig().getPupilMaskType();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(pupilMaskType.getPupilMaskTypeId());
			return subimageDefList.getInteriorCentroidXsAsString();
		} else {
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
			return subimageDefList.getInteriorCentroidXsAsString(); // default
		}
	}

	/**
	 * @return the y-coordinates of the ideal centroid locations for the mask type currently being displayed
	 */
	public String getCentDefYs() {
		
		// determine which procedure type we are in
		if (procedureController.getProcedure() != null && procedureController.getProcedure().getProcedureType().isSufs()) {
			FloatPoint[] coords = constantsCache.getPrimaryMirrorSegmentConstants().getSufsSpotCoordinates();
			return FloatPointListEncoder.encodeYList(coords);
			
		} else if (procedureController.getProcedure() != null && !procedureController.getProcedure().getProcedureType().isCenterTelescope() && currentDisplay != null  && !currentDisplay.isDisplayTypeAvgPtCentroidOffsets()) {
			
			PupilMaskType pupilMaskType = procedureController.getProcedure().getProcedureConfigSet().getProcedureConfig().getPupilMaskType();
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(pupilMaskType.getPupilMaskTypeId());
			return subimageDefList.getInteriorCentroidYsAsString();
		} else {
			SubimageDefList subimageDefList = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
			return subimageDefList.getInteriorCentroidYsAsString(); // default
		}
	}

	public void setCentDefXs(String str) {}
	public void setCentDefYs(String str) {}
	public void setEdgeXs(String str) {}
	public void setEdgeYs(String str) {}
	public void setEdgeNormAngles(String str) {}
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
	
	public boolean isShowEdgeNums() {
		return showEdgeNums;
	}

	public void setShowEdgeNums(boolean showEdgeNums) {
		this.showEdgeNums = showEdgeNums;
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
		return centroidOffsetDisplayEnabled && !sufsCentroidOffsetDisplayEnabled;
	}

	public boolean isAvgPtCentroidOffsetDisplayEnabled() {
		return avgPtCentroidOffsetDisplayEnabled;
	}

	public boolean isAvgFsCentroidOffsetDisplayEnabled() {
		return avgFsCentroidOffsetDisplayEnabled;
	}

	public boolean isAvgSufsCentroidOffsetDisplayEnabled() {
		return avgSufsCentroidOffsetDisplayEnabled;
	}

	public boolean isActuatorDeltaDisplayEnabled() {
		return actuatorDeltaDisplayEnabled;
	}

	public boolean isEdgeHeightsDisplayEnabled() {
		return edgeHeightsDisplayEnabled;
	}

	public void setEdgeHeightsDisplayEnabled(boolean edgeHeightsDisplayEnabled) {
		this.edgeHeightsDisplayEnabled = edgeHeightsDisplayEnabled;
	}

	public boolean isEdgeResidualsDisplayEnabled() {
		return edgeResidualsDisplayEnabled;
	}

	public void setEdgeResidualsDisplayEnabled(boolean edgeResidualsDisplayEnabled) {
		this.edgeResidualsDisplayEnabled = edgeResidualsDisplayEnabled;
	}

	public boolean isSufsCentroidOffsetDisplayEnabled() {
		return sufsCentroidOffsetDisplayEnabled;
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
	
	public String getGoodSpots() {
		return graphicDisplayMgmt.getGoodSpots();
	}

	public void setGoodSpots(String goodSpotsEncoded) {
	}

	public String getAvgPtCentroidOffsetXs() {
		return graphicDisplayMgmt.getAvgPtCentroidOffsetXs();
	}

	public void setAvgPtCentroidOffsetXs(String centroidXs) {
	}

	public String getAvgPtCentroidOffsetYs() {
		return graphicDisplayMgmt.getAvgPtCentroidOffsetYs();
	}

	public void setAvgPtCentroidOffsetYs(String centroidYs) {
	}

	public String getAvgFsCentroidOffsetXs() {
		return graphicDisplayMgmt.getAvgFsCentroidOffsetXs();
	}

	public void setAvgFsCentroidOffsetXs(String centroidXs) {
	}

	public String getAvgFsCentroidOffsetYs() {
		return graphicDisplayMgmt.getAvgFsCentroidOffsetYs();
	}

	public void setAvgFsCentroidOffsetYs(String centroidYs) {
	}

	public String getEdgeHeights() {
		return graphicDisplayMgmt.getEdgeHeights();
	}
	
	public void setEdgeHeights(String edgeHeights) {
		
	}
	
	public String getEdgeResiduals() {
		return graphicDisplayMgmt.getEdgeResiduals();
	}
	
	public void setEdgeResiduals(String edgeResiduals) {
		
	}
	
	public String getUseForAnalysis() {
		return graphicDisplayMgmt.getUseForAnalysis();
	}
	
	public void setUseForAnalysis(String useForAnalysis) {
		
	}
	
	public String getRowFlagOut() {
		return graphicDisplayMgmt.getRowFlagOut();
	}
	
	public void setRowFlagOut(String rowFlagOut) {
		
	}
	
	public String getCentroidNbrs() {
		return graphicDisplayMgmt.getCentroidNbrs();
	}

	public void setCentroidNbrs(String centroidNbrs) {
		graphicDisplayMgmt.setCentroidNbrs(centroidNbrs);
	}

	public void setMaxOffset(float maxOffset) {
		
	}
	
	public float getMaxOffset() {
		if (graphicDisplayMgmt.getCentroidOffsetsDisplayValues() == null || 
				graphicDisplayMgmt.getCentroidOffsetsDisplayValues().getCentroidStatsResult() == null) return 0.0f;
		
		return graphicDisplayMgmt.getCentroidOffsetsDisplayValues().getCentroidStatsResult().getMaxOffset();
	}
	
	public void setMaxAvgPtOffset(float maxOffset) {}
	
	public float getMaxAvgPtOffset() {
		if (graphicDisplayMgmt.getAvgPtCentroidOffsetsDisplayValues() == null) return 0.0f;
		
		return graphicDisplayMgmt.getAvgPtCentroidOffsetsDisplayValues().getAvgPtCentroidStatsResult().getMaxOffset();
	}

	public void setMaxAvgFsOffset(float maxOffset) {}

	public float getMaxAvgFsOffset() {
		if (graphicDisplayMgmt.getAvgFsCentroidOffsetsDisplayValues() == null) return 0.0f;
		
		return graphicDisplayMgmt.getAvgFsCentroidOffsetsDisplayValues().getAvgFsCentroidStatsResult().getMaxOffset();
	}
	
	public void setMaxSufsOffset(float maxOffset) {}

	public float getMaxSufsOffset() {
		if (graphicDisplayMgmt.getSufsCentroidOffsetsDisplayValues() == null) return 0.0f;
		
		return graphicDisplayMgmt.getSufsCentroidOffsetsDisplayValues().getSufsCentroidStatsResult().getMaxOffset()[getSufsGroupSegmentNumber()];
	}
	
	public void setMaxAvgSufsOffset(float maxOffset) {}

	public float getMaxAvgSufsOffset() {
		if (graphicDisplayMgmt.getAvgSufsCentroidOffsetsDisplayValues() == null) return 0.0f;
		
		return graphicDisplayMgmt.getAvgSufsCentroidOffsetsDisplayValues().getSufsCentroidStatsResult().getMaxOffset()[getAvgSufsGroupSegmentNumber()];
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

	public AvgPtCentroidOffsetsDisplayValues getAvgPtCentroidOffsetsDisplayValues() {
		return graphicDisplayMgmt.getAvgPtCentroidOffsetsDisplayValues();
	}

	public void setAvgPtCentroidOffsetsDisplayValues(AvgPtCentroidOffsetsDisplayValues avgPtCentroidOffsetsDisplayValues) {}

	public AvgFsCentroidOffsetsDisplayValues getAvgFsCentroidOffsetsDisplayValues() {
		return graphicDisplayMgmt.getAvgFsCentroidOffsetsDisplayValues();
	}

	public void setAvgFsCentroidOffsetsDisplayValues(AvgFsCentroidOffsetsDisplayValues avgFsCentroidOffsetsDisplayValues) {}

	public SufsCentroidOffsetsDisplayValues getSufsCentroidOffsetsDisplayValues() {
		return graphicDisplayMgmt.getSufsCentroidOffsetsDisplayValues();
	}

	public void setSufsCentroidOffsetsDisplayValues(SufsCentroidOffsetsDisplayValues sufsCentroidOffsetsDisplayValues) {}

	
	public AvgSufsCentroidOffsetsDisplayValues getAvgSufsCentroidOffsetsDisplayValues() {
		return graphicDisplayMgmt.getAvgSufsCentroidOffsetsDisplayValues();
	}

	public void setAvgSufsCentroidOffsetsDisplayValues(AvgSufsCentroidOffsetsDisplayValues avgSufsCentroidOffsetsDisplayValues) {}

	
	public ActuatorDeltasDisplayValues getActuatorDeltasDisplayValues() {
		return graphicDisplayMgmt.getActuatorDeltasDisplayValues();
	}
		
	public void setActuatorDeltasDisplayValues(ActuatorDeltasDisplayValues values) {}
	
	public EdgeHeightsDisplayValues getEdgeHeightsDisplayValues() {
		return graphicDisplayMgmt.getEdgeHeightsDisplayValues();
	}
	
	public void setEdgeHeightsDisplayValues(EdgeHeightsDisplayValues values) {}
	
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

	public int getSufsGroupSegmentNumber() {
		return graphicDisplayMgmt.getSufsGroupSegmentNumber();
	}

	public void setSufsGroupSegmentNumber(int sufsGroupSegmentNumber) {
		graphicDisplayMgmt.setSufsGroupSegmentNumber(sufsGroupSegmentNumber);
	}

	public int getAvgSufsGroupSegmentNumber() {
		return graphicDisplayMgmt.getAvgSufsGroupSegmentNumber();
	}

	public void setAvgSufsGroupSegmentNumber(int avgSufsGroupSegmentNumber) {
		graphicDisplayMgmt.setAvgSufsGroupSegmentNumber(avgSufsGroupSegmentNumber);
	}

	/**
	 * Initializes enabled state for each visualization display type accessible from this controller for the currently rendered procedure type.
	 */
	public void initVisualizationDisplays(Long procedureTypeId) {

		centroidDisplayEnabled = false;
		centroidOffsetDisplayEnabled = false;
		avgPtCentroidOffsetDisplayEnabled = false;
		avgFsCentroidOffsetDisplayEnabled = false;
		avgSufsCentroidOffsetDisplayEnabled = false;
		actuatorDeltaDisplayEnabled = false;
		edgeHeightsDisplayEnabled = false;
		edgeResidualsDisplayEnabled = false;

		List<VisualizationDisplay> visualizationDisplayList = visualizationDisplayMgmt.findVisualizationDisplays(procedureTypeId);

		for (VisualizationDisplay visualizationDisplay : visualizationDisplayList) {

			switch (visualizationDisplay.getVisualizationDisplayId().intValue()) {
			case VisualizationDisplay.DISPLAY_TYPE_CENTROIDS:
				centroidDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_CENTROID_OFFSETS:
				centroidOffsetDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_AVG_PT_CENTROID_OFFSETS:
				avgPtCentroidOffsetDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_AVG_FS_CENTROID_OFFSETS:
				avgFsCentroidOffsetDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_SUFS_CENTROID_OFFSETS:
				sufsCentroidOffsetDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_AVG_SUFS_CENTROID_OFFSETS:
				avgSufsCentroidOffsetDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_ACTUATOR_DELTAS:
				actuatorDeltaDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_EDGE_HEIGHTS:
				edgeHeightsDisplayEnabled = true;
				break;
			case VisualizationDisplay.DISPLAY_TYPE_EDGE_RESIDUALS:
				edgeResidualsDisplayEnabled = true;
				break;
			}
		}
	}

	/**
	 * UI event listener for centroid offset display
	 * Calls javascript to redraw with updated values for 'showSegments' and 'showSegNums'
	 */
	public void updateCentroidOffsetDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawCentroidOffsets(" + showSegments + ", " + showSegNums + ")");
	}

	/**
	 * UI event listener for average PT centroid offset display
	 * Calls javascript to redraw with updated values for 'showSegments' and 'showSegNums'
	 */
	public void updateAvgPtCentroidOffsetDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawAvgPtCentroidOffsets(" + showSegments + ", " + showSegNums + ")");
	}

	/**
	 * UI event listener for averate FS centroid offset display
	 * Calls javascript to redraw with updated values for 'showSegments' and 'showSegNums'
	 */
	public void updateAvgFsCentroidOffsetDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawAvgFsCentroidOffsets(" + showSegments + ", " + showSegNums + ")");
	}

	/**
	 * UI event listener for actuator deltas display
	 * Calls javascript to redraw with updated values for 'showSegments', 'showSegNums', 'showActVals', 'showHeat', and 'showHeatCircles'
	 */
	public void updateActDeltaDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawActDeltas(" + showSegments + ", " + showSegNums + ", " + showActVals + ", " + showHeat  + ", " + showHeatCircles + ")");
	}

	/**
	 * UI event listener for edge heights display
	 * Calls javascript to redraw with updated values for 'showSegments', 'showSegNums' and 'showEdgeNums'
	 */
	public void updateEdgeHeightsDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawEdgeHeights(" + showSegments + ", " + showSegNums + ", " + showEdgeNums + ")");
	}

	/**
	 * UI event listener for edge residuals display
	 * Calls javascript to redraw with updated values for 'showSegments', 'showSegNums' and 'showEdgeNums'
	 */
	public void updateEdgeResidualsDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawEdgeResiduals(" + showSegments + ", " + showSegNums + ", " + showEdgeNums  + ")");
	}
	
	/**
	 * UI event listener for SUFS centroid offsets display
	 * Calls javascript to redraw the centroid offsets with current values
	 */
	public void updateSufsCentroidOffsetDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawSufsCentroidOffsets()");
	}

	/**
	 * UI event listener for average SUFS centroid offsets display
	 * Calls javascript to redraw the average centroid offsets with current values
	 */
	public void updateAvgSufsCentroidOffsetDisplayListener() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.execute("drawAvgSufsCentroidOffsets()");
	}

	/**
	 * JSF Action method to populate the centroid display data prior to rendering, called from the navigation menu dropdown
	 * Create ref map version only
	 */
	public void doPopulateCentroidDisplay() {

		doPopulateCentroidDisplay(0);
	}

	/**
	 * JSF Action method to populate the centroid display data prior to rendering, called from the navigation menu dropdown
	 * @param iteration the iteration to draw data from 
	 */
	public void doPopulateCentroidDisplay(int iteration) {

		Procedure procedure = procedureController.getProcedure();

		if (procedure.getProcedureCcdFrameList() != null) {

			// centroids
			CentroidMap centroidMap = procedure.getProcedureCcdFrameList().get(iteration).getCentroidMap();
			
			graphicDisplayMgmt.setAndEncodeCentroidMap(centroidMap);
			
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROIDS);
	}

	
	/**
	 * JSF Action method to populate the centroid offsets display data prior to rendering, called from the navigation menu dropdown
	 * @param iteration the iteration to draw data from 
	 */
	public void doPopulateCentroidOffsetDisplay(int iteration) {
		offsetScale = 100.0f; // initialize at 100%

		ProcedureIterationOutput procedureIterationOutput = procedureController.getProcedure().getProcedureOutput().getProcedureIterationOutputList().get(iteration);

		if (procedureIterationOutput instanceof CentroidOffsetsDisplayValues) {

			graphicDisplayMgmt.setAndEncodeCentroidOffsetsDisplayValues((CentroidOffsetsDisplayValues) procedureIterationOutput);
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_CENTROID_OFFSETS);		
	}
	
	/**
	 * JSF Action method to populate the averate passive tilt centroid display data prior to rendering, called from the navigation menu dropdown
	 */
	public void doPopulateAvgPtCentroidOffsetDisplay() {
		offsetScale = 100.0f; // initialize at 100%

		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		if (procedureOutput instanceof AvgPtCentroidOffsetsDisplayValues) {

			graphicDisplayMgmt.setAndEncodeAvgPtCentroidOffsetsDisplayValues((AvgPtCentroidOffsetsDisplayValues) procedureOutput);
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_AVG_PT_CENTROID_OFFSETS);		
	}

	/**
	 * JSF Action method to populate the average fine screen centroid display data prior to rendering, called from the navigation menu dropdown
	 */
	public void doPopulateAvgFsCentroidOffsetDisplay() {
		offsetScale = 100.0f; // initialize at 100%

		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		if (procedureOutput instanceof AvgFsCentroidOffsetsDisplayValues) {

			graphicDisplayMgmt.setAndEncodeAvgFsCentroidOffsetsDisplayValues((AvgFsCentroidOffsetsDisplayValues) procedureOutput);
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_AVG_FS_CENTROID_OFFSETS);		
	}


	/**
	 * JSF Action method to populate the SUFS centroid display data prior to rendering, called from the navigation menu dropdown
	 */
	public void doPopulateSufsCentroidOffsetDisplay(int iteration) {
		offsetScale = 100.0f; // initialize at 100%

		ProcedureIterationOutput procedureIterationOutput = procedureController.getProcedure().getProcedureOutput().getProcedureIterationOutputList().get(iteration);

		if (procedureIterationOutput instanceof SufsCentroidOffsetsDisplayValues) {

			// reset display to first segment
			setSufsGroupSegmentNumber(0);
			
			graphicDisplayMgmt.setAndEncodeSufsOffsetsDisplayValues((SufsCentroidOffsetsDisplayValues) procedureIterationOutput, 0);
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_SUFS_CENTROID_OFFSETS);		
	}

	/**
	 * JSF Action method to populate the average SUFS centroid display data prior to rendering, called from the navigation menu dropdown
	 */
	public void doPopulateAvgSufsCentroidOffsetDisplay() {
		offsetScale = 100.0f; // initialize at 100%

		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		if (procedureOutput instanceof AvgSufsCentroidOffsetsDisplayValues) {

			// reset display to first segment
			setAvgSufsGroupSegmentNumber(0);
			
			graphicDisplayMgmt.setAndEncodeAvgSufsOffsetsDisplayValues((AvgSufsCentroidOffsetsDisplayValues) procedureOutput, 0);
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_SUFS_CENTROID_OFFSETS);		
	}

	
	/**
	 * JSF Action method to populate the actuator delta display data prior to rendering, called from the navigation menu dropdown
	 */
	public void doPopulateActuatorDeltaDisplay() {
		
		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		if (procedureOutput instanceof ActuatorDeltasDisplayValues) {

			ActuatorDeltasDisplayValues addv = (ActuatorDeltasDisplayValues) procedureOutput;
			
			graphicDisplayMgmt.setAndEncodeActuatorDeltasDisplayValues(addv);
			
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_ACTUATOR_DELTAS);		

	}
	
	/**
	 * JSF Action method to populate the edge heights display data prior to rendering, called from the navigation menu dropdown
	 */
	public void doPopulateEdgeHeightsDisplay() {
		
		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		if (procedureOutput instanceof EdgeHeightsDisplayValues) {

			EdgeHeightsDisplayValues ehdv = (EdgeHeightsDisplayValues) procedureOutput;
			
			graphicDisplayMgmt.setAndEncodeEdgeHeightsDisplayValues(ehdv);
			
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_EDGE_HEIGHTS);		

	}
	
	/**
	 * JSF Action method to populate the edge residual display data prior to rendering, called from the navigation menu dropdown
	 */
	public void doPopulateEdgeResidualsDisplay() {
		
		ProcedureOutput procedureOutput = procedureController.getProcedure().getProcedureOutput();

		if (procedureOutput instanceof EdgeHeightsDisplayValues) {

			EdgeHeightsDisplayValues ehdv = (EdgeHeightsDisplayValues) procedureOutput;
			
			graphicDisplayMgmt.setAndEncodeEdgeResidualsDisplayValues(ehdv);
			
		}
		currentDisplay = new VisualizationDisplay(VisualizationDisplay.DISPLAY_TYPE_EDGE_RESIDUALS);		

	}

	/**
	 * JSF Action method called with the SUFS centroid offsets display 'Next' button is clicked.
	 * Increments the SUFS segment number and populates the centroid offsets for that segment
	 */
	public void doSufsCentroidOffsetsNext() {
		int sufsGroupSegmentNumber = (getSufsGroupSegmentNumber() < 6) ? getSufsGroupSegmentNumber()+1 : 0; 	
		setSufsGroupSegmentNumber(sufsGroupSegmentNumber);
		graphicDisplayMgmt.encodeSufsOffsetsForDisplay(sufsGroupSegmentNumber);
	}

	/**
	 * JSF Action method called with the SUFS centroid offsets display 'Back' button is clicked.
	 * Decrements the SUFS segment number and populates the centroid offsets for that segment
	 */
	public void doSufsCentroidOffsetsBack() {
		int sufsGroupSegmentNumber = (getSufsGroupSegmentNumber() >0) ? getSufsGroupSegmentNumber()-1 : 6; 	
		setSufsGroupSegmentNumber(sufsGroupSegmentNumber);
		graphicDisplayMgmt.encodeSufsOffsetsForDisplay(sufsGroupSegmentNumber);
	}
	
	/**
	 * JSF Action method called with the average SUFS centroid offsets display 'Next' button is clicked.
	 * Increments the SUFS segment number and populates the average centroid offsets for that segment
	 */
	public void doAvgSufsCentroidOffsetsNext() {
		int avgSufsGroupSegmentNumber = (getAvgSufsGroupSegmentNumber() < 6) ? getAvgSufsGroupSegmentNumber()+1 : 0; 	
		setAvgSufsGroupSegmentNumber(avgSufsGroupSegmentNumber);
		graphicDisplayMgmt.encodeAvgSufsOffsetsForDisplay(avgSufsGroupSegmentNumber);
	}

	/**
	 * JSF Action method called with the average SUFS centroid offsets display 'Back' button is clicked.
	 * Decrements the SUFS segment number and populates the average centroid offsets for that segment
	 */
	public void doAvgSufsCentroidOffsetsBack() {
		int avgSufsGroupSegmentNumber = (getAvgSufsGroupSegmentNumber() >0) ? getAvgSufsGroupSegmentNumber()-1 : 6; 	
		setAvgSufsGroupSegmentNumber(avgSufsGroupSegmentNumber);
		graphicDisplayMgmt.encodeAvgSufsOffsetsForDisplay(avgSufsGroupSegmentNumber);
	}
	

	/**
	 * @return the current display being rendered
	 */
	public VisualizationDisplay getCurrentDisplay() {
		return currentDisplay;
	}

	public void setCurrentDisplay(VisualizationDisplay currentDisplay) {
		this.currentDisplay = currentDisplay;
	}




}