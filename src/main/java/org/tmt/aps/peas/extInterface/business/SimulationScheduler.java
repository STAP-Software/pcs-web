package org.tmt.aps.peas.extInterface.business;

import java.util.Arrays;

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
import org.tmt.aps.peas.extinf.CameraCommand;
import org.tmt.aps.peas.extinf.CameraQueryListener;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.extinf.CameraStatusListener;


/**
 * Scheduled Singleton EJB that supplies Camera I/F listeners with values periodically.  
 * @author smichaels
 */
@Singleton
@Startup
@DependsOn({ "ExtInfFactory", "PeasProperties", "ExtInfConfigState" })
@Lock(LockType.READ)
public class SimulationScheduler {
	
	@EJB
	ExtInfFactory extInfFactory;
	@EJB
	ExtInfConfigState extInfConfigState;


	Logger logger = Logger.getLogger(this.getClass());

	@PostConstruct
	@Lock(LockType.WRITE)
	@Schedule(second = "*/2", minute = "*", hour = "*", persistent = false)
	public void emitEvents() {

		try {
			
			// if camera is enabled, then don't run
			if (!extInfConfigState.getExtInfConnectConfig().isCameraEnabled()) {
			
			CameraCommand cameraCommand = extInfFactory.getCameraCommand();
			if (cameraCommand instanceof CameraCommandSimulator) {
				
				CameraCommandSimulator simulator = (CameraCommandSimulator)cameraCommand;
				
				// Camera Status
				CameraStatus cameraStatus = simulator.getCameraStatus();
				
				for (CameraStatusListener listener : simulator.cameraStatusChangeListenerList) {
					listener.cameraStatusUpdate(cameraStatus);
				}
				for (CameraStatusListener listener : simulator.cameraStatusChangePeriodicListenerList) {
					listener.cameraStatusUpdate(cameraStatus);
				}
				for (CameraStatusListener listener : simulator.cameraStatusPeriodicListenerList) {
					listener.cameraStatusUpdate(cameraStatus);
				}
				
				
				// Camera Query
				
				System.out.println(Arrays.toString(simulator.deviceCodeToCameraChangeListenerList.keySet().toArray()));
				
				for (Integer deviceKey : simulator.deviceCodeToCameraChangeListenerList.keySet()) {
					// get the value CameraQueryResult for this device
					CameraQueryResult result = simulator.queryCamera(deviceKey);
					for (CameraQueryListener listener : simulator.deviceCodeToCameraChangeListenerList.get(deviceKey)) {
						listener.cameraQueryUpdate(deviceKey, result);
					}
				}
				for (Integer deviceKey : simulator.deviceCodeToCameraChangePeriodicListenerList.keySet()) {
					// get the value CameraQueryResult for this device
					CameraQueryResult result = simulator.queryCamera(deviceKey);
					for (CameraQueryListener listener : simulator.deviceCodeToCameraChangePeriodicListenerList.get(deviceKey)) {
						listener.cameraQueryUpdate(deviceKey, result);
					}
				}
				for (Integer deviceKey : simulator.deviceCodeToCameraPeriodicListenerList.keySet()) {
					// get the value CameraQueryResult for this device
					CameraQueryResult result = simulator.queryCamera(deviceKey);
					for (CameraQueryListener listener : simulator.deviceCodeToCameraPeriodicListenerList.get(deviceKey)) {
						listener.cameraQueryUpdate(deviceKey, result);
					}
				}
				
				
			}
			}

		} catch (Throwable e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}


	
	
}
