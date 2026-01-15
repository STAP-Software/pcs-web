package org.tmt.aps.peas.common.ui;

import java.io.Serializable;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import org.tmt.aps.peas.procedure.model.ProcedureOutputField;
import org.tmt.aps.peas.session.business.FieldMetaDataCache;
import org.tmt.aps.peas.session.model.FieldMetaData;

/**
 * Session scoped, JSF named object used for supplying {@link org.tmt.aps.peas.session.model.FieldMetaData} and {@link org.tmt.aps.peas.procedure.model.ProcedureOutputField} metadata from the {@link org.tmt.aps.peas.session.business.FieldMetaDataCache} and making this data available to JSF when rendering a page.
 * @author smichaels
 * @see org.tmt.aps.peas.session.business.FieldMetaDataCache
 */
@Named
@SessionScoped
public class MetaDataController implements Serializable {

	
	@EJB
	FieldMetaDataCache fieldMetaDataCache;
	
	
	public FieldMetaData findFieldMetaData(String tableName, String columnName) {
		return fieldMetaDataCache.getFieldMetaData(tableName, columnName);
	}
	
	public ProcedureOutputField findProcedureOutputField(String className, String fieldName) {
		return fieldMetaDataCache.getProcedureOutputField(className, fieldName);
	}

}
