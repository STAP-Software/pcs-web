/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Permission;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.extinf.AcsCommand;
import org.tmt.aps.peas.extinf.CameraCommand;

@Singleton
@Startup
public class ExtInfFactory {

	@EJB
	PeasProperties peasProperties;

	// caches the current state of the ACS for use in PEAS PCS
	Logger logger = Logger.getLogger(this.getClass());

	String extInfServer;
	boolean acsEnabled;
	boolean dcsEnabled;
	boolean cameraEnabled;
	boolean ccdEnabled;

	@PostConstruct
	void init() {

		try {

			extInfServer = peasProperties.getProp("org.tmt.aps.peas.ext_inf_server");
			
			String acsEnabledStr = peasProperties.getProp("org.tmt.aps.peas.acs_enabled");
			acsEnabled = new Boolean(acsEnabledStr);

			String dcsEnabledStr = peasProperties.getProp("org.tmt.aps.peas.dcs_enabled");
			dcsEnabled = new Boolean(dcsEnabledStr);

			String cameraEnabledStr = peasProperties.getProp("org.tmt.aps.peas.camera_enabled");
			cameraEnabled = new Boolean(cameraEnabledStr);

			String ccdEnabledStr = peasProperties.getProp("org.tmt.aps.peas.ccd_enabled");
			ccdEnabled = new Boolean(ccdEnabledStr);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AcsCommand getAcsCommand() {

		try {
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

	private AcsCommand getAcsCommandRemote() {
		try {
			
			String name = "AcsCommand";
			Registry registry = LocateRegistry.getRegistry(extInfServer);
			AcsCommand acsCommand = (AcsCommand) registry.lookup(name);
			return acsCommand;
		} catch (Exception e) {
			System.err.println("Acs Command exception:");
			e.printStackTrace();
			return null;
		}
	}

	private CameraCommand getCameraCommandRemote() {
		try {
			
			String name = "CameraCommand";
			Registry registry = LocateRegistry.getRegistry(extInfServer);
			CameraCommand cameraCommand = (CameraCommand) registry.lookup(name);
			return cameraCommand;
		} catch (Exception e) {
			System.err.println("Camera Command exception:");
			e.printStackTrace();
			return null;
		}
	}

	
}
