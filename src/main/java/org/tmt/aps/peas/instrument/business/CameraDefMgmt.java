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
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.FilterWheel;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.PupilWheel;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.instrument.model.SufsGroup;

/**
 * Session EJB managing database queries/updates for camera configuration
 * @author smichaels
 *
 */
@Stateless
public class CameraDefMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	/**
	 * @return all Filters defined in the database
	 */
	public List<Filter> findAllFilters() {
		TypedQuery<Filter> query = em.createNamedQuery("findAllFilters", Filter.class);

		return query.getResultList();
	}

	/**
	 * Creates a Filter record in the database
	 * @param filter the filter to create
	 */
	public void createFilter(Filter filter) {
		logger.info(MessageGenerator.generateMessage("record.create", "filter"));
		em.persist(filter);

	}

	/** 
	 * Updates a Filter record in the database
	 * @param filter the filter to update
	 */
	public void updateFilter(Filter filter) {

		logger.info(MessageGenerator.generateMessage("record.update", "filter"));
		em.merge(filter);

	}

	/**
	 * Updates a filterWheel record in the database
	 * @param filterWheel the filterWheel record to update
	 */
	public void updateFilterWheel(FilterWheel filterWheel) {

		logger.info(MessageGenerator.generateMessage("record.update", "filterWheel"));
		em.merge(filterWheel);

	}

	/**
	 * @return all PupilMasks defined in the database
	 */
	public List<PupilMask> findAllPupilMasks() {
		TypedQuery<PupilMask> query = em.createNamedQuery("findAllPupilMasks", PupilMask.class);

		return query.getResultList();
	}

	/**
	 * Creates a pupil mask record in the database
	 * @param pupilMask the pupil mask to create
	 */
	public void createPupilMask(PupilMask pupilMask) {

		logger.info(MessageGenerator.generateMessage("record.create", "pupilMask"));
		em.persist(pupilMask);

	}

	/**
	 * Updates a pupil mask record in the database
	 * @param pupilMask the pupil mask to update
	 */
	public void updatePupilMask(PupilMask pupilMask) {

		logger.info(MessageGenerator.generateMessage("record.update", "pupilMask"));
		em.merge(pupilMask);
	}

	/**
	 * Updates a pupilWheel record in the database
	 * @param pupilWheel the pupil wheel to update
	 */
	public void updatePupilWheel(PupilWheel pupilWheel) {

		logger.info(MessageGenerator.generateMessage("record.update", "pupilWheel"));
		em.merge(pupilWheel);

	}
	
	/**
	 * @return all pupil mask type records in the database
	 */
	public List<PupilMaskType> findAllPupilMaskTypes() {
		TypedQuery<PupilMaskType> query = em.createNamedQuery("findAllPupilMaskTypes", PupilMaskType.class);

		return query.getResultList();
	}

	/**
	 * Returns a pupil mask type given its id
	 * @param pupilMaskTypeId the pupil mask type id
	 * @return the pupil mask record for the passed id
	 */
	public PupilMaskType findPupilMaskType(Long pupilMaskTypeId) {

		return em.find(PupilMaskType.class, pupilMaskTypeId);
	}

	/**
	 * Updates a coarse tilt mirror record
	 * @param coarseTiltMirror the coarse tilt mirror to update
	 */
	public void updateCoarseTiltMirror(CoarseTiltMirror coarseTiltMirror) {

		logger.info(MessageGenerator.generateMessage("record.update", "coarseTiltMirror"));
		em.merge(coarseTiltMirror);

	}
	
	/**
	 * Updates a fine tilt mirror record
	 * @param fineTiltMirror the fine tilt mirror to update
	 */
	public void updateFineTiltMirror(FineTiltMirror fineTiltMirror) {

		logger.info(MessageGenerator.generateMessage("record.update", "fineTiltMirror"));
		em.merge(fineTiltMirror);

	}

	/**
	 * Returns an instrument record given its id.  The query also pulls in all linked records which are all the 
	 * configuration for all the elements in the instrument matching the id, plus the CCD record that is linked 
	 * to that instrument.
	 * @param instrumentId the instrument id to search for
	 * @return the instrument matching the passed id
	 */
	public Instrument findInstrument(Long instrumentId) {

		TypedQuery<Instrument> query = em.createNamedQuery("findInstrument", Instrument.class);
		query.setParameter("instrumentId", instrumentId);

		return query.getSingleResult();
	}

	/**
	 * Creates an SUFS group record
	 * @param sufsGroup the SUFS Group to create
	 */
	public void createSufsGroup(SufsGroup sufsGroup) {

		logger.info(MessageGenerator.generateMessage("record.create", "sufsGroup"));
		em.persist(sufsGroup);

	}

	/**
	 * Updates an SUFS group record
	 * @param sufsGroup the SUFS Group to update
	 */
	public void updateSufsGroup(SufsGroup sufsGroup) {

		logger.info(MessageGenerator.generateMessage("record.update", "sufsGroup"));
		em.merge(sufsGroup);

	}

	/**
	 * @return all SUFS Group records
	 */
	public List<SufsGroup> findSufsGroups() {
		TypedQuery<SufsGroup> query = em.createNamedQuery("findAllSufsGroups", SufsGroup.class);
		return query.getResultList();

	}

	/**
	 * Create a reference beam record in the database
	 * @param referenceBeam the reference beam to create
	 */
	public void createReferenceBeam(ReferenceBeam referenceBeam) {

		logger.info(MessageGenerator.generateMessage("record.create", "referenceBeam"));
		em.persist(referenceBeam);

	}

	/**
	 * Updates a reference beam record in the database
	 * @param referenceBeam the reference beam record to update
	 */
	public void updateReferenceBeam(ReferenceBeam referenceBeam) {

		logger.info(MessageGenerator.generateMessage("record.update", "referenceBeam"));
		em.merge(referenceBeam);

	}

	/**
	 * Finds the pupil mask matching the passed pupil mask type that is present on the passed pupil wheel
	 * @param pupilMaskTypeId the id of the pupil mask type to match
	 * @param pupilWheelId the id of the pupil wheel to search
	 * @return the matching pupil mask record
	 */
	public PupilMask getPupilMaskByTypeAndWheel(Long pupilMaskTypeId, Long pupilWheelId) {
		// get the pupil mask of the defined type that is currently on the wheel
		try {

			logger.debug("pupilMaskTypeId = " + pupilMaskTypeId + ", pupilWheelId = " + pupilWheelId);
			TypedQuery<PupilMask> query = em.createNamedQuery("findByPupilMaskTypeAndWheel", PupilMask.class);
			query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
			query.setParameter("pupilWheelId", pupilWheelId);

			List<PupilMask> resultList = query.getResultList();
			if (resultList.size() == 0) {
				return null;
			}
			return resultList.get(0);

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}
	
	/**
	 * Finds the filter matching the passed filter type that is present on the passed filter wheel
	 * @param filterTypeId the id of the filter type to match
	 * @param filterWheelId the id of the filter wheel to search
	 * @return the matching filter record
	 */
	public Filter getFilterByFilterTypeAndWheel(Long filterTypeId, Long filterWheelId) {
		// get the pupil mask of the defined type that is currently on the wheel
		try {
			TypedQuery<Filter> query = em.createNamedQuery("findByFilterTypeAndWheel", Filter.class);
			query.setParameter("filterTypeId", filterTypeId);
			query.setParameter("filterWheelId", filterWheelId);

			return query.getSingleResult();

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}

	/**
	 * @return all filter type records in the database
	 */
	public List<FilterType> findAllFilterTypes() {
		TypedQuery<FilterType> query = em.createNamedQuery("findAllFilterTypes", FilterType.class);

		return query.getResultList();
	}

	public List<ReferenceBeam> findAllRefBeams(Long instrumentId) {
		TypedQuery<ReferenceBeam> query = em.createNamedQuery("findRefBeamByInstrument", ReferenceBeam.class);

		query.setParameter("instrumentId", instrumentId);

		return query.getResultList();
	}
	


}
