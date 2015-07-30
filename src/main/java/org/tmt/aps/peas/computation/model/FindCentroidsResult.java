package org.tmt.aps.peas.computation.model;

import java.util.ArrayList;
import java.util.List;

import org.tmt.aps.peas.common.FloatPoint;

public class FindCentroidsResult {

	private List<FindCentResult> findCentResultList;
	

	
	public FindCentroidsResult(float[] xCent, float[] yCent, float[] intensity, float[] peak, int[] findCentStatus) {
		
		findCentResultList = new ArrayList<FindCentResult>();
		
		for (int i = 0; i< xCent.length; i++) {
			FindCentResult findCentResult = new FindCentResult(new FloatPoint(xCent[i], yCent[i]), intensity[i], peak[i], findCentStatus[i]);
			findCentResultList.add(findCentResult);
		}
	}
	
	public FindCentroidsResult(List<FloatPoint> centroidList, List<Float> intensity, List<Float> peak, List<Integer> findCentStatus) {
		
		findCentResultList = new ArrayList<FindCentResult>();
		
		for (int i = 0; i< centroidList.size(); i++) {
			FindCentResult findCentResult = new FindCentResult(centroidList.get(i), intensity.get(i), peak.get(i), findCentStatus.get(i));
			findCentResultList.add(findCentResult);
		}
	}
	
	public FindCentroidsResult(List<FloatPoint> centroidList) {
		
		findCentResultList = new ArrayList<FindCentResult>();
		
		for (int i = 0; i< centroidList.size(); i++) {
			FindCentResult findCentResult = new FindCentResult(centroidList.get(i), 0.0f, 0.0f, 0);
			findCentResultList.add(findCentResult);
		}
	}
	
	public List<FloatPoint> getCentroidList() {
		
		List<FloatPoint> centroids = new ArrayList<FloatPoint>();
		
		for (FindCentResult findCentResult : findCentResultList) {
			centroids.add(findCentResult.getCentroid());
		}

		return centroids;
	}

	public float[] getIntensityList() {
		
		float[] intensities = new float[findCentResultList.size()];
		int i=0;
		for (FindCentResult findCentResult : findCentResultList) {
			intensities[i++] = findCentResult.getSubimageIntensity();
		}

		return intensities;
	}
	
	public float[] getPeakList() {
		
		float[] peaks = new float[findCentResultList.size()];
		int i=0;
		for (FindCentResult findCentResult : findCentResultList) {
			peaks[i++] = findCentResult.getPeakIntensity();
		}

		return peaks;
	}
	
	public int[] getFindCentStatusList() {
		
		int[] statuses = new int[findCentResultList.size()];
		int i=0;
		for (FindCentResult findCentResult : findCentResultList) {
			statuses[i++] = findCentResult.getFindCentStatus();
		}

		return statuses;
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
		
	}
	
}
