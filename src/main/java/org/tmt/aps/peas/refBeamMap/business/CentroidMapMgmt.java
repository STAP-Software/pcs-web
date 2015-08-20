/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.Subimage;
import org.tmt.aps.peas.config.business.MissingSpotsMgmt;
import org.tmt.aps.peas.config.model.MissingSpotList;
import org.tmt.aps.peas.config.model.PeripheralSpotList;
import org.tmt.aps.peas.config.model.SubimageDef;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.ProcedureRefBeamMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

@Stateless
public class CentroidMapMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	private static final long ONE_DAY_MS = 24 * 60 * 60 * 1000;

	@EJB
	MissingSpotsMgmt missingSpotsMgmt;

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
		if ((System.currentTimeMillis() - refBeamMap.getCreateDate().getTime()) > (ONE_DAY_MS / 2)) {
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

			return refBeamMap;

		} catch (Exception e) {
			logger.info("No reference beam map found.");
			return null;
		}

	}

	public List<SubimageDef> getSubimageDefList(Long telescopeId, Long pupilMaskTypeId) {

		TypedQuery<RefBeamMap> query = em.createNamedQuery("findRefBeamDefMap", RefBeamMap.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);

		query.setMaxResults(1);
		RefBeamMap refBeamMap = query.getSingleResult();

		// decode String into transient FloatPoint values
		List<FloatPoint> centroidList = FloatPointListEncoder.decodeList(refBeamMap.getCentroidMap().getCentroidMapData());

		// to create a list of SubimageDefs

		List<SubimageDef> subimageDefList = new ArrayList<SubimageDef>();

		int i = 0;
		for (FloatPoint centroid : centroidList) {
			// make some subimageDefs without spotTypes and missingSpotTypes
			SubimageDef subimageDef = new SubimageDef(++i, centroid, Constants.SPOT_TYPE_INTERIOR, Constants.MISSING_SPOT_TYPE_USE);
			// make some subimages without intensities or findCentResults
			subimageDefList.add(subimageDef);
		}

		// merge this list with the spotType and missingSpotType lists
		MissingSpotList missingSpotListFandI = missingSpotsMgmt.findMissingSpotList(1, telescopeId, pupilMaskTypeId);
		MissingSpotList missingSpotListAnalysis = missingSpotsMgmt.findMissingSpotList(2, telescopeId, pupilMaskTypeId);

		List<Integer> missingSpotListAnalysisDecoded = IntegerListEncoder.decodeList(missingSpotListAnalysis.getMissingSpotListEncoded());
		for (Integer spot : missingSpotListAnalysisDecoded) {
			subimageDefList.get(spot - 1).setMissingSpotType(Constants.MISSING_SPOT_TYPE_NOT_FOR_ANALYSIS);
		}

		// F&I missing value overrides analysis
		List<Integer> missingSpotListFandIDecoded = IntegerListEncoder.decodeList(missingSpotListFandI.getMissingSpotListEncoded());
		for (Integer spot : missingSpotListFandIDecoded) {
			subimageDefList.get(spot - 1).setMissingSpotType(Constants.MISSING_SPOT_TYPE_NOT_EXPECTED);
		}

		// apply peripheral spot definitions
		try {
			PeripheralSpotList peripheralSpotList = findPeripheralSpotList(pupilMaskTypeId);
			List<Integer> peripheralSpotListDecoded = IntegerListEncoder.decodeList(peripheralSpotList.getPeripheralSpotListEncoded());
			for (Integer spot : peripheralSpotListDecoded) {
				subimageDefList.get(spot - 1).setSpotType(Constants.SPOT_TYPE_PERIPHERAL);
			}
		} catch (NoResultException e) {
			// if no peripherals, then do nothing
		}

		return subimageDefList;
	}

	public PeripheralSpotList findPeripheralSpotList(Long pupilMaskTypeId) {

		TypedQuery<PeripheralSpotList> query = em.createNamedQuery("findPeripheralSpotList", PeripheralSpotList.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);

		query.setMaxResults(1);

		return query.getSingleResult();
	}

	public RefBeamMap saveRefBeamMap(RefBeamMap refBeamMap, Procedure procedure) {

		refBeamMap.setCreateDate(new Date());

		logger.info(MessageGenerator.generateMessage("record.create", "refBeamMap"));
		em.persist(refBeamMap);

		return refBeamMap;
	}

	public CentroidMap saveCentroidMap(CentroidMap centroidMap) {

		centroidMap.setCreateDate(new Date());

		logger.info(MessageGenerator.generateMessage("record.create", "centroidMap"));
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
		logger.info(MessageGenerator.generateMessage("record.create", "procedureRefBeamMap"));
		em.persist(procedureRefBeamMap);
	}

}
