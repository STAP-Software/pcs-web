package org.tmt.aps.peas.common.ui;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.tmt.aps.peas.procedure.model.ProcedureOutputField;
import org.tmt.aps.peas.session.business.FieldMetaDataCache;
import org.tmt.aps.peas.session.model.FieldMetaData;

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
