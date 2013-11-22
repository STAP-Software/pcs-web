/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.business;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.refBeamMap.model.ProcedureRefBeamMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

@Stateless
public class RefBeamMapMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;



	public RefBeamMap getCurrentRefBeamMap(Long instrumentId, Long pupilMaskTypeId) {

		return null;
	}

	public void saveRefBeamMap(ProcedureRefBeamMap procedureRefBeamMap)  {

		RefBeamMap refBeamMap = procedureRefBeamMap.getRefBeamMap();

		// TODO: encode String from transient FloatPoint map

		em.persist(refBeamMap);

		associateRefBeamMap(procedureRefBeamMap);
	}


	public void associateRefBeamMap(ProcedureRefBeamMap procedureRefBeamMap) {

		// perform the association
		em.persist(procedureRefBeamMap);
	}


}
