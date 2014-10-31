package org.tmt.aps.peas.extInterface.business;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;

@Singleton
@Startup
@DependsOn({ "CameraMgmt", "PeasProperties" })
@Lock(LockType.READ)
public class CameraPoller {
	@EJB
	CameraMgmt cameraMgmt;

	private boolean doPoll = true;

	Logger logger = Logger.getLogger(this.getClass());

	@PostConstruct
	@Lock(LockType.WRITE)
	@Schedule(second = "*/2", minute = "*", hour = "*", persistent = false)
	public void pollCamera() {

		//logger.info("polling camera: doPoll = " + doPoll);
		
		if (doPoll) {

			try {

				//logger.info("refreshing camera status");
				cameraMgmt.refreshStatus();

			} catch (Exception e) {
				logger.error("error refreshing camera status: " + e.getMessage());
			}

		}
	}

	@Lock(LockType.WRITE)
	public void setDoPoll(boolean doPoll) {
		this.doPoll = doPoll;
	}
	
	
}
