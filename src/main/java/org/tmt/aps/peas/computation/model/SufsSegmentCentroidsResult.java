package org.tmt.aps.peas.computation.model;

/**
 * Computation data result class for <b>generateSufsSegmentCentroids<b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#generateSufsSegmentCentroids(FindCentroidsResult, int[][])
 */
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
