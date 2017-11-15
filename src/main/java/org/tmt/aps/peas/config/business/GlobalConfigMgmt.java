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
import org.tmt.aps.peas.config.model.CalcM2M1ConfigDefaults;
import org.tmt.aps.peas.config.model.CentroidOffsetsConfigDefaults;
import org.tmt.aps.peas.config.model.FIConfigDefaults;
import org.tmt.aps.peas.config.model.FindCentConfigDefaults;
import org.tmt.aps.peas.config.model.GlobalConfigDefaults;
import org.tmt.aps.peas.config.model.NbFilterSeqConfigDefaults;
import org.tmt.aps.peas.config.model.PupilRegErrorConfigDefaults;
import org.tmt.aps.peas.config.model.RefMapConfigDefaults;
import org.tmt.aps.peas.config.model.SufsCoarseOffsetsConfigDefaults;
import org.tmt.aps.peas.config.model.SufsRefMapConfigDefaults;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;

/**
 * Session EJB managing global configuration and computation configuration database queries and updates.
 * @author smichaels
 */
@Stateless
public class GlobalConfigMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Queries the database for global configuration default values for a given telescope and PCS instrument
	 * @param telescopeId Keck 1, Keck 2
	 * @param instrumentId PCS1, PCS2
	 * @return the current global configuration default values
	 */
	public GlobalConfigDefaults findDefaultConfig(Long telescopeId, Long instrumentId) {
		TypedQuery<GlobalConfigDefaults> query = em.createNamedQuery("findDefaultConfig", GlobalConfigDefaults.class);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("instrumentId", instrumentId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
	/**
	 * Updates the database with new global configuration default values
	 * @param globalConfigDefaults the new global configuration default values
	 */
	public void saveDefaultConfig(GlobalConfigDefaults globalConfigDefaults) {
		
		logger.info(MessageGenerator.generateMessage("record.update", "globalConfigDefaults"));
		em.merge(globalConfigDefaults);
	}
	
	/**
	 * Queries the database for the find and identify computation configuration default values, for a given instrument, pupilMaskType and light source 
	 * @param instrumentId PCS1 or PCS2
	 * @param pupilMaskTypeId the pupil mask type, such as phasing or fine screen, etc
	 * @param lightSource star or reference beam
	 * @return the find and identify computation configuration default values
	 */
	public FIConfigDefaults findFIConfigDefaults(Long instrumentId, Long pupilMaskTypeId, int lightSource, Long ccdTypeId) {
		TypedQuery<FIConfigDefaults> query = em.createNamedQuery("findByMaskTypeAndInstrument", FIConfigDefaults.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("lightSource", lightSource);
		query.setParameter("ccdTypeId", ccdTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();

	}

	/**
	 * Queries the database for the find centroid computation configuration default values, for a given pupil mask type, filter type and spot type
	 * @param pupilMaskTypeId the pupil mask type, such as phasing or fine screen, etc
	 * @param filterTypeId the filter type e.g. 611, 891, etc
	 * @param spotType interior or peripheral spot
	 * @return the find centroid computation configuration default values
	 */
	public FindCentConfigDefaults findFindCentConfig(Long pupilMaskTypeId, Long filterTypeId, int spotType, Long ccdTypeId) {
		
		TypedQuery<FindCentConfigDefaults> query = em.createNamedQuery("findByMaskType", FindCentConfigDefaults.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		query.setParameter("filterTypeId", filterTypeId);
		query.setParameter("spotType", spotType);
		query.setParameter("ccdTypeId", ccdTypeId);
				
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
	/**
	 * Queries the database for Pupil Registration Error computation configuration default values, given the pupil mask type.
	 * @param pupilMaskTypeId the pupil mask type, such as phasing or fine screen, etc
	 * @return Pupil Registration Error computation configuration default values
	 */
	public PupilRegErrorConfigDefaults findPupilRegErrorConfig(Long pupilMaskTypeId) {
		
		TypedQuery<PupilRegErrorConfigDefaults> query = em.createNamedQuery("pupilRegErrorConfig.findByMaskType", PupilRegErrorConfigDefaults.class);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}
	
	/**
	 * Queries the database for M2/M1 computation default values, given the procedure type
	 * @param procedureTypeId fine screen, etc
	 * @return the M2/M1 computation default values
	 */
	public CalcM2M1ConfigDefaults findCalcM2M1Config(Long procedureTypeId) {
		
		TypedQuery<CalcM2M1ConfigDefaults> query = em.createNamedQuery("calcM2M1Config.findByProcedureType", CalcM2M1ConfigDefaults.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}

	
	/**
	 * Queries the database for centroid offsets computation default values, given the procedure type
	 * @param procedureTypeId fine screen, etc
	 * @return the centroid offsets computation default values
	 */
	public CentroidOffsetsConfigDefaults findCentroidOffsetsConfig(Long procedureTypeId, Long ccdTypeId) {
		
		TypedQuery<CentroidOffsetsConfigDefaults> query = em.createNamedQuery("findByProcedureType", CentroidOffsetsConfigDefaults.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		query.setParameter("ccdTypeId", ccdTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}
	
	/**
	 * Queries the database for the reference map configuration default values, for a given instrument, pupil mask type, and filter type
	 * @param pupilMaskTypeId the pupil mask type, such as phasing or fine screen, etc
	 * @param filterTypeId the filter type e.g. 611, 891, etc
	 * @param instrumentId PCS1 or PCS2
	 * @return the reference map configuration default values
	 */	
	public RefMapConfigDefaults findRefMapConfigDefaults(Long instrumentId, Long pupilMaskTypeId, Long filterTypeId, Long ccdTypeId) {
		TypedQuery<RefMapConfigDefaults> query = em.createNamedQuery("findByMaskTypeAndFilterType", RefMapConfigDefaults.class);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("pupilMaskTypeId", pupilMaskTypeId);
		query.setParameter("filterTypeId", filterTypeId);
		query.setParameter("ccdTypeId", ccdTypeId);
		
		query.setMaxResults(1);
		
		RefMapConfigDefaults refMapConfigDefaults = query.getSingleResult();
		
		// get the reference beam by ref beam number
		TypedQuery<ReferenceBeam> query2 = em.createNamedQuery("findByNumberAndInstrument", ReferenceBeam.class);
		query2.setParameter("refBeamNum", refMapConfigDefaults.getReferenceBeamNum());
		query2.setParameter("instrumentId", instrumentId);
		
		query2.setMaxResults(1);
		
		ReferenceBeam referenceBeam = query2.getSingleResult();
	
		refMapConfigDefaults.setReferenceBeam(referenceBeam);
		
		return refMapConfigDefaults;

	}
	
	/**
	 * Queries the database for the reference map configuration default values, for a given instrument, pupil mask type, and filter type
	 * @param pupilMaskTypeId the pupil mask type, such as phasing or fine screen, etc
	 * @param filterTypeId the filter type e.g. 611, 891, etc
	 * @param instrumentId PCS1 or PCS2
	 * @return the reference map configuration default values
	 */	
	public SufsRefMapConfigDefaults findSufsRefMapConfigDefaults(Long instrumentId, Long ccdTypeId, int referenceBeamNum) {
		TypedQuery<SufsRefMapConfigDefaults> query = em.createNamedQuery("findByRefBeamNum", SufsRefMapConfigDefaults.class);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("ccdTypeId", ccdTypeId);
		query.setParameter("referenceBeamNum", referenceBeamNum);
		
		query.setMaxResults(1);
		
		SufsRefMapConfigDefaults sufsRefMapConfigDefaults = query.getSingleResult();
		
		
		return sufsRefMapConfigDefaults;

	}
	
	/**
	 * Queries the database for the reference beam that matches the passed reference beam number
	 * @param refBeamNum the reference beam number
	 * @return the matching reference beam
	 */
	public ReferenceBeam findReferenceBeamByNumber(int refBeamNum) {
		
		// get the reference beam by ref beam number
		TypedQuery<ReferenceBeam> query2 = em.createNamedQuery("findByNumber", ReferenceBeam.class);
		query2.setParameter("refBeamNum", refBeamNum);
		
		query2.setMaxResults(1);
		
		ReferenceBeam referenceBeam = query2.getSingleResult();
		
		return referenceBeam;
	}

	/**
	 * Queries database for automatic reference beam taking criteria, given the procedure type
	 * @param procedureTypeId the procedure type, e.g. passive tilt, fine screen, etc.
	 * @return the automatic reference beam taking criteria
	 */
	public AutoRefMapConfigDefaults findAutoRefMapConfig(Long procedureTypeId, Long ccdTypeId) {
		TypedQuery<AutoRefMapConfigDefaults> query = em.createNamedQuery("findAutoByProcedureType", AutoRefMapConfigDefaults.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		query.setParameter("ccdTypeId", ccdTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}
	
	/**
	 * Queries the database for automatic telescope centering criteria, given the procedure type
	 * @param procedureTypeId the procedure type, e.g. passive tilt, fine screen, etc
	 * @return the automatic telecope centering criteria
	 */
	public AutoCenterTelConfigDefaults findAutoCenterTelConfig(Long procedureTypeId) {
		TypedQuery<AutoCenterTelConfigDefaults> query = em.createNamedQuery("findAutoCenterTelConfigDefaults", AutoCenterTelConfigDefaults.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}

	/**
	 * Queries the database for SUFS coarse mirror offsets default values, given the PCS instrument and SUFS group
	 * @param instrumentId PCS1 or PCS2
	 * @param sufsGroupId the SUFS group to steer to
	 * @return the SUFS coarse mirror offset default values to steer coarse mirror to the SUFS group
	 */
	public SufsCoarseOffsetsConfigDefaults findSufsCoarseOffsetsConfig(Long instrumentId, Long sufsGroupId) {
		TypedQuery<SufsCoarseOffsetsConfigDefaults> query = em.createNamedQuery("findSufsCoarseOffsetsConfig", SufsCoarseOffsetsConfigDefaults.class);
		
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("sufsGroupId", sufsGroupId);
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}

	/**
	 * Updates the SUFS coarse offsets 'last used' values for the instrument and sufs group
	 * @param instrumentId PCS1 or PCS2	
	 * @param sufsGroupId sufsGroupId the SUFS group to steer to
	 * @param coarseOffsetX the new coarse offset x value
	 * @param coarseOffsetY the new coarse offset y value
	 */
	public void updateSufsCoarseOffsetsCurrent(Long instrumentId, int sufsGroupId, int coarseOffsetX, int coarseOffsetY) {
		
		SufsCoarseOffsetsConfigDefaults sufsCoarseOffsetsConfigDefaults = findSufsCoarseOffsetsConfig(instrumentId, new Long(sufsGroupId));
		sufsCoarseOffsetsConfigDefaults.setCoarseMirrorOffsetCurrentX(coarseOffsetX);
		sufsCoarseOffsetsConfigDefaults.setCoarseMirrorOffsetCurrentY(coarseOffsetY);
		
		em.merge(sufsCoarseOffsetsConfigDefaults);
		
	}
	
	/**
	 * Queries the database for NarrowBand Filter Sequence analysis computation configuration default values, given the iteration config for a filter set.
	 * @param iterationListConfigId the iteration list config id of the filter set option 
	 * @return Narrow Band Filter Seq analysis computation configuration default values
	 */
	public NbFilterSeqConfigDefaults findNbFilterSeqConfig(Long iterationListConfigId) {
		
		TypedQuery<NbFilterSeqConfigDefaults> query = em.createNamedQuery("nbFilterSeqConfig.findByFilterSetOption", NbFilterSeqConfigDefaults.class);
		query.setParameter("iterationListConfigId", iterationListConfigId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();	
	}


}
