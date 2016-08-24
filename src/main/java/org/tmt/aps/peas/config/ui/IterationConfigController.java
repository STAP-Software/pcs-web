/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.config.business.IterationEntityCache;
import org.tmt.aps.peas.config.model.IterableEntity;
import org.tmt.aps.peas.config.model.IterationListConfigOption;
import org.tmt.aps.peas.config.model.IterationValue;
import org.tmt.aps.peas.config.model.IterationValueList;
import org.tmt.aps.peas.config.model.ProcedureIterationDef;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.ReferenceBeam;
import org.tmt.aps.peas.procedure.model.ProcedureType;
import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.session.ui.SessionController;

/**
 * JSF Controller for iteration configuration user interface 
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class IterationConfigController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	IterationEntityCache iterationEntityCache;
	@EJB
	PeasProperties peasProperties;
	@Inject
	SessionController sessionController;
	@Inject
	ProcedureController procedureController;
	@EJB
	CameraDefMgmt cameraDefMgmt;


	boolean elementEnable1;
	boolean elementEnable2;
	boolean elementEnable3;
	boolean elementEnable4;
	
	Filter filter1;
	Filter filter2;
	Filter filter3;
	Filter filter4;
	
	ReferenceBeam refBeam1;
	ReferenceBeam refBeam2;
	ReferenceBeam refBeam3;
	ReferenceBeam refBeam4;
	
	List<Filter> filterList;
	List<ReferenceBeam> refBeamList;

	Instrument instrument;

	@PostConstruct
	public void init() throws Exception {
		

		instrument = sessionController.getInstrument();
		
		// set up the filter list
		filterList = sessionController.getInstrument().getCamera().getFilterWheel().getOrigFilterList();
		// order by name
		Collections.sort(filterList, new BeanComparator("filterName"));

		// set up the ref beam list
		refBeamList = cameraDefMgmt.findAllRefBeams(instrument.getInstrumentId());
	}

	public boolean isElementEnable1() {
		return elementEnable1;
	}

	public void setElementEnable1(boolean elementEnable1) {
		this.elementEnable1 = elementEnable1;
	}

	public boolean isElementEnable2() {
		return elementEnable2;
	}

	public void setElementEnable2(boolean elementEnable2) {
		this.elementEnable2 = elementEnable2;
	}

	public boolean isElementEnable3() {
		return elementEnable3;
	}

	public void setElementEnable3(boolean elementEnable3) {
		this.elementEnable3 = elementEnable3;
	}

	public boolean isElementEnable4() {
		return elementEnable4;
	}

	public void setElementEnable4(boolean elementEnable4) {
		this.elementEnable4 = elementEnable4;
	}

	public Filter getFilter1() {
		return filter1;
	}

	public void setFilter1(Filter filter1) {
		this.filter1 = filter1;
	}

	public Filter getFilter2() {
		return filter2;
	}

	public void setFilter2(Filter filter2) {
		this.filter2 = filter2;
	}

	public Filter getFilter3() {
		return filter3;
	}

	public void setFilter3(Filter filter3) {
		this.filter3 = filter3;
	}

	public Filter getFilter4() {
		return filter4;
	}

	public void setFilter4(Filter filter4) {
		this.filter4 = filter4;
	}

	public ReferenceBeam getRefBeam1() {
		return refBeam1;
	}

	public void setRefBeam1(ReferenceBeam refBeam1) {
		this.refBeam1 = refBeam1;
	}

	public ReferenceBeam getRefBeam2() {
		return refBeam2;
	}

	public void setRefBeam2(ReferenceBeam refBeam2) {
		this.refBeam2 = refBeam2;
	}

	public ReferenceBeam getRefBeam3() {
		return refBeam3;
	}

	public void setRefBeam3(ReferenceBeam refBeam3) {
		this.refBeam3 = refBeam3;
	}

	public ReferenceBeam getRefBeam4() {
		return refBeam4;
	}

	public void setRefBeam4(ReferenceBeam refBeam4) {
		this.refBeam4 = refBeam4;
	}

	public List<Filter> getFilterList() {
		return filterList;
	}

	public void setFilterList(List<Filter> filterList) {
		this.filterList = filterList;
	}

	public List<ReferenceBeam> getRefBeamList() {
		return refBeamList;
	}

	public void setRefBeamList(List<ReferenceBeam> refBeamList) {
		this.refBeamList = refBeamList;
	}

	/**
	 * Action method called when 'cancel' button is clicked on iteration configuration page
	 */
	public void doCancelSaveIterationSet() throws Exception {
		
	}

	/**
	 * Action method to save the iteration configuration to the database
	 */
	public void doSaveIterationSet() {

		try {
			
			ProcedureType procedureType = procedureController.getProcedure().getProcedureType();
			
			// generate an IterationListConfigOption
			IterationListConfigOption option = new IterationListConfigOption();
			
			
			// fully populate the option
			
			option.setInstrument(instrument);
			option.setProcedureType(procedureType);

			// determine the next id in the list 
			int nextNum = iterationEntityCache.getOptionList(procedureType.getProcedureTypeId()).size();
			
			option.setIterationListConfigId(new Long(nextNum));
			option.setOptionOrder(nextNum);  // this is not needed, since we are not saving the 'option' to the database

			// the procedureIterationDefs for this procedureType
			List<ProcedureIterationDef> entityList = iterationEntityCache.getProcedureIterationDefList(procedureType.getProcedureTypeId());
			// use this to determine the order we need to define for the encoding

			
			// Now build up the iterationValueList
			List<IterationValue> iterationValues = new ArrayList<IterationValue>();
			
			// an iteration value is a single pair of filter/refbeam
			if (elementEnable1) {			
				iterationValues.add(createIterationValue(filter1, refBeam1));
			}
			if (elementEnable2) {
				iterationValues.add(createIterationValue(filter2, refBeam2));				
			}
			if (elementEnable3) {
				iterationValues.add(createIterationValue(filter3, refBeam3));				
			}
			if (elementEnable4) {
				iterationValues.add(createIterationValue(filter4, refBeam4));				
			}
			
		
			IterationValueList iterationValueList = new IterationValueList(iterationValues);
			
			option.setIterationValueList(iterationValueList);
			
			// encode the value list for potential store to the database
			String iterationValueListEncoded = iterationEntityCache.encodeList(iterationValueList);
			
			option.setIterationValueListEncoded(iterationValueListEncoded);

			// add to the iterationEntityCache option list for the current procedure type
			iterationEntityCache.addOption(procedureType.getProcedureTypeId(), option);			

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}
	}
	
	private IterationValue createIterationValue(IterableEntity... entityArgs) {
		List<IterableEntity> entities = new ArrayList<IterableEntity>();
		for (IterableEntity entity : entityArgs) {
			entities.add(entity);
		}
		IterationValue iterationValue = new IterationValue(entities);
		return iterationValue;
	}

	/**
	 * Action method to view iteration configuration values
	 * @return JSF page to render
	 */
	public void doResetIterationSet() {
		
				
		elementEnable1 = false;
		elementEnable2 = false;
		elementEnable3 = false;
		elementEnable4 = false;
		
		filter1 = filterList.get(0);
		filter2 = filterList.get(0);
		filter3 = filterList.get(0);
		filter4 = filterList.get(0);

		refBeam1 = refBeamList.get(0);
		refBeam2 = refBeamList.get(0);
		refBeam3 = refBeamList.get(0);
		refBeam4 = refBeamList.get(0);	

	}
	
	



}
