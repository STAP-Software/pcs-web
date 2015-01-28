/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.procedure.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.config.model.ProcedureConfigDefaults;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureType;

@Stateless
public class ProcedureMgmt {

	Logger logger = Logger.getLogger(this.getClass());
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	ProcedureOutputMgmt procedureOutputMgmt;
	
	public Procedure findProcedure(Long procedureId) {
		TypedQuery<Procedure> query = em.createNamedQuery("findProcedure", Procedure.class);
		query.setParameter("procedureId", procedureId);
		
		Procedure procedure = query.getSingleResult();
		
		try {
			ProcedureOutput procedureOutput = procedureOutputMgmt.findProcedureOutput(procedureId);
			procedure.setProcedureOutput(procedureOutput);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return procedure;
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
