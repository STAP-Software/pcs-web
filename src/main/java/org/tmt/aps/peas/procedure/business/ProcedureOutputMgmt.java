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
import org.tmt.aps.peas.config.model.MissingSpotList;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureOutputField;
import org.tmt.aps.peas.procedure.model.ProcedureOutputValue;

@Stateless
public class ProcedureOutputMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	public ProcedureOutput createProcedureOutput(ProcedureOutput procedureOutput, Long procedureId) throws Exception {

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
					procedureOutputValue.setIteration(1);

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

	public ProcedureOutput findProcedureOutput(Long procedureId) {

		// fill out a list of ProcedureOutputValues
		
		// FIXME: not sure if we have a case where we need the values populating a subclass of ProcedureOutput
		ProcedureOutput procedureOutput = new ProcedureOutput();
		TypedQuery<ProcedureOutputValue> query = em.createNamedQuery("findOutputValuesForProcedure", ProcedureOutputValue.class);
		query.setParameter("procedureId", procedureId);

		List<ProcedureOutputValue> procedureOutputList = query.getResultList();

		procedureOutput.setProcedureOutputList(procedureOutputList);
		
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
		return Character.toLowerCase(string.charAt(0)) + (string.length() > 1 ? string.substring(1) : "");
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

					for (Object subelement : returnArray) {

						buf.append("" + subelement + ", ");
					}

				} else {
					buf.append("" + element + ", ");
				}

			}

			buf.delete(buf.length() - 2, buf.length());
			return buf.toString();

		} else {
			return "" + method.invoke(object, args);
		}

	}

	private void decodeAndSetObjectFieldValue(Object constantsInstance, Constant constant) throws Exception {

		// get the named field's setter method
		Method method = null;

		// easy cases first
		if (constant.isScalar()) {

			switch (constant.getDataType()) {

			case Constant.DATA_TYPE_INT:
				Integer intValue = new Integer(constant.getData());
				method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), int.class);
				method.invoke(constantsInstance, intValue);
				break;

			case Constant.DATA_TYPE_FLOAT:
				Float floatValue = new Float(constant.getData());
				method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), float.class);
				method.invoke(constantsInstance, floatValue);
				break;

			case Constant.DATA_TYPE_DOUBLE:
				Double doubleValue = new Double(constant.getData());
				method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), double.class);
				method.invoke(constantsInstance, doubleValue);
				break;

			case Constant.DATA_TYPE_BOOLEAN:
				Boolean booleanValue = new Boolean(constant.getData());
				method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), boolean.class);
				method.invoke(constantsInstance, booleanValue);
				break;

			}

		} else {

			// one and two dimensional array cases

			if (constant.isOneDimensional()) {

				// one dimensional arrays and arrays of points

				switch (constant.getDataType()) {

				case Constant.DATA_TYPE_INT:
					Integer intArray[] = IntegerListEncoder.decodeList(constant.getData()).toArray(new Integer[] {});
					int primitiveIntArray[] = new int[intArray.length];
					for (int i = 0; i < intArray.length; i++) {
						primitiveIntArray[i] = intArray[i];
					}

					method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), int[].class);
					method.invoke(constantsInstance, (Object) primitiveIntArray);
					break;

				case Constant.DATA_TYPE_FLOAT:
					Float floatArray[] = FloatListEncoder.decodeList(constant.getData()).toArray(new Float[] {});
					float primitiveFloatArray[] = new float[floatArray.length];
					for (int i = 0; i < floatArray.length; i++) {
						primitiveFloatArray[i] = floatArray[i];
					}
					method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), float[].class);
					method.invoke(constantsInstance, (Object) primitiveFloatArray);
					break;

				case Constant.DATA_TYPE_INT_POINT:
					Point pointArray[] = PointListEncoder.decodeList(constant.getData()).toArray(new Point[] {});
					method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), Point[].class);
					method.invoke(constantsInstance, (Object) pointArray);
					break;

				case Constant.DATA_TYPE_FLOAT_POINT:
					FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(constant.getData()).toArray(new FloatPoint[] {});
					method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), FloatPoint[].class);
					method.invoke(constantsInstance, (Object) floatPointArray);
					break;

				}

			} else {

				// two dimensional (non-point type) arrays

				// assuming iteration over first index surrounds the second index iterator

				switch (constant.getDataType()) {
				case Constant.DATA_TYPE_INT:
					Integer intArray[] = IntegerListEncoder.decodeList(constant.getData()).toArray(new Integer[] {});

					int int2dArray[][] = new int[constant.getDimension1()][constant.getDimension2()];

					// flat array now needs to be read into 2-d array
					int k = 0;
					for (int i = 0; i < constant.getDimension1(); i++) {
						for (int j = 0; j < constant.getDimension2(); j++) {

							int2dArray[i][j] = intArray[k++].intValue();
						}
					}

					method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), int[][].class);
					method.invoke(constantsInstance, (Object) int2dArray);

					break;

				case Constant.DATA_TYPE_FLOAT:
					Float floatArray[] = FloatListEncoder.decodeList(constant.getData()).toArray(new Float[] {});

					float float2dArray[][] = new float[constant.getDimension1()][constant.getDimension2()];

					// flat array now needs to be read into 2-d array
					int fk = 0;
					for (int fi = 0; fi < constant.getDimension1(); fi++) {
						for (int fj = 0; fj < constant.getDimension2(); fj++) {
							float2dArray[fi][fj] = floatArray[fk++].floatValue();
						}
					}

					method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), float[][].class);
					method.invoke(constantsInstance, (Object) float2dArray);
					break;

				}
			}

		}

	}

}
