/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.instrument.model.Ccd;

@Stateless
public class CcdDefMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	
	public List<Ccd> findAllCcds() {
		TypedQuery<Ccd> query = em.createNamedQuery("findAllCcds", Ccd.class);

		return query.getResultList();
	}

	public void createCcd(Ccd ccd) {
		em.persist(ccd);
		
	}

	public void updateCcd(Ccd ccd) {
		
		em.merge(ccd);
		
	}
	




	
	
}
