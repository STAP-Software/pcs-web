/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.session.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.session.model.FieldMetaData;

@Singleton
@Startup
public class FieldMetaDataCache {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	SessionMgmt sessionMgmt;
	
	private Map<String, FieldMetaData> fieldMap;
	
	
	@PostConstruct
	public void init() throws Exception {
		
		List<FieldMetaData> fmdList = sessionMgmt.findAllFieldMetaData();
		fieldMap = new HashMap<String, FieldMetaData>();
		
		
		for (FieldMetaData fmd : fmdList) {
			
			String key = fmd.getTableName() + "::" + fmd.getFieldName();
			
			fieldMap.put(key.toLowerCase(), fmd);
		}

	}


	public FieldMetaData getFieldMetaData(String tableName, String columnName) {
		String key = tableName + "::" + columnName;
		return fieldMap.get(key.toLowerCase());
	}
	
}
