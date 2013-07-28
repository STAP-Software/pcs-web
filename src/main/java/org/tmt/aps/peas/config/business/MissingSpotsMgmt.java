package org.tmt.aps.peas.config.business;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.tmt.aps.peas.config.model.MissingSpotList;

@Stateless
public class MissingSpotsMgmt {
	
	@PersistenceContext
	private EntityManager em;
	
	
	public MissingSpotList findMissingSpotList(int spotListType, Long pupilMaskTypeId) {
		TypedQuery<MissingSpotList> query = em.createNamedQuery("findSpotListByTypeAndMask", MissingSpotList.class);
		query.setParameter("spotListType", spotListType);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}

}
