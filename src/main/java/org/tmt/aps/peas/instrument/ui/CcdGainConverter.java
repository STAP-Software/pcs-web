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
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.CcdGain;
import org.tmt.aps.peas.instrument.model.Filter;

/** 
 * JSF Converter class enabling {@link Filter} objects to be used in JSF pages
 * @author smichaels
 */
@Named
@SessionScoped
public class CcdGainConverter implements Converter<Object>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4291122651249797152L;

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private PhysicalModel physicalModel;

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				String idStr = submittedValue;
				long id = Long.valueOf(idStr);

				List<CcdGain> fullList = physicalModel.getInstrument().getCcd().getCcdGainList();

				for (CcdGain ccdGain : fullList) {
					if (ccdGain.getCcdGainId().longValue() == id) {
						return ccdGain;
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
			CcdGain ccdGain = (CcdGain) value;
			return "" + ccdGain.getCcdGainId();

		}
	}

}
