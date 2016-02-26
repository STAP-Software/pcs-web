package org.tmt.aps.peas.common.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.NumberConverter;

@FacesConverter("DynamicNumberConverter")
public class DynamicNumberConverter extends NumberConverter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        setPattern((String) component.getAttributes().get("pattern"));
        return super.getAsObject(context, component, value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        setPattern((String) component.getAttributes().get("pattern"));
        return super.getAsString(context, component, value);
    }
}
