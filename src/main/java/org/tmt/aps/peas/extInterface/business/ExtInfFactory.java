/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.business.ExtInfConfigState;
import org.tmt.aps.peas.extinf.ACS;
import org.tmt.aps.peas.extinf.AcsCommand;
import org.tmt.aps.peas.extinf.CCD;
import org.tmt.aps.peas.extinf.CamAsync;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CcdCommand;
import org.tmt.aps.peas.extinf.DcsCommand;
import org.tmt.aps.peas.extinf.DcsRsk;
import org.tmt.aps.peas.extinf.InstrumentInterface;

@Singleton
@Startup
@Lock(LockType.READ)
@DependsOn("ExtInfConfigState")
public class ExtInfFactory {

	@EJB
	PeasProperties peasProperties;
	@EJB
	ExtInfConfigState extInfConfigState;

	Logger logger = Logger.getLogger(this.getClass());

	DcsCommandSimulator dcsCommandSimulator;

	CamAsync camAsync = null;
	DcsRsk dcsRsk = null;
	CCD ccd = null;
	ACS acs = null;
	
	
	int telescopeId;
	
	@PostConstruct
	void init() throws Exception {
		dcsCommandSimulator = new DcsCommandSimulator();
		
		String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
		telescopeId = new Integer(telescopeIdStr);

	}



	public AcsCommand getAcsCommand() {

		try {

			if (extInfConfigState.getExtInfConnectConfig().isAcsEnabled()) {
				return getAcsCommandRemote(telescopeId);
			} else {
				return new AcsCommandSimulator();
			}
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}

	@Lock(LockType.WRITE)
	@AccessTimeout(value=2000)  // two seconds
	public CameraCommand getCameraCommand() {

		try {

			if (extInfConfigState.getExtInfConnectConfig().isCameraEnabled()) {
				return getCameraCommandRemote(telescopeId);
			} else {
				return new CameraCommandSimulator();
			}
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}
	
	public CcdCommand getCcdCommand() {

		try {
			
			if (extInfConfigState.getExtInfConnectConfig().isCcdEnabled()) {
				return getCcdCommandRemote(telescopeId);
			} else {
				return new CcdCommandSimulator();
			}
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}

	public DcsCommand getDcsCommand() {

		try {

			if (extInfConfigState.getExtInfConnectConfig().isDcsEnabled()) {
				return getDcsCommandRemote(telescopeId);
			} else {
				return dcsCommandSimulator;
			}

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}
	
	public InstrumentInterface getInstrumentCommand() {

		try {
			String instrumentEnabledStr = peasProperties.getProp("org.tmt.aps.peas.instrument_enabled");
			boolean instrumentEnabled = new Boolean(instrumentEnabledStr);

			if (instrumentEnabled) {
				return getInstrumentCommandRemote();
			} else {
				return new InstrumentCommandSimulator();
			}
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
	}

	public void resetAll() {
		acs = null;
		dcsRsk = null;
		camAsync = null;
		ccd = null;
	}


	private AcsCommand getAcsCommandRemote(int telescopeId) {
		try {
			
			if (acs == null) {
				acs = new ACS(telescopeId);
			}
			
			return acs;
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Acs Command Exception: ", e);
			return null;
		}
	}

	private CameraCommand getCameraCommandRemote(int telescopeId) {
		try {
			
			if (camAsync == null) {
				camAsync = new CamAsync(telescopeId);
			}
			return camAsync;
			
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Camera Command Exception:: ", e);
			return null;
		}
	}
	


	private CcdCommand getCcdCommandRemote(int telescopeId) {
		try {
			if (ccd == null) {
				ccd = new CCD(telescopeId);
			}
			return ccd;

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Ccd Command Exception:: ", e);
			return null;
		}
	}
	
	private DcsCommand getDcsCommandRemote(int telescopeId) {
		try {
			if (dcsRsk == null) {
				dcsRsk = new DcsRsk(telescopeId);
			}
			return dcsRsk;
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Dcs Command Exception:: ", e);
			return null;
		}
	}

	private InstrumentInterface getInstrumentCommandRemote() {
		try {
			String instrumentExtInfServer = peasProperties.getProp("org.tmt.aps.peas.instrument_ext_inf_server");
			String instrumentServiceName = peasProperties.getProp("org.tmt.aps.peas.instrument_service_name");

			Registry registry = LocateRegistry.getRegistry(instrumentExtInfServer);
			InstrumentInterface instCommand = (InstrumentInterface) registry.lookup(instrumentServiceName);
			return instCommand;
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Instrument Command Exception:: ", e);
			return null;
		}
	}

	
}
