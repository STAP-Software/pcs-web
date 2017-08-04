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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
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
import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.computation.business.ComputationLibraryImpl;
import org.tmt.aps.peas.computation.model.CorrectOverscanDarkResult;
import org.tmt.aps.peas.computation.model.FindCentResult;
import org.tmt.aps.peas.computation.model.Subimage;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.FindCentConfig;
import org.tmt.aps.peas.extInterface.business.CcdMgmt;
import org.tmt.aps.peas.extInterface.ui.CameraManualController;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.CcdFrame;
import org.tmt.aps.peas.frame.model.FitsFilename;
import org.tmt.aps.peas.frame.model.FitsFilesMaps;
import org.tmt.aps.peas.frame.model.MarkedSubimage;
import org.tmt.aps.peas.instrument.business.CameraDefMgmt;
import org.tmt.aps.peas.instrument.business.PhysicalModel;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.CcdType;
import org.tmt.aps.peas.instrument.model.FilterType;
import org.tmt.aps.peas.instrument.model.PupilMask;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.business.ProcedureMgmt;
import org.tmt.aps.peas.telescope.business.TelescopeMgmt;

/**
 * JSF Controller for the frame tools user interface
 * @author smichaels
 */
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
	CcdMgmt ccdMgmt;
	@EJB
	ProcedureMgmt procedureMgmt;
	@EJB
	PhysicalModel physicalModel;
	@EJB
	TelescopeMgmt telescopeMgmt;
	@EJB
	GlobalConfigMgmt globalConfigMgmt;
	@EJB
	CameraDefMgmt cameraDefMgmt;
	@EJB
	private ComputationLibraryImpl computationLibrary;

	private TreeNode sessionRoot;
	private TreeNode typeRoot;

	private TreeNode selectedNode;
	private StreamedContent graphicImage;
	private int searchRadius;
	private String centroidXs;
	private String centroidYs;
	private String pixelValue;
	private boolean frameEditMode; // true = Pan/Zoom, false = mark
	private List<MarkedSubimage> markedSubimageList;

	private CcdFrame ccdFrame;
	private PupilMask pupilMask;
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

	public String getPixelValue() {
		return pixelValue;
	}

	public void setPixelValue(String pixelValue) {
		this.pixelValue = pixelValue;
	}

	public int getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(int searchRadius) {
		this.searchRadius = searchRadius;
	}

	public PupilMask getPupilMask() {
		return pupilMask;
	}

	public void setPupilMask(PupilMask pupilMask) {
		this.pupilMask = pupilMask;
	}

	public List<MarkedSubimage> getMarkedSubimageList() {
		return markedSubimageList;
	}

	public void setMarkedSubimageList(List<MarkedSubimage> markedSubimageList) {
		this.markedSubimageList = markedSubimageList;
	}

	public boolean getPanZoomDisplayMode() {
		return frameEditMode;
	}

	public boolean getMarkingDisplayMode() {
		return !frameEditMode;
	}

	public boolean isFrameEditMode() {
		return frameEditMode;
	}

	public void setFrameEditMode(boolean frameEditMode) {
		this.frameEditMode = frameEditMode;
	}

	public CcdFrame getCcdFrame() {
		return ccdFrame;
	}

	public void setCcdFrame(CcdFrame ccdFrame) {
		this.ccdFrame = ccdFrame;
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

		// partial keys apply here... 'FS' = FS-B, etc
		List<FitsFilename> filenameList = new ArrayList<FitsFilename>();
		for (String key : type2Fits.keySet()) {
			if (key.startsWith(procedureTypeCd)) {
				filenameList.addAll(type2Fits.get(key));
			}
		}
		return filenameList;
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

	/**
	 * Initialization method, sets up tree nodes and reloads all fits files by calling {@link #reloadFits()}
	 */
	@PostConstruct
	public void init() {

		long start = System.currentTimeMillis();

		// dummy for session root
		sessionRoot = new DefaultTreeNode(new FrameTreeElement("Sessions", "-"), null);
		typeRoot = new DefaultTreeNode("folder", new FrameTreeElement("Frames", "-"), null);

		try {

			String firstFilename = reloadFits();

			// load up first frame
			ccdFrame = frameMgmt.loadFitsFrame(firstFilename);
			byte[] falseColorPng = frameMgmt.loadPng(ccdFrame, true);
			graphicImage = new DefaultStreamedContent(new ByteArrayInputStream(falseColorPng), "image/png");

			searchRadius = 20; // TODO: whatever that should be - this needs to be loaded with the frame too.

			// default pupilMask for findCent marking info to PT
			pupilMask = cameraDefMgmt.getPupilMaskByTypeAndWheel(PupilMaskType.PUPIL_MASK_TYPE_ID_36,
					physicalModel.getInstrument().getCamera().getPupilWheel().getPupilWheelId());

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

		long end = System.currentTimeMillis();
		logger.info("Frame Tree loaded in " + (end - start) + " ms");
	}
	
	public void reload() {

		// dummy for session root
		sessionRoot = new DefaultTreeNode(new FrameTreeElement("Sessions", "-"), null);
		typeRoot = new DefaultTreeNode("folder", new FrameTreeElement("Frames", "-"), null);

		try {
			reloadFits();

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}

	/**
	 * Reloads all fits files and creates maps for tree browser
	 * @return the fits filename of the default 'first' fits file node
	 */
	public String reloadFits() {
			
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

		try {
			FitsFilesMaps fitsFilesMaps = frameMgmt.generateFitsFilesMaps();
			
			this.type2Fits = fitsFilesMaps.getType2Fits();
			Map<Integer, Map<Date, List<FitsFilename>>> telescope2Fits = fitsFilesMaps.getTelescope2Fits();
			
			for (Integer telescope : telescope2Fits.keySet()) {
	
				Map<Date, List<FitsFilename>> telescopeFitsMap = telescope2Fits.get(telescope);
	
				TreeNode telescopeNode = new DefaultTreeNode(new FrameTreeElement("Keck " + telescope, "-"), sessionRoot);
	
				for (Date date : telescopeFitsMap.keySet()) {
					List<FitsFilename> dateFitsList = telescopeFitsMap.get(date);
					TreeNode dateNode = new DefaultTreeNode(new FrameTreeElement(sdf.format(date), ""), telescopeNode);
	
					// order dateFitsList by procedure number
					Collections.sort(dateFitsList, new BeanComparator("procedureNumber"));
					for (FitsFilename fitsFile : dateFitsList) {
						TreeNode sessionNode00 = new DefaultTreeNode("picture",
								new FrameTreeElement(
										fitsFile.getProcedureNumber() + ": " + fitsFile.getProcedureName() + ": " + fitsFile.getFileName(),
										fitsFile.getFileName()),
								dateNode);
					}
				}
			}
	
			String firstFilename = null;
	
			for (String type : type2Fits.keySet()) {
	
				List<FitsFilename> typeFitsList = type2Fits.get(type);
	
				TreeNode typeNode = new DefaultTreeNode(new FrameTreeElement(type, ""), typeRoot);
	
				// order typeFitsList by telescope
				Collections.sort(typeFitsList, new BeanComparator("procedureNumber"));
				Collections.sort(typeFitsList, new BeanComparator("date"));
				Collections.sort(typeFitsList, new BeanComparator("telescope"));
				for (FitsFilename fitsFile : typeFitsList) {
					TreeNode sessionNode00 = new DefaultTreeNode("picture",
							new FrameTreeElement(fitsFile.getFileName(), fitsFile.getFileName()), typeNode);
	
					if (firstFilename == null) {
						firstFilename = fitsFile.getFileName();
					}
				}
			}
			return firstFilename;
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}
		
		
	}
	
	/**
	 * Node select listener from tree node.  Loads the selected FITS file into the frame display.
	 */
	public void onNodeSelect(NodeSelectEvent event) {

		try {

			FrameTreeElement selectedElement = (FrameTreeElement) event.getTreeNode().getData();

			ccdFrame = frameMgmt.loadFitsFrame(selectedElement.getFileName());

			byte[] falseColorPng = frameMgmt.loadPng(ccdFrame, true);

			graphicImage = new DefaultStreamedContent(new ByteArrayInputStream(falseColorPng), "image/png");

			// do not save files from selected nodes
			allowFrameSave = false;

			// clear any marking
			centroidXs = null;
			centroidYs = null;
			markedSubimageList.clear();

			// we should be setting the default search area and know the pupil mask type
			FitsFilename fitsFilename = new FitsFilename(ccdFrame.getFitsFilename());
			fitsFilename.getProcedureTypeCd();

			searchRadius = 20;

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	/**
	 * JSF Action method to render the frame tools view.  Calls {@link #init()}.  
	 * @return the JSF page to render
	 */
	public String doSetupFrameViewer() {

		try {

			init(); // load frames each time in case the list has changed

			breadcrumbMenuBean.addFirstItem("Frame/Instrument Tools ", "/modules/frameViewer/frameViewer.xhtml");

			return "/modules/frameViewer/frameViewer.xhtml?faces-redirect=true";

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
			return null;
		}

	}
	
	/**
	 * Sets up a raw frame for display (one that has been manually taken)
	 * @param rawFrame the raw frame
	 */
	public void setupFrameToolFrameDisplay(CcdFrame ccdFrame) {

		try {
			
		this.ccdFrame = ccdFrame;
		
		byte[] falseColorPng = frameMgmt.loadPng(ccdFrame, false);

		graphicImage = new DefaultStreamedContent(new ByteArrayInputStream(falseColorPng), "image/png");

		allowFrameSave = true;
		
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	/**
	 * JSF Action method called when the user marks the frame display.  Updates the centroid list with the new marked centroid.
	 */
	public void doHandMark() {
		String xStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_x");
		String yStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("canvas_y");

		int x = (new Double(xStr)).intValue();
		int y = (new Double(yStr)).intValue();

		// add to the centroid hidden form vars
		centroidXs = (centroidXs == null) ? "" + x : centroidXs + "," + x;
		centroidYs = (centroidYs == null) ? "" + y : centroidYs + "," + y;

		try {

			MarkedSubimage markedSubimage = calcMarkedSubimage(new FloatPoint(x, y), markedSubimageList.size(),
					markedSubimageList.size() > 0 ? markedSubimageList.get(0).getCentroid() : null);

			// create the table data
			markedSubimageList.add(markedSubimage);

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	/**
	 * JSF Action method to reset the frame marking data, clearing all centroids.
	 */
	public void doResetMarking() {
		centroidXs = null;
		centroidYs = null;
		markedSubimageList.clear();
	}

	/**
	 * JSF Action method removing the centroid last marked when the user clicks 'Undo'.
	 */
	public void doUndoMarking() {
		// remove the last one marked

		List<Float> xList = FloatListEncoder.decodeList(centroidXs);
		List<Float> yList = FloatListEncoder.decodeList(centroidYs);

		if (!xList.isEmpty())
			xList.remove(xList.size() - 1);
		if (!yList.isEmpty())
			yList.remove(yList.size() - 1);

		centroidXs = FloatListEncoder.encodeList(xList);
		centroidYs = FloatListEncoder.encodeList(yList);

		markedSubimageList.remove(markedSubimageList.size() - 1);
	}

	/**
	 * JSF Action method called as the user mouses over the frame, updates the pixel value display with the CCD value from the raw frame.
	 */
	public void doGetFrameValue() {
		
		String xStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("mouse_x");
		String yStr = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("mouse_y");

		int x = (new Double(xStr)).intValue();
		int y = (new Double(yStr)).intValue();

		int value = ccdFrame.getRawFrame()[x][y];

		pixelValue = "" + value;
	}

	/**
	 * JSF Action method to saves a frame that was taken manually
	 */
	public void doSaveFrame() {

		try {

			String newName = new FitsFilename(physicalModel.getInstrument().getInstrumentId(),
					physicalModel.getInstrument().getCamera().getPupilWheel().getSelectedPupilMask().getPupilMaskType(), 0, 0)
							.generateFileName();

			// determine 'iteration' number if multiple frames of this mask taken today
			int iterationNumber = frameMgmt.findMatchingFitsFiles(newName.substring(0, newName.length() - 8) + "*").size();

			FitsFilename fitsFilename = new FitsFilename(physicalModel.getInstrument().getInstrumentId(),
					physicalModel.getInstrument().getCamera().getPupilWheel().getSelectedPupilMask().getPupilMaskType(), iterationNumber, 0);

			// refresh status for fits header
			telescopeMgmt.refreshStatus();

			
			// get integration time here
			double intTime = ccdMgmt.getExposureTime();
			ccdFrame.setIntTime((float)intTime);
			
			ccdFrame.setFitsFilename(fitsFilename.generateFileName());
			frameMgmt.saveFitsFrame(ccdFrame);

			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateSuccessfulMessage());

			// update tree list
			reload();


		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.recordUpdateFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("crud.failure"), e);
		}

	}
	
	public void doCorrectDarkCurrent() {
		
		// get overscan results for testing
		Ccd ccd = physicalModel.getInstrument().getCcd();
		int overscanSize = (ccd.getCcdType().getOverscanReadoutWidth() - ccd.getCcdType().getNormalReadoutWidth())/2;

		CorrectOverscanDarkResult result = computationLibrary.correctOverscanFrameDarkOffsets(ccdFrame.getRawFrame(), 
				ccd.getDarkOvscnLeftColStart(), 
				ccd.getDarkOvscnLeftColEnd(), 
				ccd.getDarkOvscnRightColStart(), 
				ccd.getDarkOvscnRightColEnd(),
				overscanSize);
		
		// overwrite ccdFrame with corrected frame
		CcdFrame correctedFrame = frameMgmt.populateCcdFrame(result.getCorrectedFrame(), ccdFrame.getIntTime(), 0, 
				result.getDarkMedianValueLeft(), result.getDarkMedianValueRight());

		setupFrameToolFrameDisplay(correctedFrame);
		
	}
	

	/**
	 * JSF Action method to set the frame display mode to allow panning and zooming into the frame
	 * @param setting if true sets the pan/zoom mode on
	 */
	public void doSetPanZoomDisplayMode(boolean setting) {
		frameEditMode = setting;
	}

	/**
	 * JSF Action method to set the frame display mode to allow frame marking
	 * @param setting if true sets the frame marking mode on
	 */
	public void doSetMarkingDisplayMode(boolean setting) {
		frameEditMode = !setting;
	}

	/**
	 * JSF Action method that updates the marking info dialog with all the current frame marking
	 */
	public void doUpdateMarkingInfoDialog() {
		markedSubimageList = new ArrayList<MarkedSubimage>();

		// 1. get all the 'guesses' and call findCent for each one

		List<Float> xList = FloatListEncoder.decodeList(centroidXs);
		List<Float> yList = FloatListEncoder.decodeList(centroidYs);

		List<FloatPoint> guessList = FloatPointListEncoder.constructFromXandY(xList, yList);

		// if findCent fails then we just use the user-marked guesses as the centroids

		try {

			int count = 0;
			FloatPoint firstCentroid = guessList.get(0); // just in case findCent fails

			for (FloatPoint guess : guessList) {

				MarkedSubimage markedSubimage = calcMarkedSubimage(guess, count, firstCentroid);

				// 3. create the table data
				markedSubimageList.add(markedSubimage);
				
				count++;

			}

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}


	private MarkedSubimage calcMarkedSubimage(FloatPoint guess, int count, FloatPoint firstCentroid) {

		Subimage subimage = new Subimage(guess, 0.0f, 0.0f, 0);

		// load up defaults for mask type
		CcdType ccdType = physicalModel.getInstrument().getCcd().getCcdType();
		FindCentConfig findCentConfig = globalConfigMgmt.findFindCentConfig(pupilMask.getPupilMaskType().getPupilMaskTypeId(), FilterType.FILTER_TYPE_ID_611,
				Constants.SPOT_TYPE_INTERIOR, ccdType.getCcdTypeId());

		// then set the search radius for hand marking
		findCentConfig.setIrad(searchRadius);

		float[][] frame = ccdFrame.getCorrectedFrame();

		try {

			FindCentResult findCentResult = computationLibrary.findCent(frame, guess, findCentConfig, Constants.SPOT_TYPE_INTERIOR);
			subimage = findCentResult.getSubimage();
			
			if (!subimage.isGoodCentroid()) {
				throw new Exception("No good centroid found");
			}
			
			
			// 2. determine metrics against the first subimage

			FloatPoint deltaPos = new FloatPoint(0.0f, 0.0f);
			float deltaDistance = 0.0f;
			float deltaAngle = 0.0f;

			if (count == 0) {

				firstCentroid = subimage.getCentroid();

			} else {

				// calculate delta centroid
				FloatPoint centroid = subimage.getCentroid();
				float deltaX = centroid.x - firstCentroid.x;
				float deltaY = centroid.y - firstCentroid.y;
				deltaPos = new FloatPoint(deltaX, deltaY);

				// calculate the distance
				deltaDistance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

				// calculate the angle
				deltaAngle = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));

			}

			MarkedSubimage markedSubimage = new MarkedSubimage(count, subimage.getCentroid(), subimage.getSubimageIntensity(),
					subimage.getPeakIntensity(), subimage.getFindCentStatus(), deltaPos, deltaDistance, deltaAngle);

			return markedSubimage;

		} catch (Exception e) {
			MarkedSubimage markedSubimage = new MarkedSubimage(count, subimage.getCentroid(), subimage.getSubimageIntensity(),
					subimage.getPeakIntensity(), subimage.getFindCentStatus(), new FloatPoint(0,0), 0.0f, 0.0f);

			return markedSubimage;

		}
	}
}
