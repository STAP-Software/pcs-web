/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.business;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.instrument.model.CameraState;

@Stateless
public class CameraStateMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;


	public CameraState findCameraStateForFrame(Long ccdFrameId) {

		TypedQuery<CameraState> query = em.createNamedQuery("findCameraStateForFrame", CameraState.class);
		query.setParameter("ccdFrameId", ccdFrameId);

		return query.getSingleResult();
	}
	
	public void createCameraState(CameraState cameraState) {
		em.persist(cameraState);

	}



}
