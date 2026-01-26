/** 
 * @author Scott Michaels 
 * @version 1.0 
 * Copyright TMT Observatory Corporation 2013 - All Rights Reserved. 
 */
package org.tmt.aps.peas.config.ui;

import java.io.Serializable;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.model.IterationListConfig;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.procedure.ui.ProcedureController;

/**
 * JSF Converter class to enable usage of IterationListConfig objects on JSF pages
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class IterationListConfigConverter implements Converter, Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private ProcedureController procedureController;

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				String idStr = submittedValue;
				long id = Long.valueOf(idStr);

				List<IterationListConfig> fullList = procedureController.getIterationListConfigOptions();

				for (IterationListConfig option : fullList) {
					if (option.getIterationListConfigId().longValue() == id) {
						return option;
					}
				}

			} catch (Exception e) {
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid iterationListConfig"));
			}
		}

		return null;
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		
		logger.debug("getAsString::enter");
		if (value == null || value.equals("")) {
			return "";
		} else {
			
			IterationListConfig listConfigValue = (IterationListConfig) value;
			
			// get the option that matches the value - even if the value is not an option with the correct id, compare iterationValueListEncoded strings
			List<IterationListConfig> fullList = procedureController.getIterationListConfigOptions();

			for (IterationListConfig candidate : fullList) {
							
				if (listConfigValue.getIterationValueList().getSize() == candidate.getIterationValueList().getSize()) {
					
					boolean match = true;
					
					// both have the same number of iterations, check that the filters match
					for (int i=0; i<listConfigValue.getIterationValueList().getSize(); i++) {
						
						try {
						
							String matchString = listConfigValue.getIterationValueList().findEntityLabelValue(i, "Filter");
							String candidateString = candidate.getIterationValueList().findEntityLabelValue(i, "Filter");
						
							if (!matchString.equals(candidateString)) {
								match = false;
							}
						
						
						} catch (Exception e) {
							match = false;
						}
						
					}
					
					if (match) {
						return "" + candidate.getIterationListConfigId();
					}
										
				}
				
			}
			
			return "";

		}
	}

}
