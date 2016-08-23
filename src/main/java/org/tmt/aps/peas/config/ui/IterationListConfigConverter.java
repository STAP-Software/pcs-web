/** 
 * @author Scott Michaels 
 * @version 1.0 
 * Copyright TMT Observatory Corporation 2013 - All Rights Reserved. 
 */
package org.tmt.aps.peas.config.ui;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.model.IterationListConfigOption;
import org.tmt.aps.peas.instrument.model.SufsGroup;
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
				long id = new Long(idStr);

				List<IterationListConfigOption> fullList = procedureController.getIterationListConfigOptions();

				for (IterationListConfigOption option : fullList) {
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
			IterationListConfigOption option = (IterationListConfigOption) value;
			return "" + option.getIterationListConfigId();

		}
	}

}
