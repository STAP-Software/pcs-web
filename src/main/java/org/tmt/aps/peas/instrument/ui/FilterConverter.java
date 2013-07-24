/** 
 * @author Scott Michaels 
 * @version 1.0 
 * Copyright TMT Observatory Corporation 2013 - All Rights Reserved. 
 */
package org.tmt.aps.peas.instrument.ui;

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

import org.tmt.aps.peas.frame.model.PcsFitsFile;
import org.tmt.aps.peas.instrument.model.Filter;

@Named
@SessionScoped
public class FilterConverter implements Converter, Serializable {

	@Inject
	private FilterController filterController;

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				String idStr = submittedValue;
				long id = new Long(idStr);

				List<Filter> fullList = filterController.getFilterList();

				for (Filter filter : fullList) {
					if (filter.getFilterId().longValue() == id) {
						return filter;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid pcs fits file"));
			}
		}

		return null;
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		
		System.out.println("getAsString::enter");
		if (value == null || value.equals("")) {
			return "";
		} else {
			Filter filter = (Filter) value;
			return "" + filter.getFilterId();

		}
	}

}
