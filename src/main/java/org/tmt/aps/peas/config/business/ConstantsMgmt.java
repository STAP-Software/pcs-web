/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import java.lang.reflect.Method;
import java.util.List;

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

/**
 * Session EJB to load constants from the database.  Reads in and decodes array and coordinate data from string encoded data stored in database.
 * @author smichaels
 */
@Stateless
public class ConstantsMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	/**
	 * Query to load all constants from the database
	 * @return a list of constants, each of which is a field description and encoded data
	 */
	public List<Constant> findAllConstants() {
		TypedQuery<Constant> query = em.createNamedQuery("findAllConstants", Constant.class);

		return query.getResultList();
	}

	/**
	 * Loads the constants from the database, decodes data values according to field metadata such as data type and dimensions for all constants for which the metadata classname matches a passed object class names.
	 * Each passed instance is populated with its associated decoded constants data.
	 * @param instances instantiation of constants data classes, such as (@link org.tmt.aps.peas.config.model.PrimaryMirrorConstants}, {@link org.tmt.aps.peas.config.model.PhasingConstants}, etc.
	 * @throws Exception
	 */
	public void loadConstants(List<Object> instances) throws Exception {

		List<Constant> constantList = findAllConstants();

		for (Constant constant : constantList) {

			// get the appropriate class instance
			Object constantsInstance = null;
			for (Object instance : instances) {
				if (instance.getClass().getName().equals(constant.getClassName())) {
					constantsInstance = instance;
				}
			}

			if (constantsInstance == null) {
				continue;
			}

			// call
			encodeObjectFieldValue(constantsInstance, constant);
			
		}
	}
			
	private void encodeObjectFieldValue(Object constantsInstance, Constant constant) throws Exception {
			
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

				}

			} else {

				// one and two dimensional array cases

				if (constant.isOneDimensional()) {

					// one dimensional arrays and arrays of points

					switch (constant.getDataType()) {

					case Constant.DATA_TYPE_INT:
						Integer intArray[] = IntegerListEncoder.decodeList(constant.getData()).toArray(new Integer[] {});
						int primitiveIntArray[] = new int[intArray.length];
						for (int i=0; i<intArray.length; i++) {
							primitiveIntArray[i] = intArray[i];
						}
						
						method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), int[].class);
						method.invoke(constantsInstance, (Object) primitiveIntArray);
						break;

					case Constant.DATA_TYPE_FLOAT:
						Float floatArray[] = FloatListEncoder.decodeList(constant.getData()).toArray(new Float[] {});
						float primitiveFloatArray[] = new float[floatArray.length];
						for (int i=0; i<floatArray.length; i++) {
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
						int k=0;
						for (int i=0; i<constant.getDimension1(); i++) {
							for (int j=0; j<constant.getDimension2(); j++) {
								
								int2dArray[i][j] = intArray[k++].intValue();
							}
						}
						
						method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), int[][].class);
						method.invoke(constantsInstance, (Object)int2dArray);
						
						break;

					case Constant.DATA_TYPE_FLOAT:
						Float floatArray[] = FloatListEncoder.decodeList(constant.getData()).toArray(new Float[] {});
						
						float float2dArray[][] = new float[constant.getDimension1()][constant.getDimension2()];
						
						// flat array now needs to be read into 2-d array
						int fk=0;
						for (int fi=0; fi<constant.getDimension1(); fi++) {
							for (int fj=0; fj<constant.getDimension2(); fj++) {
								float2dArray[fi][fj] = floatArray[fk++].floatValue();
							}
						}
						
						method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), float[][].class);
						method.invoke(constantsInstance, (Object)float2dArray);
						break;

						
					case Constant.DATA_TYPE_FLOAT_POINT:
						FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(constant.getData()).toArray(new FloatPoint[] {});
						
						FloatPoint floatPoint2dArray[][] = new FloatPoint[constant.getDimension1()][constant.getDimension2()];
						
						// flat array now needs to be read into 2-d array
						int fpk=0;
						for (int fpi=0; fpi<constant.getDimension1(); fpi++) {
							for (int fpj=0; fpj<constant.getDimension2(); fpj++) {
								floatPoint2dArray[fpi][fpj] = floatPointArray[fpk++];
							}
						}
						
						method = constantsInstance.getClass().getDeclaredMethod("set" + constant.getFieldName(), FloatPoint[][].class);
						method.invoke(constantsInstance, (Object) floatPoint2dArray);
						break;

						
					}
				}

			}


	}
}
