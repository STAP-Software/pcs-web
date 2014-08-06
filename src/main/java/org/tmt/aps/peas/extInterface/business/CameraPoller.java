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
@DependsOn({"CameraMgmt", "PeasProperties"})
@Lock(LockType.READ)
public class CameraPoller {
    @EJB
    CameraMgmt cameraMgmt;
    
    Logger logger = Logger.getLogger(this.getClass());
  
    @PostConstruct
    @Lock(LockType.WRITE)
    @Schedule(second="*/5", minute="*",hour="*", persistent=false)
    public void pollCamera(){
    	
    	try {
    		
    		logger.debug("refreshing camera status");
    		cameraMgmt.refreshStatus();
        
    	} catch (Exception e) {
    		logger.error("error refreshing camera status: " + e.getMessage());
    	}
    }
}
