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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.IntegerListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.config.business.MissingSpotsMgmt;
import org.tmt.aps.peas.config.business.SubimageDefCache;
import org.tmt.aps.peas.config.model.MissingSpotList;
import org.tmt.aps.peas.config.model.SubimageDef;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.instrument.model.SufsGroup;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;


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

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<Integer> selectedSpots;

	private List<Integer> spots;

	private List<SubimageDef> subimageDefList;

	String centroidNumbers; // for javascript svg display
	String centroidXs; // for javascript svg display
	String centroidYs; // for javascript svg display
	String missingSpots; // for javascript svg display

	private Integer spotListType;
	private MissingSpotList missingSpotList;
	private List<PupilMaskType> pupilMaskTypeList;
	private PupilMaskType pupilMaskType;
	private SufsGroup sufsGroup;
	private List<SufsGroup> sufsGroupList;

	@PostConstruct
	public void init() {

		try {

			pupilMaskTypeList = cameraDefMgmt.findAllPupilMaskTypes();
			pupilMaskType = pupilMaskTypeList.get(0);
			spotListType = 1;
			sufsGroupList = cameraDefMgmt.findSufsGroups();
						

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	private void updateCentroidDisplay() {

		initMissingSpots();
		
		logger.debug("Number of Spots = " + pupilMaskType.getNumSpots());

		// read in current values from the cache
		subimageDefList = subimageDefCache.getSubimageDefList(pupilMaskType.getPupilMaskTypeId()).getListOfSubimageDefs();
		

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

	public boolean getRenderSufsGroup() {
		return pupilMaskType.isPupilMaskTypeSufs();
	}

	public void listViewChangeListener() {
		// values have changed, refresh display values
		try {
			if (pupilMaskType.isPupilMaskTypeSufs()) {
				logger.debug("SUFS Group = " + sufsGroup);
				missingSpotList = missingSpotsMgmt.findMissingSpotList(spotListType, pupilMaskType.getPupilMaskTypeId(), sufsGroup.getGroupNumber());
			} else {
				missingSpotList = missingSpotsMgmt.findMissingSpotList(spotListType, pupilMaskType.getPupilMaskTypeId());
			}
			logger.debug("missing spot list encoded = " + missingSpotList.getMissingSpotListEncoded());
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			missingSpotList = new MissingSpotList();	
		}
		updateCentroidDisplay();

	}

	public void spotChangeListener(AjaxBehaviorEvent event) {

		refreshMissingSpots();
	}

	public String doViewMissingSpots() {
		try {
			if (pupilMaskType.isPupilMaskTypeSufs()) {
				missingSpotList = missingSpotsMgmt.findMissingSpotList(spotListType, pupilMaskType.getPupilMaskTypeId(), sufsGroup.getGroupNumber());
			} else {
				missingSpotList = missingSpotsMgmt.findMissingSpotList(spotListType, pupilMaskType.getPupilMaskTypeId());
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

	public void doSave() {
		try {
			refreshMissingSpots();
			missingSpotList.setMissingSpotListEncoded(missingSpots);
			
			if (missingSpotList.isNewRecord()) {
				missingSpotsMgmt.createMissingSpotList(missingSpotList);
			} else {
				missingSpotsMgmt.updateMissingSpotList(missingSpotList);
				
				// TODO: also update the cache
				
			}
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}

}
