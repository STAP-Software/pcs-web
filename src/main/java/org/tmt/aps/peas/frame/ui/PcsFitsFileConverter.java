/** 
 * @author Scott Michaels 
 * @version 1.0 
 * Copyright TMT Observatory Corporation 2013 - All Rights Reserved. 
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

import org.tmt.aps.peas.frame.model.PcsFitsFile;

@Named
@SessionScoped
public class PcsFitsFileConverter implements Converter, Serializable {

	@Inject
	private FrameController frameController;

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		System.out.println("getAsObject::enter");
		System.out.println("getAsObject::submittedValue = " + submittedValue);
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				String fitsFileName = submittedValue;

				List<PcsFitsFile> fullList = frameController.getAllFitsFiles();

				for (PcsFitsFile pcsFitsFile : fullList) {
					if (pcsFitsFile.getFileName().equals(fitsFileName)) {
						System.out.println("getAsObject:: returining: " + fitsFileName);
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
		
		System.out.println("getAsString::enter");
		if (value == null || value.equals("")) {
			return "";
		} else {
			PcsFitsFile pcsFitsFile = (PcsFitsFile) value;
			System.out.println("getAsString::" + pcsFitsFile.getFileName() );
			return pcsFitsFile.getFileName();

		}
	}

}
