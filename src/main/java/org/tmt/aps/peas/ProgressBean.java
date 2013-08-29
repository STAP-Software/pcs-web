/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

@Named
@SessionScoped
public class ProgressBean implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	private Integer progress;

	public Integer getProgress() {
		if(progress == null)
			progress = 0;
		else {
			progress = progress + (int)(Math.random() * 35);
			
			if(progress > 100)
				progress = 100;
		}
		
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}
	
	public void onComplete() {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Procedure Completed", "Progress Completed"));
	}
	
	public void cancel() {
		progress = null;
	}
}
					