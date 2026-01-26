/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * Visualization display metadata Entity class representing a row in the VisualizationDisplay table
 * @author smichaels
 *
 */
@Entity
@Table(name = "VisualizationDisplay")
@NamedQueries({
	@NamedQuery(name = "findAllVisualizationDisplays", query = "SELECT p from VisualizationDisplay p " )
})
public class VisualizationDisplay extends UserPrompt {

	public static final int DISPLAY_TYPE_CENTROIDS = 1;
	public static final int DISPLAY_TYPE_CENTROID_OFFSETS = 2;
	public static final int DISPLAY_TYPE_AVG_PT_CENTROID_OFFSETS = 3;
	public static final int DISPLAY_TYPE_AVG_FS_CENTROID_OFFSETS = 4;
	public static final int DISPLAY_TYPE_ACTUATOR_DELTAS = 5;
	public static final int DISPLAY_TYPE_EDGE_HEIGHTS = 6;
	public static final int DISPLAY_TYPE_EDGE_RESIDUALS = 7;
	public static final int DISPLAY_TYPE_SUFS_CENTROID_OFFSETS = 8;
	public static final int DISPLAY_TYPE_AVG_SUFS_CENTROID_OFFSETS = 9;
	public static final int DISPLAY_TYPE_SINGLE_FILTER_EDGE_HEIGHTS = 10;

	
	@Id
	private Long visualizationDisplayId;
	
	@Column(nullable=false, length=100)
	private String displayName;
	

	public VisualizationDisplay() {
		super("");
	}
	
	public VisualizationDisplay(int visualizationDisplayType) {
		super("");
		this.visualizationDisplayId = Long.valueOf(visualizationDisplayType);
	}
	
	public VisualizationDisplay(int visualizationDisplayType, int buttonType, String message) {
		super("", buttonType, message);
		this.visualizationDisplayId = Long.valueOf(visualizationDisplayType);
	}

	
	public Long getVisualizationDisplayId() {
		return visualizationDisplayId;
	}

	public void setVisualizationDisplayId(Long visualizationDisplayId) {
		this.visualizationDisplayId = visualizationDisplayId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isDisplayTypeCentroids() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_CENTROIDS;
	}
	
	public boolean isDisplayTypeCentroidOffsets() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_CENTROID_OFFSETS;
	}
	
	public boolean isDisplayTypeAvgPtCentroidOffsets() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_AVG_PT_CENTROID_OFFSETS;
	}
	
	public boolean isDisplayTypeAvgFsCentroidOffsets() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_AVG_FS_CENTROID_OFFSETS;
	}
	
	public boolean isDisplayTypeActuatorDeltas() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_ACTUATOR_DELTAS;
	}
	
	public boolean isDisplayTypeEdgeHeights() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_EDGE_HEIGHTS;
	}

	public boolean isDisplayTypeSingleFilterEdgeHeights() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_SINGLE_FILTER_EDGE_HEIGHTS;
	}

	public boolean isDisplayTypeEdgeResiduals() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_EDGE_RESIDUALS;
	}
	
	public boolean isDisplayTypeSufsCentroidOffsets() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_SUFS_CENTROID_OFFSETS;
	}
	
	public boolean isDisplayTypeAvgSufsCentroidOffsets() {
		return visualizationDisplayId.intValue() == DISPLAY_TYPE_AVG_SUFS_CENTROID_OFFSETS;
	}
	
}
