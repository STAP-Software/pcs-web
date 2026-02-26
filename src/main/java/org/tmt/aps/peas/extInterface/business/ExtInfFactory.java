/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.AccessTimeout;
import jakarta.ejb.DependsOn;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.business.ExtInfConfigState;
import org.tmt.aps.peas.extinf.ACS;
import org.tmt.aps.peas.extinf.AcsCommand;
import org.tmt.aps.peas.extinf.CCD;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraKtl;
import org.tmt.aps.peas.extinf.CcdCommand;
import org.tmt.aps.peas.extinf.DcsCommand;
//import org.tmt.aps.peas.extinf.DcsRsk;
import org.tmt.aps.peas.extinf.DcsKtl;
import org.tmt.aps.peas.instrument.business.PhysicalModel;

/**
 * EJB Singleton managing external interface command delegation, either to the RPC client or a simulator.
 * If an interface is a simulator or not is determined by {@link ExtInfConfigState} 
 * @author smichaels
 *
 */
@Singleton
@Startup
@Lock(LockType.READ)
@DependsOn("ExtInfConfigState")
public class ExtInfFactory {

	@EJB
	PeasProperties peasProperties;
	@EJB
	ExtInfConfigState extInfConfigState;
	@EJB
	PhysicalModel physicalModel;
	@EJB
	CameraMgmt cameraMgmt;

	Logger logger = Logger.getLogger(this.getClass());

	DcsCommandSimulator dcsCommandSimulator;
	CameraCommandSimulator cameraCommandSimulator;

	CameraKtl cameraKtl = null;
	//DcsRsk dcsRsk = null;
	DcsKtl dcsKtl = null;
	CCD ccd = null;
	ACS acs = null;
	
	CcdCommandSimulator ccdCommandSimulator = null;
	
	int telescopeId;
	

	@PostConstruct
	public void init() {
	    try {
	    	dcsCommandSimulator = new DcsCommandSimulator();
			cameraCommandSimulator = new CameraCommandSimulator();
			
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			telescopeId = Integer.valueOf(telescopeIdStr);
	    } catch (Exception e) {
	        throw new IllegalStateException("Initialization failed", e);
	    }
	}


	/**
	 * @return a reference to the ACS RPC client, or a simulator depending on current interface connection configuration
	 */
	public AcsCommand getAcsCommand() throws Exception {

		try {

			if (extInfConfigState.getExtInfConnectConfig().isAcsEnabled()) {
				return getAcsCommandRemote();
			} else {
				return new AcsCommandSimulator();
			}
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			throw e;
		}
	}

	/**
	 * @return a reference to the PCS Camera RPC client, or a simulator depending on current interface connection configuration
	 */
	//@Lock(LockType.WRITE)
	//@AccessTimeout(value=2000)  // two seconds
	public CameraCommand getCameraCommand() throws Exception {

		try {

			if (extInfConfigState.getExtInfConnectConfig().isCameraEnabled()) {
				return getCameraCommandRemote();
			} else {
				return cameraCommandSimulator;
			}
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			throw e;
		}
	}
	
	/**
	 * @return a reference to the PCS CCD RPC client, or a simulator depending on current interface connection configuration
	 */
	public CcdCommand getCcdCommand() throws Exception {

		try {
			
			if (extInfConfigState.getExtInfConnectConfig().isCcdEnabled()) {
				return getCcdCommandRemote();
			} else {
				return getCcdCommandSimulator();
				
			}
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			throw e;
		}
	}

	/**
	 * @return a reference to the DCS RPC client, or a simulator depending on current interface connection configuration
	 */
	public DcsCommand getDcsCommand() throws Exception {

		try {

			if (extInfConfigState.getExtInfConnectConfig().isDcsEnabled()) {
				return getDcsCommandRemote(telescopeId);
			} else {
				return dcsCommandSimulator;
			}

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			throw e;
		}
	}

	/**
	 * Resets all RPC client instances so that the next command will instantiate new ones.
	 */
	public void resetAll() {
		acs = null;
		//dcsRsk = null;
		dcsKtl = null;
		cameraKtl = null;
		ccd = null;
	}


	private AcsCommand getAcsCommandRemote() throws Exception {
		try {
			
			if (acs == null) {
				String host = peasProperties.getProp("org.tmt.aps.peas.acsRpcServerHost").trim();
				acs = new ACS(host);
			}
			
			return acs;
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Acs Command Exception: ", e);
			throw e;
		}
	}

	private CameraCommand getCameraCommandRemote() throws Exception {
		try {
			
			if (cameraKtl == null) {
				cameraKtl = new CameraKtl(telescopeId);
			}
			return cameraKtl;
			
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Camera Command Exception:: ", e);
			throw e;
		}
	}
	


	private CcdCommand getCcdCommandRemote() throws Exception {
		try {
			if (ccd == null) {
				String host = peasProperties.getProp("org.tmt.aps.peas.ccdHost").trim();
				int port = Integer.valueOf(peasProperties.getProp("org.tmt.aps.peas.ccdPort"));
				
				logger.info("Creating CCD Command, host = " + host + ", port = " + port);
				
				ccd = new CCD(host, port);
			}
			return ccd;

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Ccd Command Exception:: ", e);
			throw e;
		}
	}
	
	private DcsCommand getDcsCommandRemote(int telescopeId) throws Exception {
		try {
			if (dcsKtl == null) {
				String host = peasProperties.getProp("org.tmt.aps.peas.dcsRpcServerHost").trim();
				dcsKtl = new DcsKtl(telescopeId, host);
			}
			return dcsKtl;
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Dcs Command Exception:: ", e);
			throw e;
		}
	}

	private CcdCommand getCcdCommandSimulator() throws Exception {
		try {
			if (ccdCommandSimulator == null) {
				
				int imageHeight = Integer.valueOf(peasProperties.getProp("org.tmt.aps.peas.ccdSimulator.imageHeight"));
				int imageWidth = Integer.valueOf(peasProperties.getProp("org.tmt.aps.peas.ccdSimulator.imageWidth"));
				int overscanHeight = Integer.valueOf(peasProperties.getProp("org.tmt.aps.peas.ccdSimulator.overscanHeight"));
				int overscanWidth = Integer.valueOf(peasProperties.getProp("org.tmt.aps.peas.ccdSimulator.overscanWidth"));
				int gainNumber = Integer.valueOf(peasProperties.getProp("org.tmt.aps.peas.ccdSimulator.gainNumber"));
				int[] offsetCalibration = decodePropIntList(peasProperties.getProp("org.tmt.aps.peas.ccdSimulator.offsetCalibration"));
				
				ccdCommandSimulator = new CcdCommandSimulator(physicalModel.getInstrument().getCcd(), imageHeight, imageWidth, 
						overscanWidth, overscanHeight, gainNumber, offsetCalibration);
			}
			return ccdCommandSimulator;

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Ccd Command Exception:: ", e);
			throw e;
		}
	}
	
	private int[] decodePropIntList(String input) {
		List<String> items = Arrays.asList(input.split("\\s*,\\s*"));
		int[] output = new int[items.size()];
		int i=0;
		for (String item : items) {
			output[i++] = Integer.valueOf(item);
		}
		return output;
	}
	
}
