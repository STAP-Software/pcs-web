package org.tmt.aps.peas.computation.model;

import java.util.ArrayList;
import java.util.List;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.config.model.SubimageDef;

public class SubimageDefList {
	
	private List<SubimageDef> listOfSubimageDefs;
	
	public SubimageDefList(List<SubimageDef> listOfSubimageDefs) {
		this.listOfSubimageDefs = listOfSubimageDefs;
	}

	public List<SubimageDef> getListOfSubimageDefs() {
		return listOfSubimageDefs;
	}


	public void setListOfSubimageDefs(List<SubimageDef> listOfSubimageDefs) {
		this.listOfSubimageDefs = listOfSubimageDefs;
	}

	// returns the ideal centroid locations
	public List<FloatPoint> getSubimageDefListCentroids() {
		
		List<FloatPoint> centroidList = new ArrayList<FloatPoint>();
		
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			centroidList.add(subimageDef.getCentroid());
		}
		return centroidList;
	}

	// returns the 'spot_flag' array, where:
	// value 0 if subimage is not expected, 1 if subimage
	// is expected but not to be used in analysis
	// of that segment or group, 2 if subimage is
	// expected and to be used in analysis.
	public int[] getMissingSpotFlags() {
		
		int[] spotFlag = new int[listOfSubimageDefs.size()];
		
		int i=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			spotFlag[i++] = subimageDef.getMissingSpotType();
		}
		
		return spotFlag;
	}
	
	public int fandiExpectedSpotCount() {
			
		int expectedCount=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			if (subimageDef.getMissingSpotType() != Constants.MISSING_SPOT_TYPE_NOT_EXPECTED) {
				expectedCount++;
			}
		}
		
		return expectedCount;

	}
	
	// returns a one for a good spot for analysis, zero otherwise
	public int[] useForAnalysis() {
		
		int[] useForAnalysis = new int[listOfSubimageDefs.size()];
		
		int i=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			useForAnalysis[i++] = subimageDef.getMissingSpotType() == Constants.MISSING_SPOT_TYPE_USE ? 1 : 0;
		}
		
		return useForAnalysis;
	}
	
	

	// interior vs peripheral
	public int[] getNspotTypes() {
		int[] nspotFlag = new int[listOfSubimageDefs.size()];
		
		int i=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			nspotFlag[i++] = subimageDef.getSpotType();
		}
		
		return nspotFlag;
	}
	
	// use for M2 Calc
	public int[] getUseForM2SpotFlags() {
		int[] ufm2SpotFlag = new int[listOfSubimageDefs.size()];
		
		int i=0;
		for (SubimageDef subimageDef : listOfSubimageDefs) {
			ufm2SpotFlag[i++] = subimageDef.getUseForM2Calc();
		}
		
		return ufm2SpotFlag;
	}
	

	
	
}
