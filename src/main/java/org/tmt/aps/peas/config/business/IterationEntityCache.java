/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.config.model.IterableEntity;
import org.tmt.aps.peas.config.model.IterationListConfigOption;
import org.tmt.aps.peas.config.model.IterationValue;
import org.tmt.aps.peas.config.model.IterationValueList;
import org.tmt.aps.peas.config.model.ProcedureIterationDef;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;

/**
 * EJB Singleton cache for all iteration entities.  On initialization, this EJB calls {@link IterationMgmt#findIterationListConfigOptions(Long)}
 * decodes the option lists for each procedure type and composes the {@link IterationValueList} for each option value. 
 * Provides methods for querying options.  This EJB is initialized on startup.
 * @author smichaels
 */
@Singleton
@Startup
@Named
public class IterationEntityCache {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	IterationMgmt iterationMgmt;
	@EJB
	CameraDefMgmt cameraDefMgmt;
	@EJB
	PhysicalModel physicalModel;
	@EJB
	PeasProperties peasProperties;
	
	
	// cache for IterationDefLists indexed by procedure type
	Map<Long, List<ProcedureIterationDef>> procedureTypeToIterationDefList = new HashMap<Long, List<ProcedureIterationDef>>();
	Map<Long, List<String>> procedureTypeToIterationClassList = new HashMap<Long, List<String>>();

	// cache store for all entities indexed by class name and key field
	Map<String, Map<Long, IterableEntity>> classToEntityMap = new HashMap<String, Map<Long, IterableEntity>>();
	
	// cache store for config Option Lists indexed by procedureType
	Map<Long, List<IterationListConfigOption>> procedureTypeToOptionList = new HashMap<Long, List<IterationListConfigOption>>();
	
	
	List<Long> procedureTypeList = new ArrayList<Long>();
	
	List<String> entityClassNames;
	
	@PostConstruct
	public void init() throws Exception {
		refresh();
	}
	
	public void refresh() throws Exception {
		
		String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
		Long instrumentId = new Long(instrumentIdStr);

		// load up the procedureTypeToIterationDefList map
		List<ProcedureIterationDef> iterationDefs = iterationMgmt.findProcedureIterationDefs();
		
		
		// IterationDefLists maps to procedure type
		for (ProcedureIterationDef iterationDef : iterationDefs) {
			
			List<ProcedureIterationDef> procedureIterationDefList = procedureTypeToIterationDefList.get(iterationDef.getProcedureType().getProcedureTypeId());
			List<String> iterationClassList = procedureTypeToIterationClassList.get(iterationDef.getProcedureType().getProcedureTypeId());
			if (procedureIterationDefList == null) {
				procedureIterationDefList = new ArrayList<ProcedureIterationDef>();
				procedureTypeToIterationDefList.put(iterationDef.getProcedureType().getProcedureTypeId(), procedureIterationDefList);
			}
			if (iterationClassList == null) {
				iterationClassList = new ArrayList<String>();
				procedureTypeToIterationClassList.put(iterationDef.getProcedureType().getProcedureTypeId(), iterationClassList);
			}
			procedureIterationDefList.add(iterationDef);
			iterationClassList.add(iterationDef.getIterationEntityClassName());
			
		}		
		
		// load up entityCache with all entities indexed by className and indexField
		
		// this is where we query the database to get the actual entities
		// Filter Types
		List<FilterType> filterTypeList = cameraDefMgmt.findAllFilterTypes();
		Map<Long, IterableEntity> indexToEntityMap = new HashMap<Long, IterableEntity>();
		String className = null;
		for (FilterType filterType : filterTypeList) {
			indexToEntityMap.put(getKeyFieldValue(filterType), filterType);
			className = filterType.getClassName();
		}
		classToEntityMap.put(className, indexToEntityMap);
		
		// Pupil Mask Types
		List<PupilMaskType> pupilMaskList = cameraDefMgmt.findAllPupilMaskTypes();
		indexToEntityMap = new HashMap<Long, IterableEntity>();
		className = null;
		for (PupilMaskType pupilMaskType : pupilMaskList) {
			indexToEntityMap.put(getKeyFieldValue(pupilMaskType), pupilMaskType);
			className = pupilMaskType.getClassName();
		}
		classToEntityMap.put(className, indexToEntityMap);
		
		// Reference Beams
		List<ReferenceBeam>referenceBeamList = cameraDefMgmt.findAllRefBeams(instrumentId);
		indexToEntityMap = new HashMap<Long, IterableEntity>();
		className = null;
		for (ReferenceBeam referenceBeam : referenceBeamList) {
			indexToEntityMap.put(getKeyFieldValue(referenceBeam), referenceBeam);
			className = referenceBeam.getClassName();
		}
		classToEntityMap.put(className, indexToEntityMap);
		
		// TODO: implement SimpleIteratorValue
		// Simple Iterator Values
		//List<SimpleIteratorValue> simpleIteratorValueList = ???;
		
		
		// query and loop for each procedureType
		
		for (Long procedureTypeId : procedureTypeToIterationDefList.keySet()) {
		
			List<String> iterationClassList = procedureTypeToIterationClassList.get(procedureTypeId);
			
			// query iteration list config option lists
			List<IterationListConfigOption> iterationListConfigList = iterationMgmt.findIterationListConfigOptions(procedureTypeId);
			
			List<IterationListConfigOption> options = new ArrayList<IterationListConfigOption>();
			for (IterationListConfigOption iterationListConfigOption : iterationListConfigList) {
				
				// for each option, we need to decode the iteration value list
				
				String encodedValueList = iterationListConfigOption.getIterationValueListEncoded();
				
				// the iterationValueList option has all the information in it to be used within an executor
				IterationValueList option = decodeList(encodedValueList, iterationClassList);
								
				iterationListConfigOption.setIterationValueList(option);
				
				options.add(iterationListConfigOption);
			}
			
			procedureTypeToOptionList.put(procedureTypeId, options);
		}
		
	}

	/**
	 * Decodes a string representation of an IterationList to a List of IterationValues.  Values are encoded as x1, y1,..., x2, y2,...,...
	 * where x1 is the key value for the iterable entity of class x, and y1 is the key value for the iterable entity class y1.  
	 * @param encodedList the string encoded list
	 * @param className list of class names in the same order as in each iteration value 
	 * @return a List of decoded IterationValues
	 */
	public IterationValueList decodeList(String encodedList, List<String> classNames) {
		
		if (encodedList == null || encodedList.trim().length() == 0) {
			return new IterationValueList(new ArrayList<IterationValue>());
		}
		
		List<String> items = Arrays.asList(encodedList.split("\\s*,\\s*"));
		List<IterationValue> iterationValues = new ArrayList<IterationValue>();
		for (int i=0; i<items.size()/classNames.size(); i++) {
			List<IterableEntity> entities = new ArrayList<IterableEntity>();
			for (int j=0; j<classNames.size(); j++) {
				Long nextKey = new Long(items.get(i*classNames.size() + j));
				// find the entity that matches
				IterableEntity entity = classToEntityMap.get(classNames.get(j)).get(nextKey);
				entities.add(entity);
			}
			// create an IterationValue using the classNames and next className.size() key values
			IterationValue iv = new IterationValue(entities);
			iterationValues.add(iv);
		}
		
		return new IterationValueList(iterationValues);
	}

	public static String encodeList(List<FloatPoint> pointList) {
		// TODO: implement
		return null;
	}
	
	
	
	/**
	 * Returns the keyFieldValue for an iterable entity
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	Long getKeyFieldValue(IterableEntity entity) throws Exception {
		
		String getter = "get" + Character.toUpperCase(entity.getKeyFieldName().charAt(0)) + entity.getKeyFieldName().substring(1);
		
		Method method = entity.getClass().getMethod(getter , null);
		
		Object result = method.invoke(entity, null);
		
		if (result instanceof String) {
			return new Long((String)result);
		}
		if (result instanceof Integer) {
			return new Long((Integer)result);
		}
		if (result instanceof Long) {
			return (Long)result;
		}
		return null;
		
	}

	public List<IterationListConfigOption> getOptionList(Long procedureTypeId) {
		return procedureTypeToOptionList.get(procedureTypeId);
	}

	
}
