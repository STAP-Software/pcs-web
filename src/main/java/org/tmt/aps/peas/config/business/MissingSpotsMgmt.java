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
import org.tmt.aps.peas.config.model.MissingSpotList;

@Stateless
public class MissingSpotsMgmt {
	
	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	
	public MissingSpotList findMissingSpotList(int spotListType, Long pupilMaskTypeId) {
		logger.debug("findMissingSpotList::");
		TypedQuery<MissingSpotList> query = em.createNamedQuery("findSpotListByTypeAndMask", MissingSpotList.class);
		query.setParameter("spotListType", spotListType);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}

}
