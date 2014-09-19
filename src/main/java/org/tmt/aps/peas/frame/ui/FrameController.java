/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.ui;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.extInterface.ui.CameraManualController;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.instrument.business.PhysicalModel;

@Named
@SessionScoped
public class FrameController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private CameraManualController cameraManualController;

	@EJB
	FrameMgmt frameMgmt;
	@EJB
	PhysicalModel physicalModel;

	private TreeNode sessionRoot;
	private TreeNode typeRoot;

	private TreeNode selectedNode;
	private StreamedContent graphicImage;
	private int searchRadius;
	private String centroidXs;
	private String centroidYs;
	
	private CcdFrame ccdFrame;
	private boolean allowFrameSave = false;

	Map<String, List<FitsFilename>> type2Fits;

	public TreeNode getSessionRoot() {
		return sessionRoot;
	}

	public TreeNode getTypeRoot() {
		return typeRoot;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
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

	public int getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(int searchRadius) {
		this.searchRadius = searchRadius;
	}

	public String getSelectedFitsFilename() {
		if (selectedNode == null) {
			return null;
		}
		FrameTreeElement fte = (FrameTreeElement) selectedNode.getData();
		return fte.getFileName();
	}

	public StreamedContent getGraphicImage() {
		return graphicImage;
	}

	public List<FitsFilename> getProcedureFitsFiles(String procedureTypeCd) {
		return type2Fits.get(procedureTypeCd);
	}

	public List<FitsFilename> getAllFitsFiles() {
		List<FitsFilename> allFitsFiles = new ArrayList<FitsFilename>();
		for (String key : type2Fits.keySet()) {
			allFitsFiles.addAll(type2Fits.get(key));
		}
		return allFitsFiles;
	}
	public boolean isSaveAllowed() {
		return allowFrameSave;
	}

	@PostConstruct
	public void init() {
		
		long start = System.currentTimeMillis();
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

		// dummy for session root
		sessionRoot = new DefaultTreeNode(new FrameTreeElement("Sessions", "-"), null);
		typeRoot = new DefaultTreeNode("folder", new FrameTreeElement("Frames", "-"), null);

		// search folder for fits files

		Map<Integer, Map<Date, List<FitsFilename>>> telescope2Fits = new HashMap<Integer, Map<Date, List<FitsFilename>>>();

		type2Fits = new HashMap<String, List<FitsFilename>>();

		try {

			List<FitsFilename> fitsFileList = frameMgmt.findAllFitsFiles();

			for (FitsFilename fitsFile : fitsFileList) {

				try {
					Map<Date, List<FitsFilename>> telescopeFitsMap = telescope2Fits.get(new Integer(fitsFile.getTelescope()));
					if (telescopeFitsMap == null) {
						telescopeFitsMap = new TreeMap<Date, List<FitsFilename>>();
						telescope2Fits.put(new Integer(fitsFile.getTelescope()), telescopeFitsMap);
					}

					// logger.debug("map get filename = " + fitsFile.getFileName());
					// logger.debug("map get dateString = " + fitsFile.getDate());

					List<FitsFilename> dateFitsList = telescopeFitsMap.get(fitsFile.getDate());
					if (dateFitsList == null) {
						dateFitsList = new ArrayList<FitsFilename>();
						telescopeFitsMap.put(fitsFile.getDate(), dateFitsList);
					}
					dateFitsList.add(fitsFile);

					List<FitsFilename> typeFitsList = type2Fits.get(fitsFile.getProcedureTypeCd());
					if (typeFitsList == null) {
						typeFitsList = new ArrayList<FitsFilename>();
						type2Fits.put(fitsFile.getProcedureTypeCd(), typeFitsList);
					}
					typeFitsList.add(fitsFile);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (Integer telescope : telescope2Fits.keySet()) {

				Map<Date, List<FitsFilename>> telescopeFitsMap = telescope2Fits.get(telescope);

				TreeNode telescopeNode = new DefaultTreeNode(new FrameTreeElement("Keck " + telescope, "-"), sessionRoot);

				for (Date date : telescopeFitsMap.keySet()) {
					List<FitsFilename> dateFitsList = telescopeFitsMap.get(date);
					TreeNode dateNode = new DefaultTreeNode(new FrameTreeElement(sdf.format(date), ""), telescopeNode);

					// TODO: order dateFitsList by procedure number
					Collections.sort(dateFitsList, new BeanComparator("procedureNumber"));
					for (FitsFilename fitsFile : dateFitsList) {
						TreeNode sessionNode00 = new DefaultTreeNode("picture", new FrameTreeElement(fitsFile.getProcedureNumber() + ": "
								+ fitsFile.getProcedureName() + ": " + fitsFile.getFileName(), fitsFile.getFileName()), dateNode);
					}

				}

			}

			for (String type : type2Fits.keySet()) {

				List<FitsFilename> typeFitsList = type2Fits.get(type);

				TreeNode typeNode = new DefaultTreeNode(new FrameTreeElement(type, ""), typeRoot);

				// TODO: order dateFitsList by procedure number
				Collections.sort(typeFitsList, new BeanComparator("telescope"));
				for (FitsFilename fitsFile : typeFitsList) {
					TreeNode sessionNode00 = new DefaultTreeNode("picture", new FrameTreeElement(fitsFile.getFileName(),
							fitsFile.getFileName()), typeNode);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		logger.info("Frame Tree loaded in " + (end-start) + " ms");
	}

	public void onNodeSelect(NodeSelectEvent event) {

		try {

			FrameTreeElement selectedElement = (FrameTreeElement) event.getTreeNode().getData();

			ccdFrame = frameMgmt.loadFitsFrame(selectedElement.getFileName());

			byte[] falseColorPng = frameMgmt.loadPng(ccdFrame, true);

			graphicImage = new DefaultStreamedContent(new ByteArrayInputStream(falseColorPng), "image/png");
			
			// do not save files from selected nodes
			allowFrameSave = false;

		} catch (Exception e) {

		}
		// FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", event.getTreeNode().toString());

		// FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String doSetupFrameViewer() {

		try {

			init();  // load frames each time in case the list has changed
			
			breadcrumbMenuBean.addFirstItem("Frame/Instrument Tools ", "newProcedure.xhtml");

			return "/modules/frameViewer/frameViewer.xhtml?faces-redirect=true";

		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error querying camera database", e.getMessage()));
			return null;
		}

	}

	public void setupFrameToolFrameDisplay(short[][] rawFrame) {

		ccdFrame = new CcdFrame();
		ccdFrame.setRawFrame(rawFrame);

		byte[] falseColorPng = frameMgmt.loadPng(ccdFrame, false);

		graphicImage = new DefaultStreamedContent(new ByteArrayInputStream(falseColorPng), "image/png");

		allowFrameSave = true;
	}
	
	public void doHandMark() {
		String xStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_x");
		String yStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_y");
		
		int x = 2 * (new Double(xStr)).intValue(); // 512 * 2 = 1024
		int y = 2 * (new Double(yStr)).intValue(); // 512 * 2 = 1024
		// add to the centroid hidden form vars
		centroidXs = (centroidXs == null) ? "" + x : centroidXs + "," + x;
		centroidYs = (centroidYs == null) ? "" + y : centroidYs + "," + y;
	}
	
	public void doSaveFrame() {
		
		try {
			
			String newName = new FitsFilename(physicalModel.getInstrument().getInstrumentId(), 
					physicalModel.getInstrument().getCamera().getPupilWheel().getSelectedPupilMask().getPupilMaskType(), 
					0).generateFileName();
			
			// determine 'iteration' number if multiple frames of this mask taken today
			int iterationNumber = frameMgmt.findMatchingFitsFiles(newName.substring(0, newName.length()-8) + "*").size();
			
			FitsFilename fitsFilename = new FitsFilename(physicalModel.getInstrument().getInstrumentId(), 
					physicalModel.getInstrument().getCamera().getPupilWheel().getSelectedPupilMask().getPupilMaskType(), 
					iterationNumber);
			
			ccdFrame.setFitsFilename(fitsFilename.generateFileName());
			frameMgmt.saveFitsFrame(ccdFrame);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Successfully saved frame", ""));
			
			// update tree list
			init();
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error saving frame", e.getMessage()));
		}
	}
}
