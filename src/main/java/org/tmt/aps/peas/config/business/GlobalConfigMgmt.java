package org.tmt.aps.peas.config.business;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.tmt.aps.peas.config.model.GlobalConfig;

@Stateless
public class GlobalConfigMgmt {
	
	@PersistenceContext
	private EntityManager em;
	
	
	public GlobalConfig findDefaultConfig(Long telescopeId, Long instrumentId) {
		TypedQuery<GlobalConfig> query = em.createNamedQuery("findDefaultConfig", GlobalConfig.class);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("instrumentId", instrumentId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}

}
