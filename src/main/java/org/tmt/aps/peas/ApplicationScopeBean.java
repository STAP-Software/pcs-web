/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

@ManagedBean
@ApplicationScoped
public class ApplicationScopeBean {

   public void preRenderView(ComponentSystemEvent e) {
	   
		Logger logger = Logger.getLogger(this.getClass());

	   // workaround for "java.lang.IllegalStateException: Cannot create a session after the response has been committed" problem with JSF
      HttpSession session = ( HttpSession ) FacesContext.getCurrentInstance().getExternalContext().getSession( true );
      
      //tune session params, eg. session.setMaxInactiveInterval(..);

      //perform other pre-render stuff, like setting user context...
   }
}
