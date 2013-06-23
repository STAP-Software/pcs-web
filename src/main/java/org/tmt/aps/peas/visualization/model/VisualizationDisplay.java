package org.tmt.aps.peas.visualization.model;

public class VisualizationDisplay {

	public static final int DISPLAY_TYPE_CENTROIDS = 1;
	public static final int DISPLAY_TYPE_CENTROID_OFFSETS = 2;
	// TODO: add the others
	
	
	int displayType;
	
	
	public VisualizationDisplay(int type) {
		this.displayType = type;
	}

	public int getDisplayType() {
		return displayType;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}
	
	
	
}
