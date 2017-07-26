/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.business;

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
import org.tmt.aps.peas.extinf.CameraKtl;
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CcdCommand;
import org.tmt.aps.peas.extinf.DcsCommand;
import org.tmt.aps.peas.extinf.DcsRsk;
import org.tmt.aps.peas.extinf.InstrumentInterface;

/**
 * EJB Singleton managing external interface command delegation, either to the RPC client or a simulator.
 * If an interface is a simultor or not is determined by {@link ExtInfConfigState} 
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

	Logger logger = Logger.getLogger(this.getClass());

	DcsCommandSimulator dcsCommandSimulator;
	CameraCommandSimulator cameraCommandSimulator;

	CameraKtl cameraKtl = null;
	DcsRsk dcsRsk = null;
	CCD ccd = null;
	ACS acs = null;
	
	
	int telescopeId;
	
	@PostConstruct
	void init() throws Exception {
		dcsCommandSimulator = new DcsCommandSimulator();
		cameraCommandSimulator = new CameraCommandSimulator();
		
		String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
		telescopeId = new Integer(telescopeIdStr);

	}

	/**
	 * @return a reference to the ACS RPC client, or a simulator depending on current interface connection configuration
	 */
	public AcsCommand getAcsCommand() throws Exception {

		try {

			if (extInfConfigState.getExtInfConnectConfig().isAcsEnabled()) {
				return getAcsCommandRemote(telescopeId);
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
	@Lock(LockType.WRITE)
	@AccessTimeout(value=2000)  // two seconds
	public CameraCommand getCameraCommand() throws Exception {

		try {

			if (extInfConfigState.getExtInfConnectConfig().isCameraEnabled()) {
				return getCameraCommandRemote(telescopeId);
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
				return getCcdCommandRemote(telescopeId);
			} else {
				return new CcdCommandSimulator();
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
		dcsRsk = null;
		cameraKtl = null;
		ccd = null;
	}


	private AcsCommand getAcsCommandRemote(int telescopeId) throws Exception {
		try {
			
			if (acs == null) {
				acs = new ACS(telescopeId);
			}
			
			return acs;
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Acs Command Exception: ", e);
			throw e;
		}
	}

	private CameraCommand getCameraCommandRemote(int telescopeId) throws Exception {
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
	


	private CcdCommand getCcdCommandRemote(int telescopeId) throws Exception {
		try {
			if (ccd == null) {
				ccd = new CCD(telescopeId);
			}
			return ccd;

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Ccd Command Exception:: ", e);
			throw e;
		}
	}
	
	private DcsCommand getDcsCommandRemote(int telescopeId) throws Exception {
		try {
			if (dcsRsk == null) {
				dcsRsk = new DcsRsk(telescopeId);
			}
			return dcsRsk;
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error") + "Dcs Command Exception:: ", e);
			throw e;
		}
	}



	
}
