/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.config.model.FIConfigDefaults;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.CcdGain;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

/**
 * Session EJB managing database queries/updates for CCD configuration records
 * @author smichaels
 *
 */
@Stateless
public class CcdDefMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	@EJB
	PhysicalModel physicalModel;

	/**
	 * @return all CCD records in the database
	 */
	public List<Ccd> findAllCcds() {
		TypedQuery<Ccd> query = em.createNamedQuery("findAllCcds", Ccd.class);

		return query.getResultList();
	}

	/**
	 * Creates a CCD record in the database
	 * @param ccd the CCD record to create
	 */
	public void createCcd(Ccd ccd) {
		
		logger.info(MessageGenerator.generateMessage("record.create", "ccd"));
		em.persist(ccd);
		
	}

	/**
	 * Updates a CCD record in the database
	 * @param ccd the CCD record to update
	 */
	public void updateCcd(Ccd ccd) {
		
		logger.info(MessageGenerator.generateMessage("record.update", "ccd"));
		em.merge(ccd);
		
	}
	
	/**
	 * Updates ccd gain channel offsets in database and in the Ccd object in the Physical Model
	 * @param offsets
	 */
	public void updateCcdGainOffsets(int[] offsets) {

		Ccd ccd = physicalModel.getInstrument().getCcd();		
		
		CcdGain ccdGain = findCcdGain(ccd.getCcdGain().getCcdGainId());
		ccdGain.setGainOffsetChannel0(offsets[0]);
		ccdGain.setGainOffsetChannel1(offsets[1]);
		em.merge(ccdGain);
		
		// transient fields 
		ccd.setChannelOffset0(offsets[0]);
		ccd.setChannelOffset1(offsets[1]);
		
		// current gain reference in ccd model object
		ccd.getCcdGain().setGainOffsetChannel0(offsets[0]);
		ccd.getCcdGain().setGainOffsetChannel1(offsets[1]);
		
	}
	
	public CcdGain findCcdGain(Long ccdGainId) {

		return em.find(CcdGain.class, ccdGainId);
	}

	

	/**
	 * Links a CCD record to an instrument record, removing any previous link to another CCD record.
	 * The instrument is found in the {@link PhysicalModel} which is initialized when PEAS-PCS is started up
	 * to be either PCS1 or PCS2, depending on what is specified in the peas.properties file.
	 * @param ccd the new CCD record to link
	 */
	public void assignCcdToInstrument(Ccd ccd) {
		// remove current assigned ccd, if it exists
		Ccd oldCcd = physicalModel.getInstrument().getCcd();
		if (oldCcd != null) {
			oldCcd.setInstrument(null);
			logger.info(MessageGenerator.generateMessage("record.update", "oldCcd"));
			em.merge(oldCcd);
		}
		
		// assign this Ccd to the instrument
		ccd.setInstrument(physicalModel.getInstrument());
		logger.info(MessageGenerator.generateMessage("record.create", "ccd"));
		em.merge(ccd);
	}
	
	
}
