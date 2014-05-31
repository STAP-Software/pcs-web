package org.tmt.aps.peas.extInterface.business;

import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.DcsCommand;
import org.tmt.aps.peas.extinf.StarInfo;
import org.tmt.aps.peas.extinf.TimeoutException;

public class DcsCommandSimulator implements DcsCommand {

	float[] dcsM2Pos = {0.1f, 0.2f, 0.3f};
	float[] telPos = {1.1f, 2.2f};
	
	@Override
	public void commandDcsOffset(float deltaAz, float deltaEl) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		telPos[0] += (deltaAz / 1000000.0);
		telPos[1] += (deltaEl / 1000000.0);
	}

	@Override
	public float[] queryDcsM2Pos() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return dcsM2Pos;
	}

	@Override
	public void commandDcsM2PosDelta(float[] m2PosDelta) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		for (int i=0; i<3; i++) {
			dcsM2Pos[i] += m2PosDelta[i];
		}
	}

	@Override
	public int queryDcsStatus() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public float[] queryTelPos() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return telPos;
	}

	@Override
	public StarInfo queryStar() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		StarInfo starInfo = new StarInfo("Sirius", 1.42f, "A1V(A)/DA2(B)");
		return starInfo;
	}

}
