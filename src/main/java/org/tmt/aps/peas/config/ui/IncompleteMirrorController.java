/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.MissingSpotsMgmt;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;
import org.tmt.aps.peas.visualization.ui.VisualizationController;

/**
 * JSF Controller for incomplete mirror configuration user interface
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class IncompleteMirrorController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	MissingSpotsMgmt missingSpotsMgmt;
	@EJB
	SubimageDefCache subimageDefCache;
	@EJB
	CameraDefMgmt cameraDefMgmt;
	@EJB
	PeasProperties peasProperties;
	@EJB
	TelescopeMgmt telescopeMgmt;
	@EJB
	ComputationLibraryImpl computationLibraryImpl;
	@EJB
	ConstantsCache constantsCache;
	@Inject
	SessionController sessionController;
	@Inject
	VisualizationController visualizationController;
	

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	
	Telescope telescope;

	private List<Integer> mirrorSegments;

	private List<Integer> mirrorSegmentsSaved;

	String mirrors; // for javascript display

	

	@PostConstruct
	public void init() {

		try {
			
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			telescope = telescopeMgmt.findTelescope(new Long(telescopeIdStr));


			// TODO: do we want to load up the incomplete mirror config from the database?
			
			// for now, these are all defaulted to true
			mirrorSegments = new ArrayList<Integer>();
			
			for (int i=0; i<36; i++) {
				mirrorSegments.add(1);
			}
									
			mirrors = IntegerListEncoder.encodeList(mirrorSegments);
			
			// the saved version
			mirrorSegmentsSaved = new ArrayList<Integer>(mirrorSegments);
			
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	
	public String getMirrors() {
		return mirrors;
	}

	public void setMirrors(String mirrors) {
		this.mirrors = mirrors;
	}


	/**
	 * Action method to view missing spots.  Updates the display with missing spots.
	 * @return JSF page to render
	 */
	public String doViewIncompleteMirror() {
		try {
		
			breadcrumbMenuBean.addFirstItem("Incomplete Mirror Configuration", "/modules/config/incompleteMirror.xhtml");
			
			return "/modules/config/incompleteMirror.xhtml?faces-redirect=true";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}

	public void doHandleSelect() {
		
		String xStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_x");
		String yStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_y");

		int x = (new Double(xStr)).intValue();
		int y = (new Double(yStr)).intValue();

		FloatPoint subapp = new FloatPoint(x/0.61f, y/0.61f); // unscale
				
		List<FloatPoint> segCenterList = subimageDefCache.getSubimageDefList(PupilMaskType.PUPIL_MASK_TYPE_ID_36).getInteriorSubimageDefListCentroids();
		
		
		for (int i=0; i<36; i++) {
			
			FloatPoint segCenter = segCenterList.get(i);
			
			// find out which segment was chosen
			if (computationLibraryImpl.doesSubapLieInSeg(subapp, segCenter, 77.0f)) {
				mirrorSegments.set(i, mirrorSegments.get(i) == 0 ? 1 : 0);
			}
		}
								
		mirrors = IntegerListEncoder.encodeList(mirrorSegments);

	}
	
	
	/**
	 * Action method called when user clicks 'Save'.  Updates the list of mirrors and the SubimageDefCache.
	 */
	public void doSave() {
		try {
			
			// TODO: update the SubimageDefCache with new values based on the new incomplete mirror configuration
			
			
			
			mirrorSegmentsSaved = new ArrayList<Integer>(mirrorSegments);
			
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}
	
	/**
	 * Action method called when user clicks 'Reset'.  Updates the list of mirrors.
	 */
	public void doReset() {			
		
		mirrorSegments = new ArrayList<Integer>(mirrorSegmentsSaved);
		
		mirrors = IntegerListEncoder.encodeList(mirrorSegments);

	}

}
