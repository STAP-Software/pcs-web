/** 
 * @author Scott Michaels 
 * @version 1.0 
 * Copyright TMT Observatory Corporation 2013 - All Rights Reserved. 
 */
package org.tmt.aps.peas.config.ui;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.business.IterationEntityCache;
import org.tmt.aps.peas.config.model.IntegrationTime;
import org.tmt.aps.peas.config.model.IterationListConfig;

/**
 * JSF Converter class to enable usage of IterationListConfig objects on JSF pages
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class IntegrationTimeConverter implements Converter, Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	private IterationEntityCache iterationEntityCache;

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				
				float intTime = new Float(submittedValue);
				Long id = new Long((int)(intTime * 10));

				return iterationEntityCache.getIterableEntity(IntegrationTime.class.getName(), id);
				

			} catch (Exception e) {
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid iterationListConfig"));
			}
		}

	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		
		logger.debug("getAsString::enter");
		if (value == null || value.equals("")) {
			return "";
		} else {
			IntegrationTime option = (IntegrationTime) value;
			return "" + option.getIntegrationTime();

		}
	}

}
