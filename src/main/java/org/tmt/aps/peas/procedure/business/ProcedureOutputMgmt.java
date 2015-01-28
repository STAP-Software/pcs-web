/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.common.PointListEncoder;
import org.tmt.aps.peas.config.model.Constant;
import org.tmt.aps.peas.procedure.model.ProcedureIterationOutput;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureOutputField;
import org.tmt.aps.peas.procedure.model.ProcedureOutputValue;
import org.tmt.aps.peas.procedure.model.ProcedureOutputable;

@Stateless
public class ProcedureOutputMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	public ProcedureOutputable createProcedureOutput(ProcedureOutputable procedureOutput, Long procedureId) throws Exception {

		// generate all the ProcedureOutputValues for this procedureOutput
		// get all field methods from the class
		Class poClass = procedureOutput.getClass();

		Method[] methods = poClass.getMethods();

		// get the fieldId from the metadata
		Map<String, ProcedureOutputField> outputFieldMap = getOuputFieldMapForClass(poClass.getSimpleName());

		// loop over all getter methods
		for (Method method : methods) {
			if (method.getName().startsWith("get") || method.getName().startsWith("is")) {

				String fieldName = deriveFieldNameFromGetter(method.getName());
				System.out.println("fieldName = " + fieldName);

				ProcedureOutputField procedureOutputField = outputFieldMap.get(fieldName);

				if (procedureOutputField != null) {

					// construct a new ProcedureOutputValue
					ProcedureOutputValue procedureOutputValue = new ProcedureOutputValue();
					procedureOutputValue.setProcedureId(procedureId);
					procedureOutputValue.setProcedureOutputField(procedureOutputField);
					procedureOutputValue.setIteration(procedureOutput.getIteration());

					// encode the field data for store
					String data = encodeObjectFieldValue(procedureOutput, method);

					procedureOutputValue.setData(data);

					em.persist(procedureOutputValue);
				}
			}
		}

		// em.persist(procedureOutput);

		return procedureOutput;
	}

	public ProcedureOutput findProcedureOutput(Long procedureId) throws Exception {

		// fill out a list of ProcedureOutputValues
		
		TypedQuery<ProcedureOutputValue> query = em.createNamedQuery("findOutputValuesForProcedure", ProcedureOutputValue.class);
		query.setParameter("procedureId", procedureId);

		List<ProcedureOutputValue> procedureOutputList = query.getResultList();
		
		if (procedureOutputList.isEmpty()) {
			return new ProcedureOutput();
		}
		
		String fullClassName = "org.tmt.aps.peas.procedure.model." + procedureOutputList.get(0).getProcedureOutputField().getClassName();
		
		Object classInstance = Class.forName(fullClassName).newInstance();
		ProcedureOutput procedureOutput = (ProcedureOutput)classInstance;
		procedureOutput.setProcedureOutputList(procedureOutputList);

		for (ProcedureOutputValue procedureOutputValue : procedureOutputList) {
			decodeAndSetObjectFieldValue(classInstance, procedureOutputValue);
			System.out.println("procedureOutput = " + procedureOutputValue.getProcedureOutputField().getFieldName());
		}
		System.out.println("Done");
	
		// find procedure iteration outputs
		Integer iteration = 0;
		while (true) {
			query = em.createNamedQuery("findOutputValuesForProcedureIteration", ProcedureOutputValue.class);
			query.setParameter("procedureId", procedureId);
			query.setParameter("iteration", iteration);
	
			List<ProcedureOutputValue> procedureIterationOutputList = query.getResultList();
			
			if (procedureIterationOutputList.isEmpty()) {
				break;
			}

			
			fullClassName = "org.tmt.aps.peas.procedure.model." + procedureIterationOutputList.get(0).getProcedureOutputField().getClassName();
			
			classInstance = Class.forName(fullClassName).newInstance();
			ProcedureIterationOutput pio = (ProcedureIterationOutput)classInstance;
			pio.setProcedureIterationOutputList(procedureIterationOutputList);
			pio.setIteration(iteration++);

			for (ProcedureOutputValue procedureOutputValue : procedureIterationOutputList) {
				decodeAndSetObjectFieldValue(classInstance, procedureOutputValue);
			}

			procedureOutput.addIteration(pio);
			
		}
		
		return procedureOutput;
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

	private String encodeObjectFieldValue(Object object, Method method) throws Exception {

		// extract and convert the data
		Class returnTypeClass = method.getReturnType();
		Object[] args = new Object[0];

		if (returnTypeClass.isArray()) {

			Object[] returnArray = (Object[]) method.invoke(object, args);

			StringBuffer buf = new StringBuffer();
			for (Object element : returnArray) {

				if (element.getClass().isArray()) {

					if (element instanceof float[]) {
						for (float subelement : (float[])element) {
	
							buf.append("" + subelement + ", ");
						}
						
					} else if (element instanceof int[]) {
						
						for (int subelement : (int[])element) {

							buf.append("" + subelement + ", ");
						}
						
					} else {
						for (Object subelement : (Object[])element) {

							buf.append("" + subelement + ", ");
						}

					}

				} else {
					buf.append("" + element + ", ");
				}

			}
			
			float[] t = {0.0f, 2.3f};
			t.getClass().isArray();
			

			buf.delete(buf.length() - 2, buf.length());
			return buf.toString();

		} else {
			return "" + method.invoke(object, args);
		}

	}

	private void decodeAndSetObjectFieldValue(Object classInstance, ProcedureOutputValue procedureOutputValue) throws Exception {

		ProcedureOutputField procedureOutputField = procedureOutputValue.getProcedureOutputField();
		
		// get the named field's setter method
		Method method = null;

		// easy cases first
		if (procedureOutputField.isScalar()) {

			switch (procedureOutputField.getDataType()) {

			case Constant.DATA_TYPE_INT:
				Integer intValue = new Integer(procedureOutputValue.getData());
				method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), int.class);
				method.invoke(classInstance, intValue);
				break;

			case Constant.DATA_TYPE_FLOAT:
				Float floatValue = new Float(procedureOutputValue.getData());
				method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), float.class);
				method.invoke(classInstance, floatValue);
				break;

			case Constant.DATA_TYPE_DOUBLE:
				Double doubleValue = new Double(procedureOutputValue.getData());
				method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), double.class);
				method.invoke(classInstance, doubleValue);
				break;

			case Constant.DATA_TYPE_BOOLEAN:
				Boolean booleanValue = new Boolean(procedureOutputValue.getData());
				method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), boolean.class);
				method.invoke(classInstance, booleanValue);
				break;
				
			case Constant.DATA_TYPE_FLOAT_POINT:
				FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(procedureOutputValue.getData()).toArray(new FloatPoint[] {});
				FloatPoint floatPointValue = floatPointArray[0];
				method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), FloatPoint.class);
				method.invoke(classInstance, (Object) floatPointValue);
				break;


			}

		} else {

			// one and two dimensional array cases

			if (procedureOutputField.isOneDimensional()) {

				// one dimensional arrays and arrays of points

				switch (procedureOutputField.getDataType()) {

				case Constant.DATA_TYPE_INT:
					Integer intArray[] = IntegerListEncoder.decodeList(procedureOutputValue.getData()).toArray(new Integer[] {});
					int primitiveIntArray[] = new int[intArray.length];
					for (int i = 0; i < intArray.length; i++) {
						primitiveIntArray[i] = intArray[i];
					}

					method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), int[].class);
					method.invoke(classInstance, (Object) primitiveIntArray);
					break;

				case Constant.DATA_TYPE_FLOAT:
					Float floatArray[] = FloatListEncoder.decodeList(procedureOutputValue.getData()).toArray(new Float[] {});
					float primitiveFloatArray[] = new float[floatArray.length];
					for (int i = 0; i < floatArray.length; i++) {
						primitiveFloatArray[i] = floatArray[i];
					}
					method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), float[].class);
					method.invoke(classInstance, (Object) primitiveFloatArray);
					break;

				case Constant.DATA_TYPE_INT_POINT:
					Point pointArray[] = PointListEncoder.decodeList(procedureOutputValue.getData()).toArray(new Point[] {});
					method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), Point[].class);
					method.invoke(classInstance, (Object) pointArray);
					break;

				case Constant.DATA_TYPE_FLOAT_POINT:
					FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(procedureOutputValue.getData()).toArray(new FloatPoint[] {});
					method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), FloatPoint[].class);
					method.invoke(classInstance, (Object) floatPointArray);
					break;

				}

			} else {

				// two dimensional (non-point type) arrays

				// assuming iteration over first index surrounds the second index iterator

				switch (procedureOutputField.getDataType()) {
				case Constant.DATA_TYPE_INT:
					Integer intArray[] = IntegerListEncoder.decodeList(procedureOutputValue.getData()).toArray(new Integer[] {});

					int int2dArray[][] = new int[procedureOutputField.getDimension1()][procedureOutputField.getDimension2()];

					// flat array now needs to be read into 2-d array
					int k = 0;
					for (int i = 0; i < procedureOutputField.getDimension1(); i++) {
						for (int j = 0; j < procedureOutputField.getDimension2(); j++) {

							int2dArray[i][j] = intArray[k++].intValue();
						}
					}

					method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), int[][].class);
					method.invoke(classInstance, (Object) int2dArray);

					break;

				case Constant.DATA_TYPE_FLOAT:
					Float floatArray[] = FloatListEncoder.decodeList(procedureOutputValue.getData()).toArray(new Float[] {});

					float float2dArray[][] = new float[procedureOutputField.getDimension1()][procedureOutputField.getDimension2()];

					// flat array now needs to be read into 2-d array
					int fk = 0;
					for (int fi = 0; fi < procedureOutputField.getDimension1(); fi++) {
						for (int fj = 0; fj < procedureOutputField.getDimension2(); fj++) {
							float2dArray[fi][fj] = floatArray[fk++].floatValue();
						}
					}

					method = classInstance.getClass().getDeclaredMethod("set" + procedureOutputField.getFieldName(), float[][].class);
					method.invoke(classInstance, (Object) float2dArray);
					break;

				}
			}

		}

	}

}
