package org.tmt.aps.peas.extInterface.model;

import org.tmt.aps.peas.extinf.Gain;

public class GainImpl extends Gain {

	double electronsPerAdu;
	
	public GainImpl(int programNumber) {
		super(programNumber);
	}
	
	@Override
	public double getElectronsPerAdu() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setElectronsPerAdu(double electronsPerAdu) {
		this.electronsPerAdu = electronsPerAdu;
	}

	@Override
	public int getProgramNumber() {
		// TODO Auto-generated method stub
		return super.getProgramNumber();
	}

}
