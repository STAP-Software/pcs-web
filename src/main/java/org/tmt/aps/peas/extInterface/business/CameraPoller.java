package org.tmt.aps.peas.extInterface.business;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.ejb.EJB;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.business.ExtInfConfigState;

/**
 * Scheduled Singleton EJB that polls PCS camera at two second intervals.  Calls {@link CameraMgmt#refreshStatus()} every two seconds.
 * @author smichaels
 */
@Singleton
@Startup
@DependsOn({ "CameraMgmt", "CcdMgmt", "PeasProperties", "ExtInfConfigState" })
@Lock(LockType.READ)
public class CameraPoller {
	@EJB
	CameraMgmt cameraMgmt;
	@EJB
	CcdMgmt ccdMgmt;
	@EJB
	ExtInfConfigState extInfConfigState;


	private boolean doPoll = true;

	Logger logger = Logger.getLogger(this.getClass());

	@PostConstruct
	@Lock(LockType.WRITE)
	@Schedule(second = "*/2", minute = "*", hour = "*", persistent = false)
	public void pollCamera() {

		//logger.info("polling camera: doPoll = " + doPoll);
		
		if (doPoll) {

			try {

				if (!extInfConfigState.getExtInfConnectConfig().isCameraInitializing()) {
					//logger.info("refreshing camera status");
					cameraMgmt.refreshStatus();
				}
				
				if (!extInfConfigState.getExtInfConnectConfig().isCcdInitializing() && !extInfConfigState.getExtInfConnectConfig().isCameraInitializing()) {

					// refresh the CCD state
					ccdMgmt.refreshStatus();
				}

			} catch (Throwable e) {
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
			}

		}
	}

	@Lock(LockType.WRITE)
	public void setDoPoll(boolean doPoll) {
		this.doPoll = doPoll;
	}
	
	
}
