/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import java.lang.reflect.Method;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.config.model.Constant;
import org.tmt.aps.peas.config.model.ProcedureConfigDefaults;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.FrameFieldDisplay;

@Stateless
public class ProcedureMgmt {

	Logger logger = Logger.getLogger(this.getClass());
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	ProcedureOutputMgmt procedureOutputMgmt;
	@EJB
	SessionMgmt sessionMgmt;
	
	public Procedure findProcedure(Long procedureId) {
		TypedQuery<Procedure> query = em.createNamedQuery("findProcedure", Procedure.class);
		query.setParameter("procedureId", procedureId);
		
		Procedure procedure = query.getSingleResult();
		
		try {
			ProcedureOutput procedureOutput = procedureOutputMgmt.findProcedureOutput(procedureId);
			procedure.setProcedureOutput(procedureOutput);
			
			// procedure frame data 
			for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {

				setupFrameLog(procedureCcdFrame);
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return procedure;
	}
	
	// TODO: generalize this to use procedureOutputMgmt methods
	private void setupFrameLog(ProcedureCcdFrame procedureCcdFrame) throws Exception {
	
		List<FrameFieldDisplay> displayList = sessionMgmt.findAllFrameFieldsToDisplay();

		for (FrameFieldDisplay fieldDisplay : displayList) {

			Object object;
			// TODO: make this code generic later
			if (fieldDisplay.getClassName().equals("org.tmt.aps.peas.refBeamMap.model.centroidMap")) {
				object = procedureCcdFrame.getCentroidMap();
			} else {
				object = procedureCcdFrame.getCcdFrame();
			}

			if (object != null) {

				Class clazz = object.getClass();

				String fieldName = fieldDisplay.getFieldName();

				String methodPrefix = fieldDisplay.getFieldMetaData().getDataType() == Constant.DATA_TYPE_BOOLEAN ? "is" : "get";

				String methodName = methodPrefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
				Method method = clazz.getMethod(methodName, null);

				String value = null;

				if (fieldDisplay.getFieldMetaData().getDimension1() != 0) {

					// TODO: this entire area needs to be refactored to match procedure output encoding

					float[] floatArray = (float[]) method.invoke(object, null);
					value = FloatListEncoder.encodeList(floatArray);

				} else {
					value = "" + method.invoke(object, null);
				}

				fieldDisplay.setValue(value);
			}
		}
		procedureCcdFrame.setFrameFieldDisplayList(displayList);
	}

	public Procedure updateProcedure(Procedure procedure) {
		em.merge(procedure);
		return procedure;
	}
	
	public ProcedureType findProcedureType(Long procedureTypeId) {
		TypedQuery<ProcedureType> query = em.createNamedQuery("findProcedureType", ProcedureType.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		query.setMaxResults(1);
		
		ProcedureType procedureType = query.getSingleResult();
			
		return procedureType;
	}
	
	public ProcedureConfigDefaults findDefaultProcedureConfig(Long telescopeId, Long instrumentId, Long procedureTypeId) {
		TypedQuery<ProcedureConfigDefaults> query = em.createNamedQuery("findDefaultProcedureConfig", ProcedureConfigDefaults.class);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
}
