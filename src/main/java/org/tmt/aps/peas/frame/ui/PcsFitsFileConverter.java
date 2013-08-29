/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.ui;

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
import org.tmt.aps.peas.frame.model.FitsFilename;

@Named
@SessionScoped
public class PcsFitsFileConverter implements Converter, Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private FrameController frameController;

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		logger.debug("getAsObject::enter");
		logger.debug("getAsObject::submittedValue = " + submittedValue);
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				String fitsFileName = submittedValue;

				List<FitsFilename> fullList = frameController.getAllFitsFiles();

				for (FitsFilename pcsFitsFile : fullList) {
					if (pcsFitsFile.getFileName().equals(fitsFileName)) {
						logger.debug("getAsObject:: returining: " + fitsFileName);
						return pcsFitsFile;
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
		
		logger.debug("getAsString::enter");
		if (value == null || value.equals("")) {
			return "";
		} else {
			FitsFilename pcsFitsFile = (FitsFilename) value;
			logger.debug("getAsString::" + pcsFitsFile.getFileName() );
			return pcsFitsFile.getFileName();

		}
	}

}
