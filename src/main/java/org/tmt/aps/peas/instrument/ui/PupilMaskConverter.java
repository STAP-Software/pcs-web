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

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.instrument.model.PupilMask;

@Named
@SessionScoped
public class PupilMaskConverter implements Converter, Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private PupilMaskController pupilMaskController;

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				String idStr = submittedValue;
				long id = new Long(idStr);

				List<PupilMask> fullList = pupilMaskController.getPupilMaskList();

				for (PupilMask pupilMask : fullList) {
					if (pupilMask.getPupilMaskId().longValue() == id) {
						return pupilMask;
					}
				}

			} catch (Exception e) {
				logger.error(MessageGenerator.generateMessage("generic.error"), e);
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid pupil mask"));
			}
		}

		return null;
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		
		if (value == null || value.equals("")) {
			return "";
		} else {
			PupilMask pupilMask = (PupilMask) value;
			return "" + pupilMask.getPupilMaskId();

		}
	}

}
