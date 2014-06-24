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
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.SufsGroup;

@Named
@SessionScoped
public class SufsGroupConverter implements Converter, Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private MissingSpotsController missingSpotsController;

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				String idStr = submittedValue;
				long id = new Long(idStr);

				List<SufsGroup> fullList = missingSpotsController.getSufsGroupList();

				for (SufsGroup sufsGroup : fullList) {
					if (sufsGroup.getSufsGroupId().longValue() == id) {
						return sufsGroup;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid sufsGroup"));
			}
		}

		return null;
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		
		logger.debug("getAsString::enter");
		if (value == null || value.equals("")) {
			return "";
		} else {
			SufsGroup sufsGroup = (SufsGroup) value;
			return "" + sufsGroup.getSufsGroupId();

		}
	}

}
