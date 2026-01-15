package org.tmt.aps.peas.extInterface.business;

import java.rmi.RemoteException;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.extinf.DcsCommand;
import org.tmt.aps.peas.extinf.StarInfo;
import org.tmt.aps.peas.extinf.TimeoutException;

/**
 * DCS command simulator.  Generates dummy values for queries.
 * @author smichaels
 */
public class DcsCommandSimulator implements DcsCommand {

	Logger logger = Logger.getLogger(this.getClass());
	
	double[] dcsM2Pos = {0.1f, 0.2f, 0.3f};
	double[] telPos = {1.101010101, 2.202020202};
	double[] m2FocusAndTilt = {1.1, 1.2, 1.3};
	
	@Override
	public void commandDcsOffset(double deltaAz, double deltaEl) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandDcsOffset::SIMULATOR"));
		telPos[0] += ((deltaAz / 1000000.0) / Constants.DEG2RAD);
		telPos[1] += ((deltaEl / 1000000.0) / Constants.DEG2RAD);
		
		Utils.waitFor(2000);
		logger.info(MessageGenerator.generateMessage("command.success", "commandDcsOffset::SIMULATOR"));
	}

	@Override
	public double[] queryDcsM2Pos() throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "queryDcsM2Pos::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "queryDcsM2Pos::SIMULATOR"));
		return dcsM2Pos;
	}

	@Override
	public void commandDcsM2PosDelta(double[] m2PosDelta) throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "commandDcsM2PosDelta::SIMULATOR"));
		for (int i=0; i<3; i++) {
			dcsM2Pos[i] += m2PosDelta[i];
		}
		logger.info(MessageGenerator.generateMessage("command.success", "commandDcsM2PosDelta::SIMULATOR"));
	}

	@Override
	public int queryDcsStatus() throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "queryDcsStatus::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "queryDcsStatus::SIMULATOR"));
		return 4;
	}

	@Override
	public double[] queryTelPos() throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "queryTelPos::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "queryTelPos::SIMULATOR"));
		return telPos;
	}

	@Override
	public double[] queryM2FocusAndTilt() throws CommunicationException, TimeoutException, CommandFailureException, RemoteException {
		logger.info(MessageGenerator.generateMessage("command.start", "queryM2FocusAndTilt::SIMULATOR"));
		logger.info(MessageGenerator.generateMessage("command.success", "queryM2FocusAndTilt::SIMULATOR"));
		return m2FocusAndTilt;
	}

	@Override
	public StarInfo queryStar() throws CommunicationException, TimeoutException, CommandFailureException {
		logger.info(MessageGenerator.generateMessage("command.start", "queryStar::SIMULATOR"));
		StarInfo starInfo = new StarInfo("Sirius", 1.42f, "A1V(A)/DA2(B)");
		logger.info(MessageGenerator.generateMessage("command.success", "queryStar::SIMULATOR"));
		return starInfo;
	}

}
