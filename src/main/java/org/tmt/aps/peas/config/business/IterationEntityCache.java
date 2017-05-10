/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.tmt.aps.peas.config.model.IntegrationTime;
import org.tmt.aps.peas.config.model.IterableEntity;
import org.tmt.aps.peas.config.model.IterationListConfig;
import org.tmt.aps.peas.config.model.IterationListConfigOption;
import org.tmt.aps.peas.config.model.IterationValue;
import org.tmt.aps.peas.config.model.IterationValueList;
import org.tmt.aps.peas.config.model.ProcedureConfig;
import org.tmt.aps.peas.config.model.ProcedureIterationDef;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.Filter;
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
	Map<Long, List<IterationListConfig>> procedureTypeToOptionList = new HashMap<Long, List<IterationListConfig>>();
	
	List<ProcedureIterationDef> iterationDefs = null;
	
	List<Long> procedureTypeList = new ArrayList<Long>();
	
	List<String> entityClassNames;
	
	@PostConstruct
	public void init() throws Exception {
		refresh();
	}
	
	public void refresh() throws Exception {
		
		String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
		Long instrumentId = new Long(instrumentIdStr);
		
		// find the ccd for this instrument
		Ccd ccd = cameraDefMgmt.findInstrumentCcd(instrumentId);
		 

		// load up the procedureTypeToIterationDefList map
		iterationDefs = iterationMgmt.findProcedureIterationDefs();
		
		
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
		
		// Filters
		List<Filter> filterList = cameraDefMgmt.findAllFiltersForInstrument(instrumentId);
		indexToEntityMap = new HashMap<Long, IterableEntity>();
		className = null;
		for (Filter filter : filterList) {
			indexToEntityMap.put(getKeyFieldValue(filter), filter);
			className = filter.getClassName();
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
		
		indexToEntityMap = new HashMap<Long, IterableEntity>();
		// Integration Time - just create all possible times here	
		
		float intTime = 0.1f;
		while (intTime < 61.0f) {
		
			IntegrationTime integrationTime = new IntegrationTime(new Long((int)(intTime*10)), intTime);
			indexToEntityMap.put(getKeyFieldValue(integrationTime), integrationTime);
			className = integrationTime.getClassName();		
			intTime += (intTime < 1.0) ? 0.1f : 1.0f; 
		}
		classToEntityMap.put(className, indexToEntityMap);
		
		
		// TODO: ADD ALL GAIN RECORDS (for this instrument)
		
		
		
		// TODO: implement SimpleIteratorValue
		// Simple Iterator Values
		//List<SimpleIteratorValue> simpleIteratorValueList = ???;
		
		
		// query and loop for each procedureType
		
		for (Long procedureTypeId : procedureTypeToIterationDefList.keySet()) {
		
			
			// query iteration list config option lists
			List<IterationListConfigOption> iterationListConfigList = iterationMgmt.findIterationListConfigOptions(procedureTypeId, instrumentId, ccd.getCcdType().getCcdTypeId());
			
			List<IterationListConfig> options = new ArrayList<IterationListConfig>();
			for (IterationListConfigOption iterationListConfigOption : iterationListConfigList) {
				
				// for each option, we need to decode and populate the iteration value list
				populateIterationValueList(iterationListConfigOption, procedureTypeId);
				
				options.add(iterationListConfigOption);
			}
			
			procedureTypeToOptionList.put(procedureTypeId, options);
		}
		
	}
	
	/**
	 * Method to populate iteration value lists given the iterationListConfigOption from the database.  This can be used 
	 * by reporting to populate iteration tab labels and values.
	 * @param iterationListConfigOption
	 * @param procedureTypeId
	 */
	public void populateIterationValueList(IterationListConfig iterationListConfig, Long procedureTypeId) {
		
		if (iterationListConfig == null) {
			return;
		}
		
		List<String> iterationClassList = procedureTypeToIterationClassList.get(procedureTypeId);

		// we need to decode the iteration value list
		String encodedValueList = iterationListConfig.getIterationValueListEncoded();
		
		// the iterationValueList option has all the information in it to be used within an executor
		IterationValueList valueList = decodeList(encodedValueList, iterationClassList);
						
		iterationListConfig.setIterationValueList(valueList);

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
				
				try {
					Long nextKey = new Long(items.get(i*classNames.size() + j));
					// find the entity that matches
					IterableEntity entity = classToEntityMap.get(classNames.get(j)).get(nextKey);
					entities.add(entity);
					
				} catch (NumberFormatException e) {
					
					// for now we only support float instead of int
					
					Float nextKey = new Float(items.get(i*classNames.size() + j));
					// find the entity that matches
					
					Long key = new Long((int)(nextKey*10));
										
					IterableEntity entity = classToEntityMap.get(classNames.get(j)).get(key);
					entities.add(entity);
					
				}
				
				
			}
			// create an IterationValue using the classNames and next className.size() key values
			IterationValue iv = new IterationValue(entities, iterationDefs);
			iterationValues.add(iv);
		}
		
		return new IterationValueList(iterationValues);
	}

	public String encodeList(IterationValueList iterationValueList) {
		
		StringBuffer buf = new StringBuffer();
		
		for (int i=0; i<iterationValueList.getSize(); i++) {
			
			IterationValue iterationValue = iterationValueList.getIterationValue(i);

			for (IterableEntity entity : iterationValue.getEntities()) {
				buf.append(getKeyFieldValue(entity) + ",");
			}
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	
	
	/**
	 * Returns the keyFieldValue for an iterable entity
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	Long getKeyFieldValue(IterableEntity entity) {
		
		String getter = "get" + Character.toUpperCase(entity.getKeyFieldName().charAt(0)) + entity.getKeyFieldName().substring(1);
		
		try {
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
		
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
		
	}

	public List<IterationListConfig> getOptionList(Long procedureTypeId) {
		return procedureTypeToOptionList.get(procedureTypeId);
	}

	public void addOption(Long procedureTypeId, IterationListConfigOption option) {
		List<IterationListConfig> optionList = procedureTypeToOptionList.get(procedureTypeId);
		optionList.add(option);
	}

	public List<ProcedureIterationDef> getProcedureIterationDefList(Long procedureTypeId) {
		return procedureTypeToIterationDefList.get(procedureTypeId);
		
	}

	public IterableEntity getIterableEntity(String className, Long key) {
		return classToEntityMap.get(className).get(key);
	}
	
	
	
	// write a method here that takes a set of filters and compares to the current set of options for this procedure type
	// TODO - generalize for other procedures when necessary someday
	public IterationListConfig getOrCreateOptionForFitsList(List<FitsFilename> fitsList, Long procedureTypeId) throws Exception {
		
		Collection<IterableEntity> candidateList = classToEntityMap.get(Filter.class.getName()).values();
		
		List<IterableEntity> filterEntityList = new ArrayList<IterableEntity>();
		
		// finds an option that matches based on filter for NPH
		for (FitsFilename fitsFilename : fitsList) {
			// find the iteration entity for each filter
			int filter = fitsFilename.getNphFilter();
			
			// get the filter entity
			for (IterableEntity candidateEntity : candidateList) {
				Filter candidateFilter = (Filter)candidateEntity;
				
				if (candidateFilter.getFilterName().equals(filter + "")) {
					filterEntityList.add(candidateFilter);
				}
			}
		}
		
		// now we have a list of IterableEntity to compare to with.  
		// get the current option list for this procedure type
		List<IterationListConfig> currentOptionList = getOptionList(procedureTypeId);
		
		// test each option
		for (IterationListConfig iterationListConfig : currentOptionList) {
			
			// we only compare filter lists of equal length
			if (iterationListConfig.getIterationValueList().getSize() == filterEntityList.size()) {
			
				// when comparing an option, each element needs to match in sequence
				boolean sequenceMatch = true;
				for (int i=0; i<iterationListConfig.getIterationValueList().getSize(); i++) {
					Filter candidate = (Filter)iterationListConfig.getIterationValueList().getIterationValue(i).getIterableEntity("Filter");
					Filter filterEntity = (Filter)filterEntityList.get(i);
					
					if (!candidate.getFilterId().equals(filterEntity.getFilterId())) {
						sequenceMatch = false;
					}
					
				}
				if (sequenceMatch) {
					// return the option that matched
					return iterationListConfig;
				}
			}
		}
		
		// nothing matched - throw an Exception
		throw new Exception("No Filter set matches selected NPH fits file set");
		
	}
	
	
	
	public IterationValue createIterationValue(Long procedureTypeId, IterableEntity... entityArgs) {
		List<IterableEntity> entities = new ArrayList<IterableEntity>();
		for (IterableEntity entity : entityArgs) {
			entities.add(entity);
		}
		IterationValue iterationValue = new IterationValue(entities, getProcedureIterationDefList(procedureTypeId));
		return iterationValue;
	}
	
	
	public void applyIntegrationTimeList(int lightSource, IterationListConfig iterationListConfig) {
		
		// get the integration times from the String list
		List<String> items = Arrays.asList(iterationListConfig.getIntegrationTimeList().split("\\s*,\\s*"));
		

		for (int index=0; index<iterationListConfig.getIterationValueList().getSize(); index++) {
			float intTime = new Float(items.get(index));
			IterationValue iterationValue = iterationListConfig.getIterationValueList().getIterationValue(index);
			if (lightSource == ProcedureConfig.LIGHT_SOURCE_LED) {
				
				IterableEntity iterableEntity = getIterableEntity("org.tmt.aps.peas.config.model.IntegrationTime", new Long((int)(intTime * 10)));				
				iterationValue.setIterableEntity("LedIntegrationTime", iterableEntity);
				
			} else {
				IterableEntity iterableEntity = getIterableEntity("org.tmt.aps.peas.config.model.IntegrationTime", new Long((int)(intTime * 10)));				
				iterationValue.setIterableEntity("StarIntegrationTime", iterableEntity);
			}
		}

	}

}
