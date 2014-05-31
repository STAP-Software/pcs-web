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
import org.tmt.aps.peas.extinf.AcsCommand;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CcdCommand;
import org.tmt.aps.peas.extinf.DcsCommand;

@Singleton
@Startup
public class ExtInfFactory {

	@EJB
	PeasProperties peasProperties;

	// caches the current state of the ACS for use in PEAS PCS
	Logger logger = Logger.getLogger(this.getClass());

	DcsCommandSimulator dcsCommandSimulator;

	@PostConstruct
	void init() {
		dcsCommandSimulator = new DcsCommandSimulator();
	}

	public AcsCommand getAcsCommand() {

		try {
			String acsEnabledStr = peasProperties.getProp("org.tmt.aps.peas.acs_enabled");
			boolean acsEnabled = new Boolean(acsEnabledStr);

			if (acsEnabled) {
				return getAcsCommandRemote();
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
			boolean cameraEnabled = new Boolean(cameraEnabledStr);

			if (cameraEnabled) {
				return getCameraCommandRemote();
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
			boolean ccdEnabled = new Boolean(ccdEnabledStr);

			if (ccdEnabled) {
				return getCcdCommandRemote();
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
			boolean dcsEnabled = new Boolean(dcsEnabledStr);

			if (dcsEnabled) {
				return getDcsCommandRemote();
			} else {
				return dcsCommandSimulator;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			return null;
		}
	}



	private AcsCommand getAcsCommandRemote() {
		try {
			String acsExtInfServer = peasProperties.getProp("org.tmt.aps.peas.acs_ext_inf_server");
			String acsServiceName = peasProperties.getProp("org.tmt.aps.peas.acs_service_name");
			
			Registry registry = LocateRegistry.getRegistry(acsExtInfServer);
			AcsCommand acsCommand = (AcsCommand) registry.lookup(acsServiceName);
			return acsCommand;
		} catch (Exception e) {
			System.err.println("Acs Command exception:");
			e.printStackTrace();
			return null;
		}
	}

	private CameraCommand getCameraCommandRemote() {
		try {
			String cameraExtInfServer = peasProperties.getProp("org.tmt.aps.peas.camera_ext_inf_server");
			String cameraServiceName = peasProperties.getProp("org.tmt.aps.peas.camera_service_name");

			Registry registry = LocateRegistry.getRegistry(cameraExtInfServer);
			CameraCommand cameraCommand = (CameraCommand) registry.lookup(cameraServiceName);
			return cameraCommand;
		} catch (Exception e) {
			System.err.println("Camera Command exception:");
			e.printStackTrace();
			return null;
		}
	}

	private CcdCommand getCcdCommandRemote() {
		try {
			String ccdExtInfServer = peasProperties.getProp("org.tmt.aps.peas.ccd_ext_inf_server");
			String ccdServiceName = peasProperties.getProp("org.tmt.aps.peas.ccd_service_name");

			Registry registry = LocateRegistry.getRegistry(ccdExtInfServer);
			CcdCommand ccdCommand = (CcdCommand) registry.lookup(ccdServiceName);
			return ccdCommand;
		} catch (Exception e) {
			System.err.println("Ccd Command exception:");
			e.printStackTrace();
			return null;
		}
	}
	
	private DcsCommand getDcsCommandRemote() {
		try {
			String dcsExtInfServer = peasProperties.getProp("org.tmt.aps.peas.dcs_ext_inf_server");
			String dcsServiceName = peasProperties.getProp("org.tmt.aps.peas.dcs_service_name");

			Registry registry = LocateRegistry.getRegistry(dcsExtInfServer);
			DcsCommand dcsCommand = (DcsCommand) registry.lookup(dcsServiceName);
			return dcsCommand;
		} catch (Exception e) {
			System.err.println("Dcs Command exception:");
			e.printStackTrace();
			return null;
		}
	}

	
}
