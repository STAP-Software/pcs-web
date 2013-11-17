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
import org.tmt.aps.peas.instrument.model.CoarseTiltMirror;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.FilterWheel;
import org.tmt.aps.peas.instrument.model.FineTiltMirror;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.PupilWheel;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.instrument.model.SufsGroup;

@Stateless
public class CameraDefMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	public List<Filter> findAllFilters() {
		TypedQuery<Filter> query = em.createNamedQuery("findAllFilters", Filter.class);

		return query.getResultList();
	}

	public void createFilter(Filter filter) {
		em.persist(filter);

	}

	public void updateFilter(Filter filter) {

		em.merge(filter);

	}

	public void updateFilterWheel(FilterWheel filterWheel) {

		em.merge(filterWheel);

	}

	public List<PupilMask> findAllPupilMasks() {
		TypedQuery<PupilMask> query = em.createNamedQuery("findAllPupilMasks", PupilMask.class);

		return query.getResultList();
	}

	public void createPupilMask(PupilMask pupilMask) {

		em.persist(pupilMask);

	}

	public void updatePupilMask(PupilMask pupilMask) {

		em.merge(pupilMask);
	}

	public void updatePupilWheel(PupilWheel pupilWheel) {

		em.merge(pupilWheel);

	}

	public List<PupilMaskType> findAllPupilMaskTypes() {
		TypedQuery<PupilMaskType> query = em.createNamedQuery("findAllPupilMaskTypes", PupilMaskType.class);

		return query.getResultList();
	}

	public PupilMaskType findPupilMaskType(Long pupilMaskTypeId) {

		return em.find(PupilMaskType.class, pupilMaskTypeId);
	}

	public void updateCoarseTiltMirror(CoarseTiltMirror coarseTiltMirror) {

		em.merge(coarseTiltMirror);

	}

	public void updateFineTiltMirror(FineTiltMirror fineTiltMirror) {

		em.merge(fineTiltMirror);

	}

	public Instrument findInstrument(Long instrumentId) {

		TypedQuery<Instrument> query = em.createNamedQuery("findInstrument", Instrument.class);
		query.setParameter("instrumentId", instrumentId);

		return query.getSingleResult();
	}

	public void createSufsGroup(SufsGroup sufsGroup) {
		
		em.persist(sufsGroup);

	}
	
	public void updateSufsGroup(SufsGroup sufsGroup) {
		
		em.merge(sufsGroup);

	}
	
	
	public void createReferenceBeam(ReferenceBeam referenceBeam) {
			
		em.persist(referenceBeam);
		
	}

	public void updateReferenceBeam(ReferenceBeam referenceBeam) {

		em.merge(referenceBeam);
		
	}

	
	
}
