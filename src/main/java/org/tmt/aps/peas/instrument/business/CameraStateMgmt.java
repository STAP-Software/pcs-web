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
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.instrument.model.CameraState;

/**
 * Session EJB managing database queries/updates for camera state.
 * @author smichaels
 *
 */
@Stateless
public class CameraStateMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;


	/**
	 * Query the database for the camera state record that is linked to the passed CCD frame id.  The camera state is the state of the camera when the CCD frame was taken.
	 * @param ccdFrameId the id of the CCD frame record that is linked to the camera state record
	 * @return the camera state record linked to the passed CCD record id
	 */
	public CameraState findCameraStateForFrame(Long ccdFrameId) {

		TypedQuery<CameraState> query = em.createNamedQuery("findCameraStateForFrame", CameraState.class);
		query.setParameter("ccdFrameId", ccdFrameId);

		return query.getSingleResult();
	}
	
	/**
	 * Creates a camera state record
	 * @param cameraState the camera state to create
	 */
	public void createCameraState(CameraState cameraState) {

		logger.info(MessageGenerator.generateMessage("record.create", "cameraState"));
		em.persist(cameraState);

	}



}
