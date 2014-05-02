package org.tmt.aps.peas.extInterface.business;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.tmt.aps.peas.extinf.AcsCommand;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.TimeoutException;

public class AcsCommandSimulator implements AcsCommand {

	public AcsCommandSimulator() {

	}


	public void setActuDeltas(double p[]) 
			throws CommunicationException, TimeoutException, 
			CommandFailureException {
		
	}

	public double getMirrTemp() 
			throws CommunicationException, CommandFailureException {
		return 45.2;
	}

	public boolean isRunning() 
			throws CommunicationException, CommandFailureException {
		return true;
	}
	
	public double getRMSActuMove() 
			throws CommunicationException, CommandFailureException {
		return 0.00342;
	}

	public int takeSnap() 
			throws CommunicationException, CommandFailureException {
		return 42;
	}
	
	public void loadSnap(int snap) 
			throws CommunicationException, CommandFailureException {
		
	}


}
