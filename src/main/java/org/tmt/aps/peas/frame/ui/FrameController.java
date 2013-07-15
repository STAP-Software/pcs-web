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
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanComparator;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.model.FitsFrame;
import org.tmt.aps.peas.frame.model.PcsFitsFile;

@Named
@SessionScoped
public class FrameController implements Serializable {

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	@EJB
	FrameMgmt frameMgmt;

	private TreeNode sessionRoot;
	private TreeNode typeRoot;

	private TreeNode selectedNode;
	private StreamedContent graphicImage;
	private int searchRadius;

	Map<String, List<PcsFitsFile>> type2Fits;
	
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
		FrameTreeElement fte = (FrameTreeElement)selectedNode.getData();
		return fte.getFileName();
	}

	public StreamedContent getGraphicImage() {
		return graphicImage;
	}
	
	public List<PcsFitsFile> getProcedureFitsFiles(String procedureTypeCd) {
		return type2Fits.get(procedureTypeCd);
	}
	public List<PcsFitsFile> getAllFitsFiles() {
		List<PcsFitsFile> allFitsFiles = new ArrayList<PcsFitsFile>();
		for (String key : type2Fits.keySet()) {
			allFitsFiles.addAll(type2Fits.get(key));
		}
		return allFitsFiles;
	}

	@PostConstruct
	public void init() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

		// dummy for session root
		sessionRoot = new DefaultTreeNode(new FrameTreeElement("Sessions", "-"), null);
		typeRoot = new DefaultTreeNode("folder", new FrameTreeElement("Frames", "-"), null);

		// search folder for fits files

		Map<Integer, Map<Date, List<PcsFitsFile>>> telescope2Fits = new HashMap<Integer, Map<Date, List<PcsFitsFile>>>();
		
		type2Fits = new HashMap<String, List<PcsFitsFile>>();

		try {

			List<PcsFitsFile> fitsFileList = frameMgmt.findAllFitsFiles();

			for (PcsFitsFile fitsFile : fitsFileList) {

				try {
					Map<Date, List<PcsFitsFile>> telescopeFitsMap = telescope2Fits.get(new Integer(fitsFile.getTelescope()));
					if (telescopeFitsMap == null) {
						telescopeFitsMap = new TreeMap<Date, List<PcsFitsFile>>();
						telescope2Fits.put(new Integer(fitsFile.getTelescope()), telescopeFitsMap);
					}

					//System.out.println("map get filename = " + fitsFile.getFileName());
					//System.out.println("map get dateString = " + fitsFile.getDate());

					List<PcsFitsFile> dateFitsList = telescopeFitsMap.get(fitsFile.getDate());
					if (dateFitsList == null) {
						dateFitsList = new ArrayList<PcsFitsFile>();
						telescopeFitsMap.put(fitsFile.getDate(), dateFitsList);
					}
					dateFitsList.add(fitsFile);

					
					List<PcsFitsFile> typeFitsList = type2Fits.get(fitsFile.getProcedureTypeCd());
					if (typeFitsList == null) {
						typeFitsList = new ArrayList<PcsFitsFile>();
						type2Fits.put(fitsFile.getProcedureTypeCd(), typeFitsList);
					}
					typeFitsList.add(fitsFile);
					
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (Integer telescope : telescope2Fits.keySet()) {

				Map<Date, List<PcsFitsFile>> telescopeFitsMap = telescope2Fits.get(telescope);

				TreeNode telescopeNode = new DefaultTreeNode(new FrameTreeElement("Keck " + telescope, "-"), sessionRoot);

				for (Date date : telescopeFitsMap.keySet()) {
					List<PcsFitsFile> dateFitsList = telescopeFitsMap.get(date);
					TreeNode dateNode = new DefaultTreeNode(new FrameTreeElement(sdf.format(date), ""), telescopeNode);

					// TODO: order dateFitsList by procedure number
					Collections.sort(dateFitsList, new BeanComparator("procedureNumber"));
					for (PcsFitsFile fitsFile : dateFitsList) {
						TreeNode sessionNode00 = new DefaultTreeNode("picture", new FrameTreeElement(fitsFile.getProcedureNumber() + ": "
								+ fitsFile.getProcedureName() + ": " + fitsFile.getFileName(), fitsFile.getFileName()), dateNode);
					}

				}

			}

			for (String type : type2Fits.keySet()) {

				List<PcsFitsFile> typeFitsList = type2Fits.get(type);

				TreeNode typeNode = new DefaultTreeNode(new FrameTreeElement(type, ""), typeRoot);

				// TODO: order dateFitsList by procedure number
				Collections.sort(typeFitsList, new BeanComparator("telescope"));
				for (PcsFitsFile fitsFile : typeFitsList) {
					TreeNode sessionNode00 = new DefaultTreeNode("picture", new FrameTreeElement(fitsFile.getFileName(), fitsFile.getFileName()), typeNode);
				}


			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void onNodeSelect(NodeSelectEvent event) {

		try {

			FrameTreeElement selectedElement = (FrameTreeElement) event.getTreeNode().getData();

			FitsFrame fbs = frameMgmt.loadFitsFrame(selectedElement.getFileName());

			short frameArray[][] = fbs.getResult();

			FalseColorProcessor falseColorer = new FalseColorProcessor();
			
			byte[] falseColorPng = falseColorer.createImage(frameArray);
			
	        graphicImage = new DefaultStreamedContent(new ByteArrayInputStream(falseColorPng), "image/png");   

		} catch (Exception e) {

		}
		// FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", event.getTreeNode().toString());

		// FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String doSetupFrameViewer() {



		breadcrumbMenuBean.addItem("Frame Viewer ", "newProcedure.xhtml");

		return "/modules/frameViewer/frameViewer.xhtml?faces-redirect=true";
	}

}
