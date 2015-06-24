/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.ProcedureRefBeamMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

@Stateless
public class CentroidMapMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	private static final long ONE_DAY_MS = 24 * 60 * 60 * 1000;
	
	@PersistenceContext
	private EntityManager em;

	public RefBeamMap getCurrentSessionRefBeamMap(Long instrumentId, Long pupilMaskTypeId, Long filterTypeId) {
		return getCurrentSessionRefBeamMap(instrumentId, pupilMaskTypeId, filterTypeId, -1);
	}
		
	public RefBeamMap getCurrentSessionRefBeamMap(Long instrumentId, Long pupilMaskTypeId, Long filterTypeId, int sufsGroupNumber) {
		RefBeamMap refBeamMap = getCurrentRefBeamMap(instrumentId, pupilMaskTypeId, filterTypeId, sufsGroupNumber);
		
		if (refBeamMap == null) {
			return null;
		}
		
		// if older than 12 hours, then is not from this night session
		if ((System.currentTimeMillis() - refBeamMap.getCreateDate().getTime()) > (ONE_DAY_MS/2)) {
			return null;
		}
		
		return refBeamMap;
	}
	
	public RefBeamMap getCurrentRefBeamMap(Long instrumentId, Long pupilMaskTypeId, Long filterTypeId, int sufsGroupNumber) {

		TypedQuery<RefBeamMap> query;
		if (sufsGroupNumber >= 0) {
			query = em.createNamedQuery("findCurrentSufsRefBeamMap", RefBeamMap.class);
			query.setParameter("sufsGroupNumber", sufsGroupNumber);
		} else {
			query = em.createNamedQuery("findCurrentRefBeamMap", RefBeamMap.class);			
		}
		 
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		query.setParameter("filterTypeId", filterTypeId);

		query.setMaxResults(1);
		try {
			RefBeamMap refBeamMap = query.getSingleResult();
			// decode String into transient FloatPoint values
			List<FloatPoint> values = FloatPointListEncoder.decodeList(refBeamMap.getCentroidMap().getCentroidMapData());
			refBeamMap.getCentroidMap().setValues(values);
	
			return refBeamMap;
			
		} catch (Exception e) {
			logger.info("No reference beam map found.");
			return null;
		}

	}



	public RefBeamMap getRefBeamDefMap(Long pupilMaskTypeId) {

		TypedQuery<RefBeamMap> query = em.createNamedQuery("findRefBeamDefMap", RefBeamMap.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);

		query.setMaxResults(1);
		RefBeamMap refBeamMap = query.getSingleResult();

		// decode String into transient FloatPoint values
		List<FloatPoint> values = FloatPointListEncoder.decodeList(refBeamMap.getCentroidMap().getCentroidMapData());
		refBeamMap.getCentroidMap().setValues(values);

		return refBeamMap;
	}

	public RefBeamMap saveRefBeamMap(RefBeamMap refBeamMap, Procedure procedure) {

		refBeamMap.setCreateDate(new Date());
				
		em.persist(refBeamMap);

		return refBeamMap;
	}

	public CentroidMap saveCentroidMap(CentroidMap centroidMap) {

		centroidMap.setCreateDate(new Date());
		
		em.persist(centroidMap);

		return centroidMap;
	}

	public void associateRefBeamMap(RefBeamMap refBeamMap, Procedure procedure) {

		// if refBeam map does not exist, then create it
		if (refBeamMap.isNewRecord()) {
			saveRefBeamMap(refBeamMap, procedure);
		}
		
		ProcedureRefBeamMap procedureRefBeamMap = new ProcedureRefBeamMap();
		procedureRefBeamMap.setProcedure(procedure);
		procedureRefBeamMap.setRefBeamMap(refBeamMap);

		// perform the association
		em.persist(procedureRefBeamMap);
	}

	
}
