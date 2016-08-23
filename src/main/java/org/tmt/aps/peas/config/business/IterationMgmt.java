/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.config.model.IterationListConfigOption;
import org.tmt.aps.peas.config.model.IterationValueList;
import org.tmt.aps.peas.config.model.ProcedureIterationDef;

/**
 * Session EJB managing iteration configuration.
 * @author smichaels
 */
@Stateless
public class IterationMgmt {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;
	

	/**
	 * Queries the database for iteration entity definitions, given the procedure type
	 * @param procedureTypeId the procedure type, e.g. passive tilt, fine screen, etc
	 * @return a list of descriptions of iteration entities
	 */
	public List<ProcedureIterationDef> findProcedureIterationDefs() {
		TypedQuery<ProcedureIterationDef> query = em.createNamedQuery("findIterationDefs", ProcedureIterationDef.class);
		
		return query.getResultList();	
	}
	
	/**
	 * Queries the database for an option list of iteration lists, given the procedure type
	 * @param procedureTypeId the procedure type, e.g. passive tilt, fine screen, etc
	 * @return the option list
	 */
	public List<IterationListConfigOption> findIterationListConfigOptions(Long procedureTypeId) {
		TypedQuery<IterationListConfigOption> query = em.createNamedQuery("findIterationListConfigOptions", IterationListConfigOption.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		return query.getResultList();	
	}	
	
	


}
