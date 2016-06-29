package org.tmt.aps.peas.computation.model;

import java.util.ArrayList;
import java.util.List;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.config.model.SubimageDef;
/**
 * A List of Subimage definitions, and convenience methods for subimage definition lists.
 * A subimage definition list describes mask subimages: ideal spot locations, spot type (interior vs peripheral), 
 * if the spot is expected to be found and if it is to be used for analysis.
 * @author smichaels
 *
 */
public class SubimageDefList {
	
	private List<SubimageDef> listOfSubimageDefs;
	
	/**
	 * Constructor using a list of SubimageDef
	 * @param listOfSubimageDefs a list of subimageDefs
	 */
	public SubimageDefList(List<SubimageDef> listOfSubimageDefs) {
		this.listOfSubimageDefs = listOfSubimageDefs;
	}

	public List<SubimageDef> getListOfSubimageDefs() {
		return listOfSubimageDefs;
	}


	public void setListOfSubimageDefs(List<SubimageDef> listOfSubimageDefs) {
		this.listOfSubimageDefs = listOfSubimageDefs;
	}

	/**
	 * @return the ideal centroid locations for this subimage definition list
	 */
	public List<FloatPoint> getSubimageDefListCentroids() {
		
		List<FloatPoint> centroidList = new ArrayList<FloatPoint>();
		
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			centroidList.add(subimageDef.getCentroid());
		}
		return centroidList;
	}
	
	/**
	 * @return the ideal centroid locations for interior spots only
	 */
	public List<FloatPoint> getInteriorSubimageDefListCentroids() {
		
		List<FloatPoint> centroidList = new ArrayList<FloatPoint>();
		
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			if (subimageDef.getSpotType() == Constants.SPOT_TYPE_INTERIOR) {
				centroidList.add(subimageDef.getCentroid());
			}
		}
		return centroidList;
	}
	
	/**
	 * @return the ideal centroid x coordinate locations for all interior spots only as an encoded string
	 */
	public String getInteriorCentroidXsAsString() {
		float[] xArrayPt = FloatPointListEncoder.extractXArray(getInteriorSubimageDefListCentroids());
		return FloatListEncoder.encodeList(xArrayPt);
	}
	
	/**
	 * @return the ideal centroid y coordinate locations for all interior spots only as an encoded string
	 */
	public String getInteriorCentroidYsAsString() {
		float[] yArrayPt = FloatPointListEncoder.extractYArray(getInteriorSubimageDefListCentroids());
		return FloatListEncoder.encodeList(yArrayPt);
	}

	// 
	// 
	/**
	 * returns the 'spot_flag' array, where: value is 0 if subimage is not expected, 
	 * 1 if subimage is expected but not to be used in analysis of that segment or group, 
	 * 2 if subimage is expected and to be used in analysis.
	 * @return array of spot flags
	 */
	public int[] getMissingSpotFlags() {
		
		int[] spotFlag = new int[listOfSubimageDefs.size()];
		
		int i=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			spotFlag[i++] = subimageDef.getMissingSpotType();
		}
		
		return spotFlag;
	}
	
	/**
	 * Returns the number of spots that are expected to be present. e.g. for which the subimageDef missing spot type is not MISSING_SPOT_TYPE_NOT_EXPECTED
	 * @return the number of expected spots
	 */
	public int fandiExpectedSpotCount() {
			
		int expectedCount=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			if (subimageDef.getMissingSpotType() != Constants.MISSING_SPOT_TYPE_NOT_EXPECTED) {
				expectedCount++;
			}
		}
		
		return expectedCount;

	}
	
	/**
	 * Returns an array of flags cooresponding to each subimage in the list.  The flag has a value of 1 if the spot is good for analysis, 0 otherwise.
	 * @return array of good spot for analysis flags
	 */
	public int[] useForAnalysis() {
		
		int[] useForAnalysis = new int[listOfSubimageDefs.size()];
		
		int i=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			useForAnalysis[i++] = subimageDef.getMissingSpotType() == Constants.MISSING_SPOT_TYPE_USE ? 1 : 0;
		}
		
		return useForAnalysis;
	}
	
	/**
	 * Returns an array of flags corresponding to each subimage in the list.  The flags correspond to the spot type (interior vs peripheral)
	 * @return an array of spot type flags
	 */
	public int[] getNspotTypes() {
		int[] nspotFlag = new int[listOfSubimageDefs.size()];
		
		int i=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			nspotFlag[i++] = subimageDef.getSpotType();
		}
		
		return nspotFlag;
	}
	
	/**
	 * @return the number of interior spots in this subimage definition list
	 */
	public int getNumberOfInteriorSpots() {
		
		int count=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			if(subimageDef.getSpotType() == Constants.SPOT_TYPE_INTERIOR) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @return the number of peripheral spots in this subimage definition list
	 */
	public int getNumberOfPeripheralSpots() {
		
		int count=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			if(subimageDef.getSpotType() == Constants.SPOT_TYPE_PERIPHERAL) {
				count++;
			}
		}
		return count;
	}

	
	// use for M2 Calc
	/**
	 * Returns an array of flags corresponding to each element in the list of subimage definitions for interior spots only.  
	 * The value of the flag is 1 is the spot can be used for M2 calculations, 0 otherwise.
	 * @return array of interior spots M2 calc flags
	 */
	public int[] getUseForM2InteriorSpotFlags() {
		int[] ufm2SpotFlag = new int[getNumberOfInteriorSpots()];
		
		int i=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			if (subimageDef.getSpotType() == Constants.SPOT_TYPE_INTERIOR) {
				ufm2SpotFlag[i++] = subimageDef.getUseForM2Calc();
			}
		}
		
		return ufm2SpotFlag;
	}
	

	
	
}
