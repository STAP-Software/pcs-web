package org.tmt.aps.peas.procedure.business;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;

@Stateless
public class ProcedureMgmt {

	
	@PersistenceContext
	private EntityManager em;
	
	public Procedure findProcedure(Long procedureId) {
		TypedQuery<Procedure> query = em.createNamedQuery("findProcedure", Procedure.class);
		query.setParameter("procedureId", procedureId);
		
		return query.getSingleResult();
	}

	
	public ProcedureConfig findDefaultProcedureConfig(Long telescopeId, Long instrumentId, Long procedureTypeId) {
		TypedQuery<ProcedureConfig> query = em.createNamedQuery("findDefaultProcedureConfig", ProcedureConfig.class);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
}
