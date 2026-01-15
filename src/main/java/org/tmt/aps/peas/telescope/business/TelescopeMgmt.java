/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.telescope.business;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * Session EJB managing telescope record database queries and manages current status.
 * @author smichaels
 *
 */
@Stateless
public class TelescopeMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	
	@EJB
	DcsMgmt dcsMgmt;
	@EJB
	AcsMgmt acsMgmt;
	@EJB
	PhysicalModel physicalModel;



	/**
	 * Returns a telescope record from the database given its id
	 * @param telescopeId the telescope id to search
	 * @return the telescope record
	 */
	public Telescope findTelescope(long telescopeId) {
		TypedQuery<Telescope> query = em.createNamedQuery("findTelescope", Telescope.class);
		query.setParameter("telescopeId", telescopeId);

		return query.getSingleResult();
	}

	/**
	 * Sends query commands to DCS using {@link DcsMgmt} and ACS using {@link AcsMgmt} and updates the telescope's state in the {@link PhysicalModel}
	 */
	public void refreshStatus() throws Exception {

		Telescope telescope = physicalModel.getTelescope();
		
		double[] secondaryActs = dcsMgmt.querySecondary();
		double[] telPosArray = dcsMgmt.queryTelescopePosition();
		
		telescope.setM2Position(secondaryActs);
		telescope.setTelPosition(new FloatPoint((float)telPosArray[0], (float)telPosArray[1]));

		
		double mirrorTemp = acsMgmt.queryMirrorTemp();
		telescope.setMirrorTemp(mirrorTemp);

	}


}
