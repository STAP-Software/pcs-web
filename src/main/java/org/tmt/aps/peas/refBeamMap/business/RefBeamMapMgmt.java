/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.business;

import java.util.Arrays;
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
import org.tmt.aps.peas.refBeamMap.model.ProcedureRefBeamMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

@Stateless
public class RefBeamMapMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	private static final long ONE_DAY_MS = 24 * 60 * 60 * 1000;
	
	@PersistenceContext
	private EntityManager em;

	public RefBeamMap getCurrentSessionRefBeamMap(Long instrumentId, Long pupilMaskTypeId) {
		RefBeamMap refBeamMap = getCurrentRefBeamMap(instrumentId, pupilMaskTypeId);
		
		if (refBeamMap == null) {
			return null;
		}
		
		// if older than 12 hours, then is not from this night session
		if ((System.currentTimeMillis() - refBeamMap.getCreateDate().getTime()) > (ONE_DAY_MS/2)) {
			return null;
		}
		
		return refBeamMap;
	}
	
	public RefBeamMap getCurrentRefBeamMap(Long instrumentId, Long pupilMaskTypeId) {

		TypedQuery<RefBeamMap> query = em.createNamedQuery("findCurrentRefBeamMap", RefBeamMap.class);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);

		query.setMaxResults(1);
		RefBeamMap refBeamMap = query.getSingleResult();

		// decode String into transient FloatPoint values
		List<FloatPoint> values = FloatPointListEncoder.decodeList(refBeamMap.getRefBeamMapData());
		refBeamMap.setValues(values);

		return refBeamMap;
	}

	public RefBeamMap getFirstRefBeamMap(Long instrumentId, Long pupilMaskTypeId) {

		TypedQuery<RefBeamMap> query = em.createNamedQuery("findFirstRefBeamMap", RefBeamMap.class);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);

		query.setMaxResults(1);
		RefBeamMap refBeamMap = query.getSingleResult();

		// decode String into transient FloatPoint values
		List<FloatPoint> values = FloatPointListEncoder.decodeList(refBeamMap.getRefBeamMapData());
		refBeamMap.setValues(values);
		
		return refBeamMap;
		
	}

	public RefBeamMap getRefBeamDefMap(Long pupilMaskTypeId) {

		TypedQuery<RefBeamMap> query = em.createNamedQuery("findRefBeamDefMap", RefBeamMap.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);

		query.setMaxResults(1);
		RefBeamMap refBeamMap = query.getSingleResult();

		// decode String into transient FloatPoint values
		List<FloatPoint> values = FloatPointListEncoder.decodeList(refBeamMap.getRefBeamMapData());
		refBeamMap.setValues(values);

		return refBeamMap;
	}

	public RefBeamMap saveRefBeamMap(List<FloatPoint> centroids, Procedure procedure) {

		RefBeamMap refBeamMap = new RefBeamMap();
		refBeamMap.setCreateDate(new Date());
		refBeamMap.setFirstRefBeamMapFlg(0);
		refBeamMap.setInstrument(procedure.getInstrument());
		refBeamMap.setPupilMaskType(procedure.getProcedureConfig().getPupilMask().getPupilMaskType());
		refBeamMap.setRefBeamDefMapFlg(0);
		refBeamMap.setValues(centroids);

		// encode String from transient FloatPoint map
		String encodedData = FloatPointListEncoder.encodeList(refBeamMap.getValues());
		refBeamMap.setRefBeamMapData(encodedData);
		
		em.persist(refBeamMap);

		return refBeamMap;
	}

	public void associateRefBeamMap(RefBeamMap refBeamMap, Procedure procedure) {

		ProcedureRefBeamMap procedureRefBeamMap = new ProcedureRefBeamMap();
		procedureRefBeamMap.setProcedure(procedure);
		procedureRefBeamMap.setRefBeamMap(refBeamMap);

		// perform the association
		em.persist(procedureRefBeamMap);
	}

}
