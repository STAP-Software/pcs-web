/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.business;

import java.util.ArrayList;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.visualization.model.ProcTypeVisualizationDisplay;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;

/**
 * Session EJB managing visualization display metadata
 * @author smichaels
 *
 */
@Stateless
public class VisualizationDisplayMgmt {

	Logger logger = Logger.getLogger(this.getClass());
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Queries the database for the list of visualization displays supported by the passed procedure type
	 * @param procedureTypeId the procedure type id of the procedure type
	 * @return the list of visualization display types supported
	 */
	public List<VisualizationDisplay> findVisualizationDisplays(Long procedureTypeId) {
		TypedQuery<ProcTypeVisualizationDisplay> query = em.createNamedQuery("findProcTypeVisualizationDisplays", ProcTypeVisualizationDisplay.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		List<ProcTypeVisualizationDisplay> ptypeVisDisplayList = query.getResultList();
		
		List<VisualizationDisplay> visualizationDisplayList = new ArrayList<VisualizationDisplay>();
		for (ProcTypeVisualizationDisplay ptypeVisDisplay : ptypeVisDisplayList) {
			visualizationDisplayList.add(ptypeVisDisplay.getVisualizationDisplay());
		}
		
		return visualizationDisplayList;
	}
	
}
