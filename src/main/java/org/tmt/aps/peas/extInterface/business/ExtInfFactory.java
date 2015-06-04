/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
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
public class ExtInfFactory {

	@EJB
	PeasProperties peasProperties;

	// caches the current state of the ACS for use in PEAS PCS
	Logger logger = Logger.getLogger(this.getClass());

	DcsCommandSimulator dcsCommandSimulator;

	CamAsync camAsync = null;
	
	@PostConstruct
	void init() {
		dcsCommandSimulator = new DcsCommandSimulator();
	}

	public AcsCommand getAcsCommand() {

		try {
			String acsEnabledStr = peasProperties.getProp("org.tmt.aps.peas.acs_enabled");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			boolean acsEnabled = new Boolean(acsEnabledStr);
			int telescopeId = new Integer(telescopeIdStr);

			if (acsEnabled) {
				return getAcsCommandRemote(telescopeId);
			} else {
				return new AcsCommandSimulator();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	public CameraCommand getCameraCommand() {

		try {
			String cameraEnabledStr = peasProperties.getProp("org.tmt.aps.peas.camera_enabled");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			boolean cameraEnabled = new Boolean(cameraEnabledStr);
			int telescopeId = new Integer(telescopeIdStr);

			if (cameraEnabled) {
				return getCameraCommandRemote(telescopeId);
			} else {
				return new CameraCommandSimulator();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}
	
	public CcdCommand getCcdCommand() {

		try {
			String ccdEnabledStr = peasProperties.getProp("org.tmt.aps.peas.ccd_enabled");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			boolean ccdEnabled = new Boolean(ccdEnabledStr);
			int telescopeId = new Integer(telescopeIdStr);
			
			if (ccdEnabled) {
				return getCcdCommandRemote(telescopeId);
			} else {
				return new CcdCommandSimulator();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}

	public DcsCommand getDcsCommand() {

		try {
			String dcsEnabledStr = peasProperties.getProp("org.tmt.aps.peas.dcs_enabled");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			int telescopeId = new Integer(telescopeIdStr);
			boolean dcsEnabled = new Boolean(dcsEnabledStr);

			if (dcsEnabled) {
				return getDcsCommandRemote(telescopeId);
			} else {
				return dcsCommandSimulator;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}
	
	public InstrumentInterface getInstrumentCommand() {

		try {
			String instrumentEnabledStr = peasProperties.getProp("org.tmt.aps.peas.instrument_enabled");
			boolean instrumentEnabled = new Boolean(instrumentEnabledStr);
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			int telescopeId = new Integer(telescopeIdStr);

			if (instrumentEnabled) {
				return getInstrumentCommandRemote();
			} else {
				return new InstrumentCommandSimulator();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}



	private AcsCommand getAcsCommandRemote(int telescopeId) {
		try {
			
			return new ACS(telescopeId);
		} catch (Exception e) {
			System.err.println("Acs Command exception:");
			e.printStackTrace();
			return null;
		}
	}

	private CameraCommand getCameraCommandRemote(int telescopeId) {
		try {

			/*
			String cameraExtInfServer = peasProperties.getProp("org.tmt.aps.peas.camera_ext_inf_server");
			String cameraServiceName = peasProperties.getProp("org.tmt.aps.peas.camera_service_name");

			Registry registry = LocateRegistry.getRegistry(cameraExtInfServer);
			CameraCommand camCommand = (CameraCommand) registry.lookup(cameraServiceName);
			return camCommand;
			*/
			
			if (camAsync == null) {
				camAsync = new CamAsync(telescopeId);
			}
			return camAsync;
			
			
		} catch (Exception e) {
			System.err.println("Camera Command exception:");
			e.printStackTrace();
			return null;
		}
	}
	


	private CcdCommand getCcdCommandRemote(int telescopeId) {
		try {
			
			return new CCD(telescopeId);

		} catch (Exception e) {
			System.err.println("Ccd Command exception:");
			e.printStackTrace();
			return null;
		}
	}
	
	private DcsCommand getDcsCommandRemote(int telescopeId) {
		try {
			
			return new DcsRsk(telescopeId);
		} catch (Exception e) {
			System.err.println("Dcs Command exception:");
			e.printStackTrace();
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
			System.err.println("Instrument Command exception:");
			e.printStackTrace();
			return null;
		}
	}

	
}
