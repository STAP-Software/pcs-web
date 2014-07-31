/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.config.model.FIConfig;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.config.model.GlobalConfig;

@Stateless
public class GlobalConfigMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	
	public GlobalConfig findDefaultConfig(Long telescopeId, Long instrumentId) {
		TypedQuery<GlobalConfig> query = em.createNamedQuery("findDefaultConfig", GlobalConfig.class);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("instrumentId", instrumentId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}

	public FIConfig findFIConfig(Long instrumentId, Long pupilMaskTypeId) {
		TypedQuery<FIConfig> query = em.createNamedQuery("findByMaskTypeAndInstrument", FIConfig.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		query.setParameter("instrumentId", instrumentId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();

	}

	public FindCentConfig findFindCentConfig(Long pupilMaskTypeId) {
		
		TypedQuery<FindCentConfig> query = em.createNamedQuery("findByMaskType", FindCentConfig.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}
}
