/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.config.business.ConstantsCache;
import org.tmt.aps.peas.config.business.MissingSpotsMgmt;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.MissingSpotList;
import org.tmt.aps.peas.config.model.SubimageDef;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.SufsGroup;
import org.tmt.aps.peas.session.ui.SessionController;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;
import org.tmt.aps.peas.telescope.model.Telescope;

/**
 * JSF Controller for missing spots configuration user interface
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class MissingSpotsController implements Serializable {

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
	ConstantsCache constantsCache;
	@Inject
	SessionController sessionController;
	

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	
	Telescope telescope;

	private List<Integer> selectedSpots;

	private List<Integer> spots;

	private List<SubimageDef> subimageDefList;

	String centroidNumbers; // for javascript svg display
	String centroidXs; // for javascript svg display
	String centroidYs; // for javascript svg display
	String missingSpots; // for javascript svg display
	String groupSegmentNumbers;
	
	private Integer spotListType;
	private MissingSpotList missingSpotList;
	private List<PupilMaskType> pupilMaskTypeList;
	private PupilMaskType pupilMaskType;
	private SufsGroup sufsGroup;
	private List<SufsGroup> sufsGroupList;
	private boolean showSegmentNumbers;


	@PostConstruct
	public void init() {

		try {
			
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");
			telescope = telescopeMgmt.findTelescope(new Long(telescopeIdStr));


			pupilMaskTypeList = cameraDefMgmt.findAllPupilMaskTypes();
			pupilMaskType = pupilMaskTypeList.get(0);
			spotListType = 1;
			sufsGroupList = cameraDefMgmt.findSufsGroups();
			sufsGroup = sufsGroupList.get(0);
						

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	private void updateCentroidDisplay() {

		initMissingSpots();
		
		logger.debug("Number of Spots = " + pupilMaskType.getNumSpots());

		// read in current values from the cache
		if (pupilMaskType.isPupilMaskTypeSufs()) {
			subimageDefList = subimageDefCache.getSubimageDefList(pupilMaskType.getPupilMaskTypeId(), sufsGroup.getGroupNumber()).getListOfSubimageDefs();			
		} else {
			subimageDefList = subimageDefCache.getSubimageDefList(pupilMaskType.getPupilMaskTypeId()).getListOfSubimageDefs();
		}

		// generate centroid numbers, x and y positions
		StringBuffer numBuf = new StringBuffer();
		StringBuffer xBuf = new StringBuffer();
		StringBuffer yBuf = new StringBuffer();
		for (SubimageDef subimageDef : subimageDefList) {
			numBuf.append(subimageDef.getSubimageNumber() + ",");
			xBuf.append(subimageDef.getCentroid().x + ",");
			yBuf.append(subimageDef.getCentroid().y + ",");
		}
		numBuf.deleteCharAt(numBuf.length() - 1);
		xBuf.deleteCharAt(xBuf.length() - 1);
		yBuf.deleteCharAt(yBuf.length() - 1);
		centroidNumbers = numBuf.toString();
		centroidXs = xBuf.toString();
		centroidYs = yBuf.toString();
		logger.debug("centroidNumbers = " + centroidNumbers);

	}

	private void initMissingSpots() {
		
		spots = new ArrayList<Integer>();
		selectedSpots = new ArrayList<Integer>();
		for (int i = 1; i <= pupilMaskType.getNumSpots(); i++) {
			spots.add(i);
			selectedSpots.add(i);
		}
		
		// display takes the list as a comma sep list, which is our encoding
		missingSpots = missingSpotList.getMissingSpotListEncoded();
		List<Integer> missingSpotListDecoded = IntegerListEncoder.decodeList(missingSpots);
		for (Integer spot : missingSpotListDecoded) {
			// remove all the missing spots from the selected ones
			selectedSpots.remove(selectedSpots.indexOf(spot));
		}
	}
	
	private void refreshMissingSpots() {
		
		List<Integer> missingSpotsInt = new ArrayList<Integer>();
		for (int i = 1; i <= pupilMaskType.getNumSpots(); i++) {
			missingSpotsInt.add(i);
		}
		
		// removeAll should work, but it doesn't!!
		//missingSpotsInt.removeAll(selectedSpots);
		
		for (Iterator<Integer> it = missingSpotsInt.iterator(); it.hasNext(); ) {
			Integer value = it.next();
			for (Integer candidate : selectedSpots) {
				if (candidate.intValue() == value.intValue()) {
					it.remove();
					break;
				}
			}
		}
		
		// display takes the list as a comma sep list, which is our encoding
		missingSpots = IntegerListEncoder.encodeList(missingSpotsInt);

	}

	public List<Integer> getSelectedSpots() {
		return selectedSpots;
	}

	public void setSelectedSpots(List<Integer> selectedSpots) {
		this.selectedSpots = selectedSpots;
	}

	public List<Integer> getSpots() {
		return spots;
	}

	public String getCentroidNumbers() {
		return centroidNumbers;
	}

	public void setCentroidNumbers(String centroidNumbers) {
		this.centroidNumbers = centroidNumbers;
	}

	public String getCentroidXs() {
		return centroidXs;
	}

	public void setCentroidXs(String centroidXs) {
		this.centroidXs = centroidXs;
	}

	public String getCentroidYs() {
		return centroidYs;
	}

	public void setCentroidYs(String centroidYs) {
		this.centroidYs = centroidYs;
	}

	public String getMissingSpots() {
		return missingSpots;
	}

	public void setMissingSpots(String missingSpots) {
		this.missingSpots = missingSpots;
	}

	public String getGroupSegmentNumbers() {
		return groupSegmentNumbers;
	}

	public void setGroupSegmentNumbers(String groupSegmentNumbers) {
		this.groupSegmentNumbers = groupSegmentNumbers;
	}

	public Integer getSpotListType() {
		return spotListType;
	}

	public void setSpotListType(Integer spotListType) {
		this.spotListType = spotListType;
	}

	public MissingSpotList getMissingSpotList() {
		return missingSpotList;
	}

	public void setMissingSpotList(MissingSpotList missingSpotList) {
		this.missingSpotList = missingSpotList;
	}

	public List<PupilMaskType> getPupilMaskTypeList() {
		return pupilMaskTypeList;
	}

	public void setPupilMaskTypeList(List<PupilMaskType> pupilMaskTypeList) {
		this.pupilMaskTypeList = pupilMaskTypeList;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}

	public SufsGroup getSufsGroup() {
		return sufsGroup;
	}

	public void setSufsGroup(SufsGroup sufsGroup) {
		this.sufsGroup = sufsGroup;
	}

	public List<SufsGroup> getSufsGroupList() {
		return sufsGroupList;
	}

	public void setSufsGroupList(List<SufsGroup> sufsGroupList) {
		this.sufsGroupList = sufsGroupList;
	}

	public boolean isShowSegmentNumbers() {
		return showSegmentNumbers;
	}

	public void setShowSegmentNumbers(boolean showSegmentNumbers) {
		this.showSegmentNumbers = showSegmentNumbers;
	}

	public boolean getRenderSufsGroup() {
		return pupilMaskType.isPupilMaskTypeSufs();
	}

	/**
	 * Listener called when the mask or missing spot type is changed.
	 * Loads values from database for new selected mask or missing spot type.
	 */
	public void listViewChangeListener() {
		// values have changed, refresh display values
		try {
			if (pupilMaskType.isPupilMaskTypeSufs()) {
				logger.debug("SUFS Group = " + sufsGroup);
				missingSpotList = missingSpotsMgmt.findMissingSpotList(spotListType, telescope.getTelescopeId(), pupilMaskType.getPupilMaskTypeId(), sufsGroup.getGroupNumber());

				// find the spot numbers for this sufsGroup
				groupSegmentNumbers = constantsCache.getSufsConstants().getSufsGroupToMirrorDisplayString(sufsGroup.getGroupNumber()-1);
			
			
			} else {
				missingSpotList = missingSpotsMgmt.findMissingSpotList(spotListType, telescope.getTelescopeId(), pupilMaskType.getPupilMaskTypeId());
			}
			logger.debug("missing spot list encoded = " + missingSpotList.getMissingSpotListEncoded());
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			missingSpotList = new MissingSpotList();	
		}
		updateCentroidDisplay();

	}

	/**
	 * Listener called when a missing spot in the list is changed.  Refreshes the missing spot list.
	 */
	public void spotChangeListener(AjaxBehaviorEvent event) {

		refreshMissingSpots();
	}

	/**
	 * Action method to view missing spots.  Updates the display with missing spots.
	 * @return JSF page to render
	 */
	public String doViewMissingSpots() {
		try {
			if (pupilMaskType.isPupilMaskTypeSufs()) {
				missingSpotList = missingSpotsMgmt.findMissingSpotList(spotListType, telescope.getTelescopeId(), pupilMaskType.getPupilMaskTypeId(), sufsGroup.getGroupNumber());
			} else {
				missingSpotList = missingSpotsMgmt.findMissingSpotList(spotListType, telescope.getTelescopeId(), pupilMaskType.getPupilMaskTypeId());
			}
			updateCentroidDisplay();
			
			breadcrumbMenuBean.addFirstItem("Missing Spots Configuration", "/modules/config/missingSpots.xhtml");

			//RequestContext.getCurrentInstance().execute("runDrawMissingSpots()");
			
			return "/modules/config/missingSpots.xhtml?faces-redirect=true";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}

	/**
	 * Action method called when user clicks 'Save'.  Updates the list of missing spots in the database.
	 */
	public void doSave() {
		try {
			refreshMissingSpots();
			missingSpotList.setMissingSpotListEncoded(missingSpots);
			
			if (missingSpotList.isNewRecord()) {
				missingSpotsMgmt.createMissingSpotList(missingSpotList);
			} else {
				missingSpotsMgmt.updateMissingSpotList(missingSpotList);
				
				// update the cache
				subimageDefCache.refreshCache();
				
			}
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

}
