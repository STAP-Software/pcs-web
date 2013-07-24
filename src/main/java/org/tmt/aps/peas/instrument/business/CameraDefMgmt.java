package org.tmt.aps.peas.instrument.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.FilterWheel;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilWheel;
import org.tmt.aps.peas.session.model.Session;

@Stateless
public class CameraDefMgmt {

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



	
	
}
