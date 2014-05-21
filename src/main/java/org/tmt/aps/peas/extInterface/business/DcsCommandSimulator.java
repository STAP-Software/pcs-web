package org.tmt.aps.peas.extInterface.business;

import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.DcsCommand;
import org.tmt.aps.peas.extinf.StarInfo;
import org.tmt.aps.peas.extinf.TimeoutException;

public class DcsCommandSimulator implements DcsCommand {

	@Override
	public void commandDcsOffset(float deltaAz, float deltaEl) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub

	}

	@Override
	public float[] queryDcsM2Pos() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void commandDcsM2PosDelta(float[] m2PosDelta) throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub

	}

	@Override
	public int queryDcsStatus() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float[] queryTelPos() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StarInfo queryStar() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return null;
	}

}
