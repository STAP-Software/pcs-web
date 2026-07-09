/** 
 * @author Scott Michaels 
 * @version 1.0 
 * Copyright TMT Observatory Corporation 2013 - All Rights Reserved. 
 */
package org.tmt.aps.peas.instrument.ui;

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
import org.tmt.aps.peas.instrument.model.Filter;

/** 
 * JSF Converter class enabling {@link Filter} objects to be used in JSF pages
 * @author smichaels
 */
@Named
@SessionScoped
public class FilterConverter implements Converter<Object>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2071568438953984312L;

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private FilterController filterController;

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				String idStr = submittedValue;
				long id = Long.valueOf(idStr);

				List<Filter> fullList = filterController.getFilterList();

				for (Filter filter : fullList) {
					if (filter.getFilterId().longValue() == id) {
						return filter;
					}
				}

			} catch (Exception e) {
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid pcs fits file"));
			}
		}

		return null;
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		
		if (value == null || value.equals("")) {
			return "";
		} else {
			Filter filter = (Filter) value;
			return "" + filter.getFilterId();

		}
	}

}
