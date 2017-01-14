package org.tmt.aps.peas.config.model;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Constants data class containing mask constants.  This class is populated from database data in the {@link Constant} class and is made available to executors and
 * the user interface controllers in the {@link org.tmt.aps.peas.config.business.ConstantsCache}.
 * @author smichaels
 */
public class MaskConstants {
	
	
	FloatPoint passiveTiltTheoreticalLocations[];

	public FloatPoint[] getPassiveTiltTheoreticalLocations() {
		return passiveTiltTheoreticalLocations;
	}

	public void setPassiveTiltTheoreticalLocations(FloatPoint[] passiveTiltTheoreticalLocations) {
		this.passiveTiltTheoreticalLocations = passiveTiltTheoreticalLocations;
	}



	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nPassiveTileTheoreticalLocations = ");
		for (FloatPoint location : passiveTiltTheoreticalLocations) {
			buf.append(location + ", ");
		}
				
		buf.append("\n");
		return buf.toString();
	}

	
	
}
