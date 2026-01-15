/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.procedure.business.ProcedureOutputMgmt;
import org.tmt.aps.peas.procedure.model.ProcedureOutputField;
import org.tmt.aps.peas.session.model.FieldMetaData;

/**
 * Singleton EJB cache for all database field metadata and procedure output field metadata
 * @author smichaels
 *
 */
@Singleton
@Startup
public class FieldMetaDataCache {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	SessionMgmt sessionMgmt;
	@EJB 
	ProcedureOutputMgmt procedureOutputMgmt;
	
	private Map<String, FieldMetaData> fieldMap;
	private Map<String, ProcedureOutputField> procedureOutputFieldMap;
	
	/**
	 * Upon startup, query database for all field meta data and procedure output metadata and store in the cache
	 * @throws Exception
	 */
	@PostConstruct
	public void init() throws Exception {
		
		List<FieldMetaData> fmdList = sessionMgmt.findAllFieldMetaData();
		fieldMap = new HashMap<String, FieldMetaData>();
		
		for (FieldMetaData fmd : fmdList) {
			
			String key = fmd.getTableName() + "::" + fmd.getFieldName();
			
			fieldMap.put(key.toLowerCase(), fmd);
		}

		List<ProcedureOutputField> pofList = procedureOutputMgmt.findAllProcedureOutputFields();
		procedureOutputFieldMap = new HashMap<String, ProcedureOutputField>();
		
		for (ProcedureOutputField pof : pofList) {
			
			String key = pof.getClassName() + "::" + pof.getFieldName();
			
			procedureOutputFieldMap.put(key.toLowerCase(), pof);
		}

	}

	/**
	 * Cache access method to retrieve metadata for a single database field
	 * @param tableName the table name for the field
	 * @param columnName the column name for the field
	 * @return the field metadata
	 */
	public FieldMetaData getFieldMetaData(String tableName, String columnName) {
		String key = tableName + "::" + columnName;
		return fieldMap.get(key.toLowerCase());
	}
	
	/**
	 * Cache access method to retrieve metadata for a procedure output field
	 * @param className the class name of the class that contains the procedure output field
	 * @param fieldName the field name of the procedure output field
	 * @return the field metadata
	 */
	public ProcedureOutputField getProcedureOutputField(String className, String fieldName) {
		String key = className + "::" + fieldName;
		return procedureOutputFieldMap.get(key.toLowerCase());
	}
	
}
