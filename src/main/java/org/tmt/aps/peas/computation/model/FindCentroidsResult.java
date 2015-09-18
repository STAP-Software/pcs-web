package org.tmt.aps.peas.computation.model;

import java.util.ArrayList;
import java.util.List;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.config.model.SubimageDef;

public class FindCentroidsResult {

	private List<Subimage> subimageList;

	public FindCentroidsResult() {
		
	}
	
	public FindCentroidsResult(float[] xCent, float[] yCent, float[] intensity, float[] peak, int[] findCentStatus) {
		
		subimageList = new ArrayList<Subimage>();
		
		for (int i = 0; i< xCent.length; i++) {
			Subimage subimage = new Subimage(new FloatPoint(xCent[i], yCent[i]), intensity[i], peak[i], findCentStatus[i]);
			subimageList.add(subimage);
		}
	}
	
	public FindCentroidsResult(List<FloatPoint> centroidList, List<Float> intensity, List<Float> peak, List<Integer> findCentStatus) {
		
		subimageList = new ArrayList<Subimage>();
		
		for (int i = 0; i< centroidList.size(); i++) {
			Subimage subimage = new Subimage(centroidList.get(i), intensity.get(i), peak.get(i), findCentStatus.get(i));
			subimageList.add(subimage);
		}
	}
	
	/*
	public static FindCentroidsResult createFromCentroidList(List<FloatPoint> centroidList) {
		
		FindCentroidsResult result = new FindCentroidsResult();
		
		List<Subimage> subimageList = new ArrayList<Subimage>();
		
		for (int i = 0; i< centroidList.size(); i++) {
			// TODO: merge in real subimageDef
			Subimage subimage = new Subimage(null, centroidList.get(i), 0.0f, 0.0f, 0);
			subimageList.add(subimage);
		}
		result.setSubimageList(subimageList);
		
		return result;
	}
	
	private void setSubimageList(List<Subimage> subimageList) {
		this.subimageList = subimageList;
	}

	*/
	
	public FindCentroidsResult(List<Subimage> subimageList) {
		this.subimageList = subimageList;
	}
	
	
	public List<FloatPoint> getCentroidList() {
		
		List<FloatPoint> centroids = new ArrayList<FloatPoint>();
		
		for (Subimage subimage : subimageList) {
			centroids.add(subimage.getCentroid());
		}

		return centroids;
	}

	public float[] getIntensityList() {
		
		float[] intensities = new float[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			intensities[i++] = subimage.getSubimageIntensity();
		}

		return intensities;
	}
	
	public float[] getPeakList() {
		
		float[] peaks = new float[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			peaks[i++] = subimage.getPeakIntensity();
		}

		return peaks;
	}
	
	public int[] getFindCentStatusList() {
		
		int[] statuses = new int[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			statuses[i++] = subimage.getFindCentStatus();
		}

		return statuses;
	}
	
	public boolean containsGaussianCmFallbackCentroids() {
		for (Subimage subimage : subimageList) {
			if (subimage.getFindCentStatus() == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_X || 
					subimage.getFindCentStatus() == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_Y) {
				return true;
			}
		}	
		return false;
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
		
	}
	
}
