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
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.model.MissingSpotList;

/**
 * Session EJB managing missing spots database queries/updates.
 * @author smichaels
 */
@Stateless
public class MissingSpotsMgmt {
	
	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Queries database for a missing spot list given the spot list type, telescope and pupil mask type
	 * @param spotListType missing from F&I or not to be used for analysis
	 * @param telescopeId Keck 1 or Keck 2
	 * @param pupilMaskTypeId the pupil mask type, such as fine screen, passive tilt, phasing or SUFS 
	 * @return the list of missing spots
	 */
	public MissingSpotList findMissingSpotList(int spotListType, Long telescopeId, Long pupilMaskTypeId) {
		System.out.println(spotListType + "::" +  pupilMaskTypeId);
		logger.debug("findMissingSpotList::");
		TypedQuery<MissingSpotList> query = em.createNamedQuery("findSpotListByTypeAndMask", MissingSpotList.class);
		query.setParameter("spotListType", spotListType);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		query.setParameter("telescopeId", telescopeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
	/**
	 * Queries database for a missing spot list given the spot list type, telescope, pupil mask type, and SUFS Group
	 * @param spotListType missing from F&I or not to be used for analysis
	 * @param telescopeId Keck 1 or Keck 2
	 * @param pupilMaskTypeId the pupil mask type, such as fine screen, passive tilt, phasing or SUFS 
	 * @param sufsGroup the SUFS group to use for missing spots
	 * @return the list of missing spots
	 */
	public MissingSpotList findMissingSpotList(int spotListType, Long telescopeId, Long pupilMaskTypeId, int sufsGroup) {
		logger.debug("findMissingSpotList::");
		TypedQuery<MissingSpotList> query = em.createNamedQuery("findSpotListByTypeMaskGroup", MissingSpotList.class);
		query.setParameter("spotListType", spotListType);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("sufsGroup", sufsGroup);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
	/**
	 * Creates a new missing spots list
	 * @param missingSpotList the new spots list to create
	 */
	public void createMissingSpotList(MissingSpotList missingSpotList) {
		logger.info(MessageGenerator.generateMessage("record.create", "missingSpotList"));
		em.persist(missingSpotList);
		
	}

	/**
	 * Updates a new missing spots list
	 * @param missingSpotList the new spots list to update
	 */
	public void updateMissingSpotList(MissingSpotList missingSpotList) {
		
		logger.info(MessageGenerator.generateMessage("record.update", "missingSpotList"));
		em.merge(missingSpotList);
		
	}


}
