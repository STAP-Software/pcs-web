package org.tmt.aps.peas.computation.model;

public class SufsSegmentCentroidsResult {

	FindCentroidsResult[] segmentCentroidResultList;
	
	public SufsSegmentCentroidsResult(FindCentroidsResult[] segmentCentroidResultList) {
		this.segmentCentroidResultList = segmentCentroidResultList;
	}

	public FindCentroidsResult[] getSegmentCentroidResultList() {
		return segmentCentroidResultList;
	}

	public void setSegmentCentroidResultList(FindCentroidsResult[] segmentCentroidResultList) {
		this.segmentCentroidResultList = segmentCentroidResultList;
	}

	
	
}
