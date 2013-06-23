package org.tmt.aps.peas;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean
@ApplicationScoped
public class ApplicationScopeBean {

   public void preRenderView() {
	   
	  // workaround for "java.lang.IllegalStateException: Cannot create a session after the response has been committed" problem with JSF
      HttpSession session = ( HttpSession ) FacesContext.getCurrentInstance().getExternalContext().getSession( true );
      
      //tune session params, eg. session.setMaxInactiveInterval(..);

      //perform other pre-render stuff, like setting user context...
   }
}
