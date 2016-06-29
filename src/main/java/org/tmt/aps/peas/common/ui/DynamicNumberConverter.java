package org.tmt.aps.peas.common.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.NumberConverter;

/**
 * JSF Converter used to format output data using patterns found in the database such as the {@link org.tmt.aps.peas.session.model.FieldMetaData#getDisplayFormat()} method.
 * @author smichaels
 *
 * @see org.tmt.aps.peas.session.model.FieldMetaData#getDisplayFormat()
 */
@FacesConverter("DynamicNumberConverter")
public class DynamicNumberConverter extends NumberConverter {


	/**
	 * returns an Object for use on the Java side given a string value returned from the web response, the UI component and the JSF Context
	 */
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        setPattern((String) component.getAttributes().get("pattern"));
        return super.getAsObject(context, component, value);
    }

    /**
     * returns a string for use on the HTML side, given the a Java Object value
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        setPattern((String) component.getAttributes().get("pattern"));
        return super.getAsString(context, component, value);
    }
}
