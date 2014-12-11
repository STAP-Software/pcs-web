/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.telescope.business;

import javax.ejb.AsyncResult;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.telescope.model.Telescope;

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



	public Telescope findTelescope(long telescopeId) {
		TypedQuery<Telescope> query = em.createNamedQuery("findTelescope", Telescope.class);
		query.setParameter("telescopeId", telescopeId);

		return query.getSingleResult();
	}

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
