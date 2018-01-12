package org.tmt.aps.peas.computation.model;

import java.util.List;

import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.PointListEncoder;

/**
 * Computation data result class for <b>removeBadPixels</b> computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#removeBadPixels(int[][] frame, List<Rect> badPixelList, boolean removeBadPixels, float indexThreshold, int intensityThreshold)
 */
public class RemoveBadPixelsResult {

	int[][] filteredFrame; 
	List<Point> badPixelLocations;  
	int badPixelCount;
	boolean badPixelsRemoved;  
	boolean allBadPixelsFound; 
	

	public RemoveBadPixelsResult(int[][] filteredFrame, int[] badPixelLocationsX, int[] badPixelLocationsY, int badPixelCount, boolean badPixelsRemoved, boolean allBadPixelsFound) {
		
		this.filteredFrame = filteredFrame;
		this.badPixelCount = badPixelCount;
		this.badPixelsRemoved = badPixelsRemoved;
		this.allBadPixelsFound = allBadPixelsFound;
		
		this.badPixelLocations = PointListEncoder.constructFromXandY(badPixelLocationsX, badPixelLocationsY);
	}


	public int[][] getFilteredFrame() {
		return filteredFrame;
	}


	public void setFilteredFrame(int[][] filteredFrame) {
		this.filteredFrame = filteredFrame;
	}


	public List<Point> getBadPixelLocations() {
		return badPixelLocations;
	}


	public void setBadPixelLocations(List<Point> badPixelLocations) {
		this.badPixelLocations = badPixelLocations;
	}


	public int getBadPixelCount() {
		return badPixelCount;
	}


	public void setBadPixelCount(int badPixelCount) {
		this.badPixelCount = badPixelCount;
	}


	public boolean isBadPixelsRemoved() {
		return badPixelsRemoved;
	}


	public void setBadPixelsRemoved(boolean badPixelsRemoved) {
		this.badPixelsRemoved = badPixelsRemoved;
	}


	public boolean isAllBadPixelsFound() {
		return allBadPixelsFound;
	}


	public void setAllBadPixelsFound(boolean allBadPixelsFound) {
		this.allBadPixelsFound = allBadPixelsFound;
	}


	public String getBadPixelListEncoded() {
		// TODO Auto-generated method stub
		return PointListEncoder.encodeList(badPixelLocations);
	}

}
