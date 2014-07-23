package org.tmt.aps.peas.extInterface.business;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;

@Singleton
public class CameraPoller {
    @EJB
    CameraMgmt cameraMgmt;
    
    Logger logger = Logger.getLogger(this.getClass());
  
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
