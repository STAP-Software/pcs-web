package org.tmt.aps.peas.computation.model;

import java.util.ArrayList;
import java.util.List;

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
	
	// TODO: some method for peripheral spots
	
	
}
