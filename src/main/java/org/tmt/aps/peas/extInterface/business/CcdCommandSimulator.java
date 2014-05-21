package org.tmt.aps.peas.extInterface.business;

import org.tmt.aps.peas.extinf.CcdCommand;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

public class CcdCommandSimulator implements CcdCommand {

	@Override
	public int[][] getImage() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fastWipe() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub

	}

	@Override
	public void wipeOn() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub

	}

}
