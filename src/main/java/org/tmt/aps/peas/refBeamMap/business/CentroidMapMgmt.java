/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.refBeamMap.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.MissingSpotsMgmt;
import org.tmt.aps.peas.config.model.M2CalcSpotList;
import org.tmt.aps.peas.config.model.MissingSpotList;
import org.tmt.aps.peas.config.model.PeripheralSpotList;
import org.tmt.aps.peas.config.model.SubimageDef;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.refBeamMap.model.ProcedureRefBeamMap;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

/**
 * Session EJB managing Centroid maps: frame centroid maps, reference beam maps and subimage definition lists.
 * @author smichaels
 *
 */
@Stateless
public class CentroidMapMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	private static final long ONE_DAY_MS = 24 * 60 * 60 * 1000;

	@EJB
	MissingSpotsMgmt missingSpotsMgmt;
	@EJB
	ComputationLibraryImpl computationLibrary;
	@EJB
	ConstantsCache constantsCache;
	@EJB
	CameraDefMgmt cameraDefMgmt;

	@PersistenceContext	
	private EntityManager em;

	/**
	 * Returns the reference beam map current to this session, given the instrument, pupil mask type and filter type
	 * @param instrumentId PCS1 or PCS2
	 * @param pupilMaskTypeId the pupil mask type id
	 * @param filterTypeId the filter type id
	 * @return the reference beam map
	 */
	public RefBeamMap getCurrentSessionRefBeamMap(Long instrumentId, Long pupilMaskTypeId, Long filterTypeId) {
		return getCurrentSessionRefBeamMap(instrumentId, pupilMaskTypeId, filterTypeId, -1);
	}

	/**
	 * Returns the reference beam map current to this session, given the instrument, pupil mask type, filter type and SUFS group number
	 * @param instrumentId PCS1 or PCS2
	 * @param pupilMaskTypeId the pupil mask type id
	 * @param filterTypeId the filter type id
	 * @param sufsGroupNumber the SUFS group number
	 * @return the reference beam map
	 */
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

	/**
	 * Returns the newest reference beam map stored in the database, for a given instrument, pupil mask type, filter type and SUFS group number
	 * @param instrumentId PCS1 or PCS2
	 * @param pupilMaskTypeId the pupil mask type id
	 * @param filterTypeId the filter type id
	 * @param sufsGroupNumber the SUFS group number
	 * @return the reference beam map
	 */
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

	/**
	 * Returns the newest reference beam map stored in the database, for a given instrument and pupil mask type
	 * @param instrumentId PCS1 or PCS2
	 * @param pupilMaskTypeId the pupil mask type id
	 * @return the reference beam map
	 */
	public RefBeamMap getNewestRefBeamMap(Long instrumentId, Long pupilMaskTypeId, int sufsGroupNumber) {

		TypedQuery<RefBeamMap> query = em.createNamedQuery("findNewestRefBeamMap", RefBeamMap.class);
		if (sufsGroupNumber >= 0) {
			query = em.createNamedQuery("findNewestSufsRefBeamMap", RefBeamMap.class);
			query.setParameter("sufsGroupNumber", sufsGroupNumber);
		} else {
			query = em.createNamedQuery("findNewestRefBeamMap", RefBeamMap.class);
		}

		query.setParameter("instrumentId", instrumentId);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);

		query.setMaxResults(1);
		try {
			RefBeamMap refBeamMap = query.getSingleResult();

			return refBeamMap;

		} catch (Exception e) {
			logger.info("No reference beam map found.");
			return null;
		}

	}
	
	
	/**
	 * Retrieves a subimage definition list from the database, for a telecscope and pupil mask type
	 * @param telescopeId Keck1 or Keck2
	 * @param pupilMaskTypeId the pupil mask type
	 * @return the subimage definition list
	 */
	public List<SubimageDef> getSubimageDefList(Long telescopeId, Long instrumentId, Long pupilMaskTypeId, Integer[] mirrorConfig) throws Exception {
		return getSubimageDefList(telescopeId, instrumentId, pupilMaskTypeId, mirrorConfig, null);
	}
	
	/**
	 * Retrieves a subimage definition list from the database, for a telecscope, pupil mask type and SUFS group number
	 * @param telescopeId Keck1 or Keck2
	 * @param pupilMaskTypeId the pupil mask type
	 * @param sufsGroupNumber the SUFS group number
	 * @return the subimage definition list
	 */
	public List<SubimageDef> getSubimageDefList(Long telescopeId, Long instrumentId, Long pupilMaskTypeId, Integer[] mirrorConfig, Integer sufsGroupNumber) throws Exception {
		
		RefBeamMap refBeamMap = null;
				
		TypedQuery<RefBeamMap> query = em.createNamedQuery("findRefBeamDefMap", RefBeamMap.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
	
		query.setMaxResults(1);
		refBeamMap = query.getSingleResult();

		// decode String into transient FloatPoint values
		List<FloatPoint> centroidList = FloatPointListEncoder.decodeList(refBeamMap.getCentroidMap().getCentroidMapData());
		
		
		// get the theoretical subaperatures for the mask type (meters at primary mirror)
		FloatPoint[] theorecticalSubaperatures = constantsCache.getMaskTheoreticalLocations(pupilMaskTypeId, sufsGroupNumber);
		
		FloatPoint[] refMapTheoreticalSubaperatures = constantsCache.getMaskTheoreticalLocations(pupilMaskTypeId, 0);
		
		
		// the "centroidList" is calculated from the theorecticalSubaperatures, m1ToCcdScale and ccd pixelSize
		
		// 1. the pupilmask is queried given the instrumentId and pupilMaskTypeId
		
		Instrument instrument = cameraDefMgmt.findInstrument(instrumentId);
		
		PupilMask pupilMask = cameraDefMgmt.getPupilMaskByTypeAndWheel(pupilMaskTypeId, instrument.getCamera().getPupilWheel().getPupilWheelId());
		
		// 2. the ccd is queried given the instrumentId
		Ccd ccd = instrument.getCcd();
		
		// 3. the centroidList is generated by applying the scale factor to each theoretical location
		FloatPoint[] ccdSubaperatureLocations = scaleMaskLocationsFromM1ToCcd(refMapTheoreticalSubaperatures, pupilMask.getM1ToCcdScale(), 
				ccd.getCcdType().getPixelSize(), ccd.getColCount(), ccd.getRowCount());
		
		// 4. we can check against the original values here (get max deviation)
		
		FloatPoint[] originalList = centroidList.toArray(new FloatPoint[0]);
		float max = 0.0f;
		for (int i=0; i<ccdSubaperatureLocations.length; i++) {
			float deltaX = ccdSubaperatureLocations[i].x - originalList[i].x;
			float deltaY = ccdSubaperatureLocations[i].y - originalList[i].y;
			float delta = (float)Math.sqrt(deltaX*deltaX + deltaY*deltaY);
			max = Math.max(Math.abs(delta), max);
		}
		
		logger.debug("Number of spots: " + ccdSubaperatureLocations.length);
		logger.debug("Max pixel difference is: " + max);
		
		
		// to create a list of SubimageDefs

		List<SubimageDef> subimageDefList = new ArrayList<SubimageDef>();

		int i = 0;
		for (FloatPoint centroid : ccdSubaperatureLocations) {
			// make some subimageDefs without spotTypes and missingSpotTypes
			SubimageDef subimageDef = new SubimageDef(++i, centroid, Constants.SPOT_TYPE_INTERIOR, Constants.MISSING_SPOT_TYPE_USE, Constants.MISSING_SPOT_TYPE_USE, 0);
			// make some subimages without intensities or findCentResults
			subimageDefList.add(subimageDef);
		}

		// merge this list with the spotType and missingSpotType lists
		MissingSpotList missingSpotListFandI = null;
		MissingSpotList missingSpotListAnalysis = null;
		MissingSpotList missingSpotListNphAnalysis = null;
		if (sufsGroupNumber == null) {
		
			missingSpotListFandI = missingSpotsMgmt.findMissingSpotList(1, telescopeId, pupilMaskTypeId);
			missingSpotListAnalysis = missingSpotsMgmt.findMissingSpotList(2, telescopeId, pupilMaskTypeId);

		} else {
			missingSpotListFandI = missingSpotsMgmt.findMissingSpotList(1, telescopeId, pupilMaskTypeId, sufsGroupNumber);
			missingSpotListAnalysis = missingSpotsMgmt.findMissingSpotList(2, telescopeId, pupilMaskTypeId, sufsGroupNumber);			
		}
		
		boolean[] mirrorConfigAnalysisSubaperatures = computationLibrary.determineMissingSegmentAnalysisSubimages(theorecticalSubaperatures, 
				constantsCache.getPrimaryMirrorConstants().getSegmentCenters(), 
				constantsCache.getPrimaryMirrorConstants().getaHex(), 
				mirrorConfig);

		// start to build the composite FandI missing spot list
		List<Integer> fullAnalysisMissingSpotList = new ArrayList<Integer>();
	
		// fold in incomplete mirror segments - this only applies to Phasing spots
		if (pupilMaskTypeId.equals(PupilMaskType.PUPIL_MASK_TYPE_ID_160)) {
			for (int j=0; j<mirrorConfigAnalysisSubaperatures.length; j++) {
				if (!mirrorConfigAnalysisSubaperatures[j]) {
					fullAnalysisMissingSpotList.add(Integer.valueOf(j+1));
				}
			}
		}
		
		// fold in normal analysis missing spots
		List<Integer> missingSpotListAnalysisDecoded = IntegerListEncoder.decodeList(missingSpotListAnalysis.getMissingSpotListEncoded());
		for (Integer spot : missingSpotListAnalysisDecoded) {
			if (!fullAnalysisMissingSpotList.contains(spot)) {
				fullAnalysisMissingSpotList.add(spot);
			}
		}

		for (Integer spot : fullAnalysisMissingSpotList) {
			subimageDefList.get(spot - 1).setMissingSpotType(Constants.MISSING_SPOT_TYPE_NOT_FOR_ANALYSIS);
		}
		
		// F&I missing value overrides analysis
		List<Integer> missingSpotListFandIDecoded = IntegerListEncoder.decodeList(missingSpotListFandI.getMissingSpotListEncoded());
		
		// fold in incomplete mirror state.  This will add to the FI missing spots list (decoded)
		// determine missing spot list based on mirror config and mask type
		
		boolean[] mirrorConfigPresentSubaperatures = computationLibrary.determineMissingSegmentSubaperatures(
				theorecticalSubaperatures, 
				constantsCache.getPrimaryMirrorConstants().getSegmentCenters(), 
				constantsCache.getPrimaryMirrorConstants().getaHex(), 
				mirrorConfig);
		
		
		// start to build the composite FandI missing spot list
		List<Integer> fullFandIMissingSpotList = new ArrayList<Integer>();
		
		if (pupilMaskTypeId.equals(PupilMaskType.PUPIL_MASK_TYPE_ID_160)) {
			System.out.println(Arrays.toString(mirrorConfigPresentSubaperatures));
		}
		
		// fold in incomplete mirror segments - do not do this for Sufs ref maps
		if (sufsGroupNumber == null || sufsGroupNumber.intValue() > 0) {
			for (int j=0; j<mirrorConfigPresentSubaperatures.length; j++) {
				if (!mirrorConfigPresentSubaperatures[j]) {
					fullFandIMissingSpotList.add(Integer.valueOf(j+1));
				}
			}
		}
		
		// fold in normal missing spots
		for (Integer spot : missingSpotListFandIDecoded) {
			if (!fullFandIMissingSpotList.contains(spot)) {
				fullFandIMissingSpotList.add(spot);
			}
		}
		
		
		if (pupilMaskTypeId.equals(PupilMaskType.PUPIL_MASK_TYPE_ID_160)) {
		System.out.println("full list = " + fullFandIMissingSpotList + ", sufsGroup = " + sufsGroupNumber);
		}
		
		for (Integer spot : fullFandIMissingSpotList) {
			subimageDefList.get(spot - 1).setMissingSpotType(Constants.MISSING_SPOT_TYPE_NOT_EXPECTED);
			subimageDefList.get(spot - 1).setNphMissingSpotType(Constants.MISSING_SPOT_TYPE_NOT_EXPECTED);
		}
		
		// special case for NPH
		if (pupilMaskTypeId.longValue() == PupilMaskType.PUPIL_MASK_TYPE_ID_160.longValue()) {
			missingSpotListNphAnalysis = missingSpotsMgmt.findMissingSpotList(3, telescopeId, pupilMaskTypeId);
			List<Integer> missingSpotListNphAnalysisDecoded = IntegerListEncoder.decodeList(missingSpotListNphAnalysis.getMissingSpotListEncoded());
			for (Integer spot : missingSpotListNphAnalysisDecoded) {
				subimageDefList.get(spot - 1).setNphMissingSpotType(Constants.MISSING_SPOT_TYPE_NOT_FOR_ANALYSIS);
			}
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
		
		// apply M2 Calc spot definitions
		if (pupilMaskTypeId == Constants.PUPIL_MASK_FINE_SCREEN) {
			try {
				M2CalcSpotList m2CalcSpotList = findM2CalcSpotList(telescopeId);
				List<Integer> m2CalcSpotListDecoded = IntegerListEncoder.decodeList(m2CalcSpotList.getM2CalcSpotListEncoded());
				for (Integer spot : m2CalcSpotListDecoded) {
					
					if (mirrorConfigPresentSubaperatures[spot - 1]) {
						// use for M2 calc if it is in the list and is not missing due to an incomplete mirror
						subimageDefList.get(spot - 1).setUseForM2Calc(1);
					
					}
				}
			} catch (NoResultException e) {
				// if no M2Calcs, then do nothing
			}
		}
		
		

		return subimageDefList;
	}

	
	private FloatPoint[] scaleMaskLocationsFromM1ToCcd(FloatPoint[] m1Coordinates, float m1ToCcdScale, float pixelSize, int ccdWidthPixels, int ccdHeightPixels) {
		
		// scale must be applied at M1 origin, then coordinate transform to ccd (x,y)
		
		FloatPoint[] scaledCoordinates = new FloatPoint[m1Coordinates.length];
		
		// m1ToCcdScale is in m, pixelSize in meters
		
		float m1ToCcdScalePixels = m1ToCcdScale / pixelSize;
		// ccdCoordinateTranslation translates to pixel origin
		FloatPoint ccdCoordinateTranslation = new FloatPoint(ccdWidthPixels/2.0f, ccdHeightPixels/2.0f);
		// ccdCoordinateTransform negates the y coordinates
		FloatPoint ccdCoordinateTransform = new FloatPoint(1.0f, -1.0f);
		
		for (int i=0; i<m1Coordinates.length; i++) {
			// apply the scale
			FloatPoint scaledCoordinate = new FloatPoint(m1Coordinates[i].x * m1ToCcdScalePixels, m1Coordinates[i].y * m1ToCcdScalePixels);
			
			// negate all the y's
			// transform origin
			scaledCoordinates[i] = scaledCoordinate.prod(ccdCoordinateTransform).add(ccdCoordinateTranslation);
			
		}
				
		return scaledCoordinates;
	}
	
	
	/**
	 * Queries the database for the list of peripheral spots for a given pupil mask type
	 * @param pupilMaskTypeId the pupil mask type
	 * @return the list of peripheral spots
	 */
	public PeripheralSpotList findPeripheralSpotList(Long pupilMaskTypeId) {

		TypedQuery<PeripheralSpotList> query = em.createNamedQuery("findPeripheralSpotList", PeripheralSpotList.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);

		query.setMaxResults(1);

		return query.getSingleResult();
	}

	/**
	 * Queries the database for the list of M2 calculation spots for a given telescope
	 * @param telescopeId Keck1 or Keck2
	 * @return the list of M2 calculation spots
	 */
	public M2CalcSpotList findM2CalcSpotList(Long telescopeId) {

		TypedQuery<M2CalcSpotList> query = em.createNamedQuery("findM2CalcSpotList", M2CalcSpotList.class);
		query.setParameter("telescopeId", telescopeId);

		query.setMaxResults(1);

		return query.getSingleResult();
	}

	/**
	 * Saves a reference beam map to the database
	 * @param refBeamMap the reference beam map to save
	 * @return the saved reference beam map entity
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public RefBeamMap saveRefBeamMap(RefBeamMap refBeamMap) {

		CentroidMap centroidMap = em.find(CentroidMap.class, refBeamMap.getCentroidMap().getCentroidMapId());
		refBeamMap.setCentroidMap(centroidMap);
		
		refBeamMap.setCreateDate(new Date());

		logger.info(MessageGenerator.generateMessage("record.create", "refBeamMap"));
		em.persist(refBeamMap);

		return refBeamMap;
	}

	/**
	 * Saves a centroid map to the database
	 * @param centroidMap the centroid map entity
	 * @return the saved centroid map entity
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CentroidMap saveCentroidMap(CentroidMap centroidMap) {

		centroidMap.setCreateDate(new Date());

		logger.info(MessageGenerator.generateMessage("record.create", "centroidMap"));
		em.persist(centroidMap);

		return centroidMap;
	}

	/**
	 * Associates a reference beam map with a procedure as the reference beam map used in that procedure
	 * @param refBeamMap the reference beam map
	 * @param procedure the procedure to associate the reference beam map with
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void associateRefBeamMap(RefBeamMap refBeamMap, Procedure procedure) {


		ProcedureRefBeamMap procedureRefBeamMap = new ProcedureRefBeamMap();
		procedureRefBeamMap.setProcedure(procedure);
		procedureRefBeamMap.setRefBeamMap(refBeamMap);

		// perform the association
		logger.info(MessageGenerator.generateMessage("record.create", "procedureRefBeamMap"));
		em.persist(procedureRefBeamMap);
	}

}
