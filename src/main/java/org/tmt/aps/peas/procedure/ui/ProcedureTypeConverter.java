/** 
 * @author Scott Michaels 
 * @version 1.0 
 * Copyright TMT Observatory Corporation 2013 - All Rights Reserved. 
 */
package org.tmt.aps.peas.procedure.ui;

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
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.model.ProcedureType;

/** 
 * JSF Converter class enabling {@link PupilMaskType} objects to be used in JSF pages
 * @author smichaels
 */
@Named
@SessionScoped
public class ProcedureTypeConverter implements Converter, Serializable {

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

				List<ProcedureType> fullList = procedureController.getProcedureTypeForSelectList();

				for (ProcedureType procedureType : fullList) {
					if (procedureType.getProcedureTypeId().longValue() == id) {
						return procedureType;
					}
				}

			} catch (Exception e) {
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid procedure type"));
			}
		}

		return null;
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		
		if (value == null || value.equals("")) {
			return "";
		} else {
			ProcedureType procedureType = (ProcedureType) value;
			return "" + procedureType.getProcedureTypeId();

		}
	}

}
