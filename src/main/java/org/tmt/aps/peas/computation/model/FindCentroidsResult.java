package org.tmt.aps.peas.computation.model;

import java.util.ArrayList;
import java.util.List;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;

/**
 * Computation data result class for <b>findCentroids</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#findCentroids(float[][], FIResult, org.tmt.aps.peas.config.model.FindCentConfig, org.tmt.aps.peas.config.model.FindCentConfig, int[], int[], boolean)
 */
public class FindCentroidsResult {

	private List<Subimage> subimageList;

	public FindCentroidsResult() {
		
	}
	/**
	 * Constructor using x and y arrays
	 * @param xCent
	 * @param yCent
	 * @param intensity
	 * @param peak
	 * @param findCentStatus
	 */
	public FindCentroidsResult(float[] xCent, float[] yCent, float[] intensity, float[] peak, int[] findCentStatus) {
		
		subimageList = new ArrayList<Subimage>();
		
		for (int i = 0; i< xCent.length; i++) {
			Subimage subimage = new Subimage(new FloatPoint(xCent[i], yCent[i]), intensity[i], peak[i], findCentStatus[i]);
			subimageList.add(subimage);
		}
	}
	
	/**
	 * Constructor using FloatPoint coordinates array
	 * @param centroidList
	 * @param intensity
	 * @param peak
	 * @param findCentStatus
	 */
	public FindCentroidsResult(FloatPoint[] centroidList, float[] intensity, float[] peak, int[] findCentStatus) {
		
		subimageList = new ArrayList<Subimage>();
		
		for (int i = 0; i< centroidList.length; i++) {
			Subimage subimage = new Subimage(centroidList[i], intensity[i], peak[i], findCentStatus[i]);
			subimageList.add(subimage);
		}
	}
	
	/**
	 * Constructor using List of FloatPoint coordinates
	 * @param centroidList
	 * @param intensity
	 * @param peak
	 * @param findCentStatus
	 */
	public FindCentroidsResult(List<FloatPoint> centroidList, List<Float> intensity, List<Float> peak, List<Integer> findCentStatus) {
		
		subimageList = new ArrayList<Subimage>();
		
		for (int i = 0; i< centroidList.size(); i++) {
			Subimage subimage = new Subimage(centroidList.get(i), intensity.get(i), peak.get(i), findCentStatus.get(i));
			subimageList.add(subimage);
		}
	}
	
	/**
	 * Constructor using List of Subimage objects
	 * @param subimageList
	 */
	public FindCentroidsResult(List<Subimage> subimageList) {
		this.subimageList = subimageList;
	}
	
	
	// getters and setters for procedure iteration output store

	/**
	 * Extracts centroid list from list of subimages
	 * @return array of centroid coordinates
	 */
	public FloatPoint[] getCentroidList() {
		
		List<FloatPoint> centroids = new ArrayList<FloatPoint>();
		
		for (Subimage subimage : subimageList) {
			centroids.add(subimage.getCentroid());
		}

		return centroids.toArray(new FloatPoint[0]);
	}
	
	/**
	 * Sets centroids.  Adds centroids to Subimage list.  Creates new Subimage objects as necessary.
	 * @param centroidList
	 */
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
	/**
	 * Replaces a single subimage in the list of Subimages
	 * @param index the index of the subimage to replace
	 * @param subimage the subimage to replace at index
	 */
	public void setSubimage(int index, Subimage subimage) {
		this.subimageList.remove(index);
		this.subimageList.add(index, subimage);
	}

	/**
	 * Computes the array of intensities from the list of Subimage objects
	 * @return array of subimage intensities
	 */
	public float[] getIntensityList() {
		
		float[] intensities = new float[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			intensities[i++] = subimage.getSubimageIntensity();
		}

		return intensities;
	}
	

	/**
	 * Computes the array of intensity peaks from the list of Subimage objects
	 * @return array of subimage peak intensities
	 */	
	public float[] getPeakList() {
		
		float[] peaks = new float[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			peaks[i++] = subimage.getPeakIntensity();
		}

		return peaks;
	}
	
	/**
	 * Generate a list of good peaks.  Only peaks for found subimages appear in the list.
	 * @return the list of good peak intensity values
	 */
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
	
	/**
	 * @return an array of integers corresponding to the findCentStatus for each subimage in the list
	 */
	public int[] getFindCentStatusList() {
		
		int[] statuses = new int[subimageList.size()];
		int i=0;
		for (Subimage subimage : subimageList) {
			statuses[i++] = subimage.getFindCentStatus();
		}

		return statuses;
	}
	

	
	// convenience routines
	

	/**
	 * Convenience function that generates an array containing one if the corresponding find cent status is success or gaussian fallback, otherwise zero.
	 * @return array of found subimage flags
	 */
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
	
	/**
	 * Convenience function that generates an array for the interior spots only, containing one if the corresponding find cent status is success or gaussian fallback, otherwise zero.
	 * @return array of found subimage flags
	 */	
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

	/**
	 * Convenience function returns true if any of the subimages findCentStatus was a gaussian fallback
	 * @return true if any subimage findCentStatus was a gaussian fallback
	 */	
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
	 * @return the number of spots that find_cent missed e.g. status != 0, != -1 and != 1008 and != 1009
	 * these are spots in addition to what f&i missed
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
