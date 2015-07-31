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
import org.tmt.aps.peas.config.model.AutoCenterTelConfigDefaults;
import org.tmt.aps.peas.config.model.AutoRefMapConfigDefaults;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfigDefaults;
import org.tmt.aps.peas.config.model.FIConfigDefaults;
import org.tmt.aps.peas.config.model.FindCentConfigDefaults;
import org.tmt.aps.peas.config.model.GlobalConfigDefaults;
import org.tmt.aps.peas.config.model.PeripheralSpotList;
import org.tmt.aps.peas.config.model.PupilRegErrorConfigDefaults;
import org.tmt.aps.peas.config.model.RefMapConfigDefaults;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;

@Stateless
public class GlobalConfigMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	
	public GlobalConfigDefaults findDefaultConfig(Long telescopeId, Long instrumentId) {
		TypedQuery<GlobalConfigDefaults> query = em.createNamedQuery("findDefaultConfig", GlobalConfigDefaults.class);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("instrumentId", instrumentId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
	public void saveDefaultConfig(GlobalConfigDefaults globalConfigDefaults) {
		
		logger.info(MessageGenerator.generateMessage("record.update", "globalConfigDefaults"));
		em.merge(globalConfigDefaults);
	}
	
	
	

	public FIConfigDefaults findFIConfigDefaults(Long instrumentId, Long pupilMaskTypeId, int lightSource) {
		TypedQuery<FIConfigDefaults> query = em.createNamedQuery("findByMaskTypeAndInstrument", FIConfigDefaults.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("lightSource", lightSource);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();

	}

	public FindCentConfigDefaults findFindCentConfig(Long pupilMaskTypeId) {
		
		TypedQuery<FindCentConfigDefaults> query = em.createNamedQuery("findByMaskType", FindCentConfigDefaults.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}
	
	public PupilRegErrorConfigDefaults findPupilRegErrorConfig(Long pupilMaskTypeId) {
		
		TypedQuery<PupilRegErrorConfigDefaults> query = em.createNamedQuery("pupilRegErrorConfig.findByMaskType", PupilRegErrorConfigDefaults.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}
	
	public CentroidOffsetsConfigDefaults findCentroidOffsetsConfig(Long procedureTypeId) {
		
		TypedQuery<CentroidOffsetsConfigDefaults> query = em.createNamedQuery("findByProcedureType", CentroidOffsetsConfigDefaults.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}
	
	
	public RefMapConfigDefaults findRefMapConfigDefaults(Long instrumentId, Long pupilMaskTypeId, Long filterTypeId) {
		TypedQuery<RefMapConfigDefaults> query = em.createNamedQuery("findByMaskTypeAndFilterType", RefMapConfigDefaults.class);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		query.setParameter("filterTypeId", filterTypeId);
		
		query.setMaxResults(1);
		
		RefMapConfigDefaults refMapConfigDefaults = query.getSingleResult();
		
		// get the reference beam by ref beam number
		TypedQuery<ReferenceBeam> query2 = em.createNamedQuery("findByNumber", ReferenceBeam.class);
		query2.setParameter("refBeamNum", refMapConfigDefaults.getReferenceBeamNum());
		
		query2.setMaxResults(1);
		
		ReferenceBeam referenceBeam = query2.getSingleResult();
	
		refMapConfigDefaults.setReferenceBeam(referenceBeam);
		
		return refMapConfigDefaults;

	}

	public AutoRefMapConfigDefaults findAutoRefMapConfig(Long procedureTypeId) {
		TypedQuery<AutoRefMapConfigDefaults> query = em.createNamedQuery("findAutoByProcedureType", AutoRefMapConfigDefaults.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}
	
	public AutoCenterTelConfigDefaults findAutoCenterTelConfig(Long procedureTypeId) {
		TypedQuery<AutoCenterTelConfigDefaults> query = em.createNamedQuery("findAutoCenterTelConfigDefaults", AutoCenterTelConfigDefaults.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}
	

}
