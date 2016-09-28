/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.business.IterationEntityCache;
import org.tmt.aps.peas.config.model.Constant;
import org.tmt.aps.peas.config.model.ProcedureConfigDefaults;
import org.tmt.aps.peas.frame.model.ProcedureCcdFrame;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.session.business.SessionMgmt;
import org.tmt.aps.peas.session.model.FrameFieldDisplay;
import org.tmt.aps.peas.session.model.Session;

/** 
 * Session EJB for procedure related database queries and updates 
 * @author smichaels
 *
 */
@Stateless
public class ProcedureMgmt {

	Logger logger = Logger.getLogger(this.getClass());
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	ProcedureOutputMgmt procedureOutputMgmt;
	@EJB
	SessionMgmt sessionMgmt;
	@EJB 
	IterationEntityCache iterationEntityCache;
	
	/**
	 * Returns a procedure and related data structures (procedure output and frame list) given the procedure id
	 */
	public Procedure findProcedure(Long procedureId) {
		TypedQuery<Procedure> query = em.createNamedQuery("findProcedure", Procedure.class);
		query.setParameter("procedureId", procedureId);
		
		Procedure procedure = query.getSingleResult();
		
		iterationEntityCache.populateIterationValueList(procedure.getProcedureConfigSet().getIterationListConfig(), procedure.getProcedureType().getProcedureTypeId());
		
		try {
			ProcedureOutput procedureOutput = procedureOutputMgmt.findProcedureOutput(procedure);
			procedure.setProcedureOutput(procedureOutput);
			
			// procedure frame data 
			for (ProcedureCcdFrame procedureCcdFrame : procedure.getProcedureCcdFrameList()) {

				setupFrameLog(procedureCcdFrame);
			}
			
		} catch (Throwable e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
		
		return procedure;
	}
	
	/**
	 * Load up frame report data from a single procedureCcdFrame
	 * Ultimately populates the frame log data by calling {@link ProcedureCcdFrame#setFrameFieldDisplayList(List)} 
	 * @param procedureCcdFrame data structure including {@link org.tmt.aps.peas.frame.model.CcdFrame} and {@link org.tmt.aps.peas.refBeamMap.model.CentroidMap}
	 * @throws Exception
	 */
	public void setupFrameLog(ProcedureCcdFrame procedureCcdFrame) throws Exception {
	
		List<FrameFieldDisplay> displayList = sessionMgmt.findAllFrameFieldsToDisplay();
		
		List<FrameFieldDisplay> myList = new ArrayList<FrameFieldDisplay>();
		
		for (FrameFieldDisplay fieldDisplay : displayList) {

			Object object;
			// TODO: make this code generic later - i.e. put full class names in the field meta data table
			if (fieldDisplay.getClassName().equals("org.tmt.aps.peas.refBeamMap.model.centroidMap")) {
				object = procedureCcdFrame.getCentroidMap();
			} else {
				object = procedureCcdFrame.getCcdFrame();
			}

			// we clone the bean because hibernate caching gives us the same objects each time this is called.
			FrameFieldDisplay myDisplay = (FrameFieldDisplay)BeanUtils.cloneBean(fieldDisplay);
			
			if (object != null) {

				Class clazz = object.getClass();
				

				String fieldName = fieldDisplay.getFieldName();

				String methodPrefix = fieldDisplay.getFieldMetaData().getDataType() == Constant.DATA_TYPE_BOOLEAN ? "is" : "get";

				String methodName = methodPrefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
				Method method = clazz.getMethod(methodName, null);
								
				String value = procedureOutputMgmt.encodeObjectFieldValue(object, method, fieldDisplay.getFieldMetaData());
				
				myDisplay.setValue(value);
			}
			myList.add(myDisplay);
		}
		procedureCcdFrame.setFrameFieldDisplayList(myList);
	}

	/**
	 * Updates a procedure in the database
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Procedure updateProcedure(Procedure procedure) {
		logger.info(MessageGenerator.generateMessage("record.update", "procedure"));
		em.merge(procedure);
		return procedure;
	}
	
	/**
	 * Creates a session record in the database
	 * @param session the sessio record to create
	 * @return the created night session object
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Session createSession(Session session) {
		logger.info(MessageGenerator.generateMessage("record.create", "session"));
		em.persist(session);
		return session;
	}
	
	/**
	 * Creates a procedure record in the database
	 * @param procedure the procedure entity to create
	 * @return the created procedure entity
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Procedure createProcedure(Procedure procedure) {
		
		logger.info(MessageGenerator.generateMessage("record.create", "procedure"));
		em.persist(procedure);
		return procedure;
	}
	
	/**
	 * Returns a procedure type record given its id
	 * @param procedureTypeId the procedure type id
	 * @return the procedure type entity
	 */
	public ProcedureType findProcedureType(Long procedureTypeId) {
		TypedQuery<ProcedureType> query = em.createNamedQuery("findProcedureType", ProcedureType.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		query.setMaxResults(1);
		
		ProcedureType procedureType = query.getSingleResult();
			
		return procedureType;
	}
	
	/**
	 * Returns the default procedure configuration for this telescope, instrument and procedure type
	 * @param telescopeId Keck1 or Keck2
	 * @param instrumentId PCS1 or PCS2
	 * @param procedureTypeId the procedure type, e.g. passive tilt, fine screen, etc
	 * @return the default procedure configuration
	 */
	public ProcedureConfigDefaults findDefaultProcedureConfig(Long telescopeId, Long instrumentId, Long procedureTypeId) {
		TypedQuery<ProcedureConfigDefaults> query = em.createNamedQuery("findDefaultProcedureConfig", ProcedureConfigDefaults.class);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
}
