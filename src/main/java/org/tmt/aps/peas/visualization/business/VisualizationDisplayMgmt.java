/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.procedure.model.ProcedureOutput;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.visualization.model.ProcTypeVisualizationDisplay;
import org.tmt.aps.peas.visualization.model.VisualizationDisplay;

@Stateless
public class VisualizationDisplayMgmt {

	Logger logger = Logger.getLogger(this.getClass());
	
	@PersistenceContext
	private EntityManager em;
	
	
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

	public ProcedureType findProcedureType(Long procedureTypeId) {
		TypedQuery<ProcedureType> query = em.createNamedQuery("findProcedureType", ProcedureType.class);
		query.setParameter("procedureTypeId", procedureTypeId);
		query.setMaxResults(1);
		
		ProcedureType procedureType = query.getSingleResult();
			
		return procedureType;
	}
	
	public ProcedureConfig findDefaultProcedureConfig(Long telescopeId, Long instrumentId, Long procedureTypeId) {
		TypedQuery<ProcedureConfig> query = em.createNamedQuery("findDefaultProcedureConfig", ProcedureConfig.class);
		query.setParameter("telescopeId", telescopeId);
		query.setParameter("instrumentId", instrumentId);
		query.setParameter("procedureTypeId", procedureTypeId);
		
		query.setMaxResults(1);
		
		return query.getSingleResult();
	}
	
}
