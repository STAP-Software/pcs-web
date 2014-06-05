package org.tmt.aps.peas.extInterface.business;

import org.tmt.aps.peas.extinf.CcdCommand;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

public class CcdCommandSimulator implements CcdCommand {

	@Override
	public int[][] getImage() throws CommunicationException, TimeoutException, CommandFailureException {
		// TODO Auto-generated method stub
		int[][] frame = new int[1024][1024];
		for (int i=0; i<1024; i++) {
			for (int j=0; j<1024; j++) {
				
				frame[i][j] = j + i;
			}
		}
		return frame;
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
