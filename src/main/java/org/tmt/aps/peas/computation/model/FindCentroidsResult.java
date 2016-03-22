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
	
	public FindCentroidsResult(FloatPoint[] centroidList, float[] intensity, float[] peak, int[] findCentStatus) {
		
		subimageList = new ArrayList<Subimage>();
		
		for (int i = 0; i< centroidList.length; i++) {
			Subimage subimage = new Subimage(centroidList[i], intensity[i], peak[i], findCentStatus[i]);
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
	

	public FindCentroidsResult(List<Subimage> subimageList) {
		this.subimageList = subimageList;
	}
	
	
	// getters and setters for procedure iteration output store

	
	public FloatPoint[] getCentroidList() {
		
		List<FloatPoint> centroids = new ArrayList<FloatPoint>();
		
		for (Subimage subimage : subimageList) {
			centroids.add(subimage.getCentroid());
		}

		return centroids.toArray(new FloatPoint[0]);
	}
	
	public void setCentroidList(FloatPoint[] centroidList) {
		if (subimageList == null) {
			subimageList = new ArrayList<Subimage>();

			for (int i = 0; i< centroidList.length; i++) {
				Subimage subimage = new Subimage(centroidList[i], 0.0f, 0.0f, 0);
				subimageList.add(subimage);
			}
		} else {
			for (int i = 0; i< centroidList.length; i++) {
				Subimage subimage = subimageList.get(i);
				subimage.setCentroid(centroidList[i]);
			}
		}
	}

	public float[] getIntensityList() {
		
		float[] intensities = new float[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			intensities[i++] = subimage.getSubimageIntensity();
		}

		return intensities;
	}
	
	public void setIntensityList(FloatPoint[] intensityList) {
		if (subimageList == null) {
			subimageList = new ArrayList<Subimage>();

			for (int i = 0; i< intensityList.length; i++) {
				Subimage subimage = new Subimage(intensityList[i], 0.0f, 0.0f, 0);
				subimageList.add(subimage);
			}
		} else {
			for (int i = 0; i< intensityList.length; i++) {
				Subimage subimage = subimageList.get(i);
				subimage.setCentroid(intensityList[i]);
			}
		}
	}

	
	public float[] getPeakList() {
		
		float[] peaks = new float[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			peaks[i++] = subimage.getPeakIntensity();
		}

		return peaks;
	}
	
	public void setPeakList(FloatPoint[] peakList) {
		if (subimageList == null) {
			subimageList = new ArrayList<Subimage>();

			for (int i = 0; i< peakList.length; i++) {
				Subimage subimage = new Subimage(peakList[i], 0.0f, 0.0f, 0);
				subimageList.add(subimage);
			}
		} else {
			for (int i = 0; i< peakList.length; i++) {
				Subimage subimage = subimageList.get(i);
				subimage.setCentroid(peakList[i]);
			}
		}
	}

	// generate a list of good peaks (no longer ordered by spot number)
	public float[] generateGoodPeakList() {
		float[] peakList = getPeakList();
		int[] foundSubimages = getFoundSubimageFlags();
		int goodPeakSize = 0;
		for (int i=0; i<foundSubimages.length; i++) {
			goodPeakSize += foundSubimages[i];
		}
		float[] result = new float[goodPeakSize];
		int goodPeakIndex = 0;
		for (int i=0; i<peakList.length; i++) {
			if (foundSubimages[i] > 0) {
				result[goodPeakIndex++] = peakList[i];
			}
		}
		return result;
	}
	
	
	public int[] getFindCentStatusList() {
		
		int[] statuses = new int[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			statuses[i++] = subimage.getFindCentStatus();
		}

		return statuses;
	}
	
	public void setFindCentStatusList(FloatPoint[] findCentStatusList) {
		if (subimageList == null) {
			subimageList = new ArrayList<Subimage>();

			for (int i = 0; i< findCentStatusList.length; i++) {
				Subimage subimage = new Subimage(findCentStatusList[i], 0.0f, 0.0f, 0);
				subimageList.add(subimage);
			}
		} else {
			for (int i = 0; i< findCentStatusList.length; i++) {
				Subimage subimage = subimageList.get(i);
				subimage.setCentroid(findCentStatusList[i]);
			}
		}
	}

	
	
	// convenience routines
	

	
	public int[] getFoundSubimageFlags() {
		int[] foundFlags = new int[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			foundFlags[i++] = (subimage.getFindCentStatus() == Constants.FIND_CENT_STATUS_SUCCESS ||
					subimage.getFindCentStatus() == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_X || 
					subimage.getFindCentStatus() == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_Y) ? 1 : 0;
		}

		return foundFlags;

	}
	
	public int[] getFoundInteriorSubimageFlags(int[] nspotTypes) {
		
		List<Subimage> interiorList = new ArrayList<Subimage>();
		for (int i=0; i<subimageList.size(); i++) {
			if (nspotTypes[i] == Constants.SPOT_TYPE_INTERIOR) {
				interiorList.add(subimageList.get(i));
			}
		}

		int[] foundFlags = new int[interiorList.size()];
		
		int i=0;
		for (Subimage subimage : interiorList) {
			foundFlags[i++] = (subimage.getFindCentStatus() == Constants.FIND_CENT_STATUS_SUCCESS ||
					subimage.getFindCentStatus() == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_X || 
					subimage.getFindCentStatus() == Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_Y) ? 1 : 0;
		}

		return foundFlags;

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
	
	
	/**
	 * 
	 * @return the number of spots that find_cent missed e.g. status != 0, != -1 and != 1008 and != 1009
	 * these are spots in addition to what f&i missed
	 * 
	 */
	public int missedSpots() {
		int missedSpots = 0;
		for (Subimage subimage : subimageList) {
			if (subimage.getFindCentStatus() != Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_X && 
					subimage.getFindCentStatus() != Constants.FIND_CENT_STATUS_GAUSS_FALLBACK_Y &&
					subimage.getFindCentStatus() != Constants.FIND_CENT_STATUS_NOT_PERFORMED &&
					subimage.getFindCentStatus() != Constants.FIND_CENT_STATUS_SUCCESS) {
				
				missedSpots++;				
			}
		}	
		return missedSpots;
	}
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		
		return buf.toString();
		
	}
	
}
