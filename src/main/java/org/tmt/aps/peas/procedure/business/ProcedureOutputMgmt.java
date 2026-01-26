/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.PointListEncoder;
import org.tmt.aps.peas.config.model.Constant;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureIterationOutput;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureOutputField;
import org.tmt.aps.peas.procedure.model.ProcedureOutputValue;
import org.tmt.aps.peas.procedure.model.ProcedureOutputable;
import org.tmt.aps.peas.session.model.FieldDescriptor;

/**
 * Session EJB managing procedure output data.  Handles encoding and decoding of values based on procedure output field metadata.
 * @author smichaels
 *
 */
@Stateless
public class ProcedureOutputMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	/**
	 * Creates a procedure output by traversing all the data in the passed procedureOutput
	 * The method calls {@link #getOutputFieldClassNames()} and {@link #getOuputFieldMapForClass(String)} to traverse the data
	 * by class and then encodes each field using that field's {@link ProcedureOutputField} metadata.
	 * Each field is then persisted as a {@link ProcedureOutputValue}
	 * @param procedureOutput the procedure output data
	 * @param procedureId the procedure to associate with the data
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ProcedureOutputable createProcedureOutput(ProcedureOutputable procedureOutput, Long procedureId) throws Exception {

		// generate all the ProcedureOutputValues for this procedureOutput
		
		// 1. get all the calc result and decision log class fields possible
		List<String> outputClassNames = getOutputFieldClassNames();
		
		Class poClass = procedureOutput.getClass();
		Method[] poMethods = poClass.getMethods();

		for (Method poMethod : poMethods) {

			// test that this is an official calc result getter method
			if (!testMethodName(poClass, poMethod.getName(), outputClassNames)) continue;
							
			// get the calcResult object
			Object calcResult = poMethod.invoke(procedureOutput, new Object[0]);

			if (calcResult == null) continue;
			
			// get all field methods from the calc result class
			Class calcClass = calcResult.getClass();
	
			Method[] methods = calcResult.getClass().getMethods();
	
			// get the fieldId from the metadata
			Map<String, ProcedureOutputField> outputFieldMap = getOuputFieldMapForClass(calcResult.getClass().getSimpleName());
	
			// loop over all getter methods
			for (Method method : methods) {
				if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
					
					String fieldName = deriveFieldNameFromGetter(method.getName());
	
					ProcedureOutputField procedureOutputField = outputFieldMap.get(fieldName);
	
					if (procedureOutputField != null) {
	
						logger.debug("fieldName = " + fieldName);
	
						// construct a new ProcedureOutputValue
						ProcedureOutputValue procedureOutputValue = new ProcedureOutputValue();
						procedureOutputValue.setProcedureId(procedureId);
						procedureOutputValue.setProcedureOutputField(procedureOutputField);
						procedureOutputValue.setIteration(procedureOutput.getIteration());
	
						// encode the field data for store
						String data = encodeObjectFieldValue(calcResult, method, procedureOutputField);
	
						procedureOutputValue.setData(data);
	
						logger.info(MessageGenerator.generateMessage("record.create", "procedureOutputValue"));
						em.persist(procedureOutputValue);
					}
				}
			}
		}

		// em.persist(procedureOutput);

		return procedureOutput;
	}
	
			
	private boolean testMethodName(Class poClass, String methodName, List<String> candidates) {
		if (methodName.startsWith("get")) {
			for (String candidate : candidates) {
				if (methodName.equals("get" + candidate)) {					
					return true;
				}
			}
		}
		return false;
	}
		
	/**
	 * Generates a procedure output by querying all the {@link ProcedureOutputValue} data associated with the procedure output list.
	 * The procedure output list is the list of output fields defined for the procedure type.
	 * The method calls {@link #decodeAndSetObjectFieldValue(Object, FieldDescriptor, String)} to reconstitute the data structure
	 * This method handles all procedure iteration outputs as well.
	 * @param procedure the procedure to query and generate output data structure for
	 */
	public ProcedureOutput findProcedureOutput(Procedure procedure) throws Exception {

		// fill out a list of ProcedureOutputValues

		TypedQuery<ProcedureOutputValue> query = em.createNamedQuery("findOutputDisplayValuesForProcedure", ProcedureOutputValue.class);
		query.setParameter("procedureId", procedure.getProcedureId());
		query.setParameter("procedureTypeId", procedure.getProcedureType().getProcedureTypeId());

		List<ProcedureOutputValue> procedureOutputDisplayList = query.getResultList();

		if (procedureOutputDisplayList.isEmpty()) {
			return null;
		}

		String poClassName = procedure.getProcedureType().getProcedureOutputClassName();
				
		String fullPoClassName = "org.tmt.aps.peas.procedure.model." + poClassName;

		Object poClassInstance = Class.forName(fullPoClassName).newInstance();
		ProcedureOutput procedureOutput = (ProcedureOutput) poClassInstance;
		procedureOutput.setProcedureOutputList(procedureOutputDisplayList);

		
		// reconstitute the procedureOutput object tree
		
		TypedQuery<ProcedureOutputValue> query2 = em.createNamedQuery("findAllOutputValuesForProcedure", ProcedureOutputValue.class);
		query2.setParameter("procedureId", procedure.getProcedureId());
		List<ProcedureOutputValue> procedureOutputList = query2.getResultList();
		
		
		
		for (ProcedureOutputValue procedureOutputValue : procedureOutputList) {
			
			// here, check for null and create object as necessary
			String className = procedureOutputValue.getProcedureOutputField().getClassName();
			// get the calc result object from the procedure output.  Create if necessary
			
			Method calcResultGetMethod = poClassInstance.getClass().getMethod("get" + className, new Class[0]);
			
			Object calcResult = calcResultGetMethod.invoke(poClassInstance, new Object[0]);
			
			if (calcResult == null) {
				// create a new one and apply setter in procedureOutput 
				String fullClassName = className.contains("DecisionLog") ? "org.tmt.aps.peas.procedure.model." + className : "org.tmt.aps.peas.computation.model." + className;
				calcResult = Class.forName(fullClassName).newInstance();
				// apply setter method
				Class[] paramTypes = {calcResult.getClass()};
				Method setterMethod = poClassInstance.getClass().getMethod("set" + className, paramTypes);
				Object[] params = {calcResult};
				setterMethod.invoke(poClassInstance, params);
			}
			
			decodeAndSetObjectFieldValue(calcResult, procedureOutputValue.getProcedureOutputField(), procedureOutputValue.getData());
			logger.debug("procedureOutput = " + procedureOutputValue.getProcedureOutputField().getFieldName());
		}
		logger.debug("Done");

		
		
		// find procedure iteration outputs
		Integer iteration = 0;
		while (true) {
			query = em.createNamedQuery("findOutputDisplayValuesForProcedureIteration", ProcedureOutputValue.class);
			query.setParameter("procedureId", procedure.getProcedureId());
			query.setParameter("procedureTypeId", procedure.getProcedureType().getProcedureTypeId());
			query.setParameter("iteration", iteration);

			List<ProcedureOutputValue> procedureIterationOutputDisplayList = query.getResultList();

			if (procedureIterationOutputDisplayList.isEmpty()) {
				break;
			}

			// generate the iteration output class name: replace 'Procedure' with 'Iteration'
			String poItClassName = procedure.getProcedureType().getProcedureOutputClassName().replace("Procedure", "Iteration");
			
			
			String fullPoItClassName = "org.tmt.aps.peas.procedure.model." + poItClassName;

			Object poItClassInstance = Class.forName(fullPoItClassName).newInstance();

			ProcedureIterationOutput pio = (ProcedureIterationOutput) poItClassInstance;
			pio.setProcedureIterationOutputList(procedureIterationOutputDisplayList);
			pio.setIteration(iteration++);
			
			
			// reconstitute the procedureIterationOutput object tree - we don't need all the fields, so don't load them at this time
			
			//query2 = em.createNamedQuery("findAllOutputDisplayValuesForProcedureIteration", ProcedureOutputValue.class);
			//query2.setParameter("procedureId", procedure.getProcedureId());
			//List<ProcedureOutputValue> procedureIterationOutputList = query2.getResultList();


			for (ProcedureOutputValue procedureOutputValue : procedureIterationOutputDisplayList) {
				
				// here, check for null and create object as necessary
				String className = procedureOutputValue.getProcedureOutputField().getClassName();
				// get the calc result object from the procedure output.  Create if necessary
				
				Method calcResultGetMethod = poItClassInstance.getClass().getMethod("get" + className, new Class[0]);
				
				Object calcResult = calcResultGetMethod.invoke(poItClassInstance, new Object[0]);
				
				if (calcResult == null) {
					// create a new one and apply setter in procedureOutput 
					String fullClassName = className.contains("DecisionLog") ? "org.tmt.aps.peas.procedure.model." + className : "org.tmt.aps.peas.computation.model." + className;
					calcResult = Class.forName(fullClassName).newInstance();
					// apply setter method
					Class[] paramTypes = {calcResult.getClass()};
					Method setterMethod = poItClassInstance.getClass().getMethod("set" + className, paramTypes);
					Object[] params = {calcResult};
					setterMethod.invoke(poItClassInstance, params);
				}
				
				decodeAndSetObjectFieldValue(calcResult, procedureOutputValue.getProcedureOutputField(), procedureOutputValue.getData());
			}

			procedureOutput.addIteration(pio);

		}

		return procedureOutput;
	}
	
	
	/**
	 * @return the list of all defined procedure output fields
	 */
	public List<ProcedureOutputField> findAllProcedureOutputFields() {
		TypedQuery<ProcedureOutputField> query = em.createNamedQuery("findAllOutputFields", ProcedureOutputField.class);

		List<ProcedureOutputField> fieldList = query.getResultList();
		
		return fieldList;
	}

	
	// returns a list of class names used in procedure output field table
	private List<String> getOutputFieldClassNames() {
		TypedQuery<ProcedureOutputField> query = em.createNamedQuery("findAllOutputFields", ProcedureOutputField.class);

		List<ProcedureOutputField> fieldList = query.getResultList();

		Set<String> result = new TreeSet<String>();
		for (ProcedureOutputField field : fieldList) {
			result.add(field.getClassName());
		}
		
		return new ArrayList<String>(result);
	}

	// returns a map of field names to field Ids for procedureOutputFields
	private Map<String, ProcedureOutputField> getOuputFieldMapForClass(String className) {

		HashMap<String, ProcedureOutputField> fieldMap = new HashMap<String, ProcedureOutputField>();
		TypedQuery<ProcedureOutputField> query = em.createNamedQuery("findAllOutputFieldsForClass", ProcedureOutputField.class);

		query.setParameter("className", className);

		List<ProcedureOutputField> fieldList = query.getResultList();

		// fill the map
		for (ProcedureOutputField pof : fieldList) {
			fieldMap.put(pof.getFieldName(), pof);
		}

		return fieldMap;

	}

	private String deriveFieldNameFromGetter(String getterMethodName) {

		String string = (getterMethodName.startsWith("is")) ? getterMethodName.substring(2) : getterMethodName.substring(3);
		return string;
	}

	/**
	 * Encodes a field value for the passed object and returns the encoded string
	 * @param object the object containing the data value
	 * @param method the getter method to extract the value from object
	 * @param fieldDescriptor the field descriptor that determines the encoding method to use
	 * @return the encoded string
	 */
	public String encodeObjectFieldValue(Object object, Method method, FieldDescriptor fieldDescriptor) throws Exception {

		// extract and convert the data
		Class returnTypeClass = method.getReturnType();
		Object[] args = new Object[0];

		StringBuffer buf = new StringBuffer();

		// easy cases first
		if (fieldDescriptor.isScalar()) {

			return "" + method.invoke(object, args);

		} else {

			// one and two dimensional array cases

			if (fieldDescriptor.isOneDimensional()) {

				// one dimensional arrays and arrays of points

				switch (fieldDescriptor.getDataType()) {

				case Constant.DATA_TYPE_INT:
					int[] intArray = (int[]) method.invoke(object, args);
					for (int element : intArray) {
						buf.append("" + element + ", ");
					}
					break;

				case Constant.DATA_TYPE_FLOAT:
					float[] floatArray = (float[]) method.invoke(object, args);
					for (float element : floatArray) {
						buf.append("" + element + ", ");
					}
					break;

				case Constant.DATA_TYPE_INT_POINT:
				case Constant.DATA_TYPE_FLOAT_POINT:
					Object[] objArray = (Object[]) method.invoke(object, args);
					for (Object element : objArray) {
						buf.append("" + element + ", ");
					}
					break;

				}

			} else {

				// two dimensional (non-point type) arrays
				switch (fieldDescriptor.getDataType()) {
				case Constant.DATA_TYPE_INT:
					int[][] intArray = (int[][]) method.invoke(object, args);
					if (intArray != null) {
						for (int[] element : intArray) {
							for (int subelement : element) {
								buf.append("" + subelement + ", ");
							}
						}
					} else {
						buf.append(", ");
					}
					break;

				case Constant.DATA_TYPE_FLOAT:
					float[][] floatArray = (float[][]) method.invoke(object, args);
					if (floatArray != null) {
						for (float[] element : floatArray) {
							for (float subelement : element) {
								buf.append("" + subelement + ", ");
							}
						}
					} else {
						buf.append(", ");
					}
					break;

					
				case Constant.DATA_TYPE_FLOAT_POINT:
					FloatPoint[][] objArray = (FloatPoint[][]) method.invoke(object, args);
					if (objArray != null) {
						for (FloatPoint[] element : objArray) {
							for (FloatPoint subelement : element) {
								buf.append("" + subelement + ", ");
							}
						}
					} else {
						buf.append(", ");
					}
					break;

				}
			}

		}

		if (buf.length() > 1) {
			buf.delete(buf.length() - 2, buf.length());
		}
		return buf.toString();

	}

	
	/**
	 * Decodes a passed value using the passed field descriptor to determine the decoding method to use.
	 * Sets the field value in the passed classInstance using the setter method defined by the field name in the field descriptor. 
	 * @param classInstance the object to set the decoded value into
	 * @param fieldDescriptor the field descriptor of the data
	 * @param value the encoded data
	 */
	public void decodeAndSetObjectFieldValue(Object classInstance, FieldDescriptor fieldDescriptor, String value) throws Exception {

		if (value == null || value.trim().length() == 0 || value.equals("null")) return;
		
		// get the named field's setter method
		Method method = null;

		// easy cases first
				
		if (fieldDescriptor.isScalar()) {

			switch (fieldDescriptor.getDataType()) {

			case Constant.DATA_TYPE_INT:
				Integer intValue = Integer.valueOf(value);
				method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), int.class);
				method.invoke(classInstance, intValue);
				break;

			case Constant.DATA_TYPE_FLOAT:
				Float floatValue = Float.valueOf(value);
				method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), float.class);
				method.invoke(classInstance, floatValue);
				break;

			case Constant.DATA_TYPE_DOUBLE:
				Double doubleValue = Double.valueOf(value);
				method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), double.class);
				method.invoke(classInstance, doubleValue);
				break;

			case Constant.DATA_TYPE_BOOLEAN:
				Boolean booleanValue = Boolean.valueOf(value);
				method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), boolean.class);
				method.invoke(classInstance, booleanValue);
				break;

			case Constant.DATA_TYPE_FLOAT_POINT:
				FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(value).toArray(new FloatPoint[] {});
				FloatPoint floatPointValue = floatPointArray[0];
				method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), FloatPoint.class);
				method.invoke(classInstance, (Object) floatPointValue);
				break;

			}

		} else {

			// one and two dimensional array cases

			if (fieldDescriptor.isOneDimensional()) {

				// one dimensional arrays and arrays of points

				switch (fieldDescriptor.getDataType()) {

				case Constant.DATA_TYPE_INT:
					Integer intArray[] = IntegerListEncoder.decodeList(value).toArray(new Integer[] {});
					int primitiveIntArray[] = new int[intArray.length];
					for (int i = 0; i < intArray.length; i++) {
						primitiveIntArray[i] = intArray[i];
					}

					method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), int[].class);
					
					//logger.debug("method = " + method.getName() + ", array = " + Arrays.toString(primitiveIntArray));
					
					method.invoke(classInstance, (Object) primitiveIntArray);
					break;

				case Constant.DATA_TYPE_FLOAT:
					Float floatArray[] = FloatListEncoder.decodeList(value).toArray(new Float[] {});
					float primitiveFloatArray[] = new float[floatArray.length];
					for (int i = 0; i < floatArray.length; i++) {
						primitiveFloatArray[i] = floatArray[i];
					}
					method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), float[].class);
					method.invoke(classInstance, (Object) primitiveFloatArray);
					break;

				case Constant.DATA_TYPE_INT_POINT:
					Point pointArray[] = PointListEncoder.decodeList(value).toArray(new Point[] {});
					method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), Point[].class);
					method.invoke(classInstance, (Object) pointArray);
					break;

				case Constant.DATA_TYPE_FLOAT_POINT:
					FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(value).toArray(new FloatPoint[] {});
					method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), FloatPoint[].class);
					method.invoke(classInstance, (Object) floatPointArray);
					break;

				}

			} else {

				// two dimensional (non-point type) arrays

				// assuming iteration over first index surrounds the second index iterator

				switch (fieldDescriptor.getDataType()) {
				case Constant.DATA_TYPE_INT:
					Integer intArray[] = IntegerListEncoder.decodeList(value).toArray(new Integer[] {});

					int int2dArray[][] = new int[fieldDescriptor.getDimension1()][fieldDescriptor.getDimension2()];

					// flat array now needs to be read into 2-d array
					int k = 0;
					for (int i = 0; i < fieldDescriptor.getDimension1(); i++) {
						for (int j = 0; j < fieldDescriptor.getDimension2(); j++) {

							int2dArray[i][j] = intArray[k++].intValue();
						}
					}

					method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), int[][].class);
					method.invoke(classInstance, (Object) int2dArray);

					break;

				case Constant.DATA_TYPE_FLOAT:
					Float floatArray[] = FloatListEncoder.decodeList(value).toArray(new Float[] {});

					
					int dim1 = fieldDescriptor.getDimension1();
					int dim2 = fieldDescriptor.getDimension2();
					if (fieldDescriptor.getDimension1()*fieldDescriptor.getDimension2() != floatArray.length) {
						// a hack for the case where # of elements is less than field descriptor dim1 * dim2
						// in this case, dim1 is replaced with actual value, given dim2 is constant (which will work for ColorSteps)
						dim1 = floatArray.length / dim2;
					}
					
					
					float float2dArray[][] = new float[dim1][dim2];

					// flat array now needs to be read into 2-d array
					int fk = 0;
					for (int fi = 0; fi < dim1; fi++) {
						for (int fj = 0; fj < dim2; fj++) {
							float2dArray[fi][fj] = floatArray[fk++].floatValue();
						}
					}

					method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), float[][].class);
					method.invoke(classInstance, (Object) float2dArray);
					break;

					
					
				case Constant.DATA_TYPE_FLOAT_POINT:
					FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(value).toArray(new FloatPoint[] {});
					
					
					
					int d1 = fieldDescriptor.getDimension1();
					int d2 = fieldDescriptor.getDimension2();
					if (fieldDescriptor.getDimension1()*fieldDescriptor.getDimension2() != floatPointArray.length) {
						// a hack for the case where # of elements is less than field descriptor dim1 * dim2
						// in this case, dim1 is replaced with actual value, given dim2 is constant (which will work for ColorSteps)
						d1 = floatPointArray.length / d2;
					}
					
					FloatPoint fp2dArray[][] = new FloatPoint[d1][d2];

					// flat array now needs to be read into 2-d array
					int fpk = 0;
					for (int fi = 0; fi < d1; fi++) {
						for (int fj = 0; fj < d2; fj++) {
							fp2dArray[fi][fj] = floatPointArray[fpk++];
						}
					}

					method = classInstance.getClass().getMethod("set" + fieldDescriptor.getFieldName(), FloatPoint[][].class);
					method.invoke(classInstance, (Object) fp2dArray);
					break;

				}
			}

		}

	}

}
