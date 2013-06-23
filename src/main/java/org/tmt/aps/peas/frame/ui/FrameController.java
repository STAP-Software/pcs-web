package org.tmt.aps.peas.frame.ui;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanComparator;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.frame.fits.FalseColorProcessor;
import org.tmt.aps.peas.frame.fits.FitsFrame;
import org.tmt.aps.peas.frame.fits.FitsReader;
import org.tmt.aps.peas.frame.model.FitsFile;

@Named
@SessionScoped
public class FrameController implements Serializable {

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private TreeNode sessionRoot;
	private TreeNode typeRoot;

	private TreeNode selectedNode;
	private StreamedContent graphicImage;
	
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

	public StreamedContent getGraphicImage() {
		return graphicImage;
	}

	public void onNodeSelect(NodeSelectEvent event) {

		try {
			
			// TODO: frameFolder should be in peas.properties and read in by PeasProperties 
			
			String frameFolder = "C:\\Users\\Scott\\Desktop\\frames";
			
			FrameTreeElement selectedElement = (FrameTreeElement)event.getTreeNode().getData();
			
			String path = frameFolder + File.separator + selectedElement.getFileName();
			
			FitsReader ft = new FitsReader();

			FitsFrame fbs = ft.readFits(path);
			short frameArray[][] = fbs.getResult();

			FalseColorProcessor falseColorer = new FalseColorProcessor();
			graphicImage = falseColorer.createImage(frameArray);
			
		} catch (Exception e) {

		}
		// FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", event.getTreeNode().toString());

		// FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String doSetupFrameViewer() {

		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		
		// dummy for session root
		sessionRoot = new DefaultTreeNode(new FrameTreeElement("Sessions", "-"), null);

		// search folder for fits files
		
		// TODO: frameFolder should be in peas.properties and read in by PeasProperties 
		
		String frameFolder = "C:\\Users\\Scott\\Desktop\\frames";
		
		// read in and parse each frame and build up 
		File folder = new File(frameFolder);
		
		Map<Integer, Map<Date, List<FitsFile>>> telescope2Fits = new HashMap<Integer, Map<Date, List<FitsFile>>>();
		
	    for (File fileEntry : folder.listFiles()) {
	    	
	    	String filename = fileEntry.getName();
	    	
	    	FitsFile fitsFile = new FitsFile(filename);
	    	
	    	Map<Date, List<FitsFile>> telescopeFitsMap = telescope2Fits.get(new Integer(fitsFile.getTelescope()));
	    	if (telescopeFitsMap == null) {
	    		telescopeFitsMap = new TreeMap<Date, List<FitsFile>>();
	    		telescope2Fits.put(new Integer(fitsFile.getTelescope()), telescopeFitsMap);
	    	}
	    	List<FitsFile> dateFitsList = telescopeFitsMap.get(fitsFile.getDate());
	    	if (dateFitsList == null) {
	    		dateFitsList = new ArrayList<FitsFile>();
	    		telescopeFitsMap.put(fitsFile.getDate(), dateFitsList);
	    	}
	    	dateFitsList.add(fitsFile);
	    }
		
	    for (Integer telescope : telescope2Fits.keySet()) {
	    	
	    	Map<Date, List<FitsFile>> telescopeFitsMap = telescope2Fits.get(telescope);
	
			TreeNode telescopeNode = new DefaultTreeNode(new FrameTreeElement("Keck " + telescope, "-"), sessionRoot);

	    	for (Date date : telescopeFitsMap.keySet()) {
	    		List<FitsFile> dateFitsList = telescopeFitsMap.get(date);
				TreeNode dateNode = new DefaultTreeNode(new FrameTreeElement(sdf.format(date), ""), telescopeNode);
	    		
				// TODO: order dateFitsList by procedure number
				Collections.sort(dateFitsList, new BeanComparator("procedureNumber"));
				for (FitsFile fitsFile : dateFitsList) {
					TreeNode sessionNode00 = new DefaultTreeNode("picture", new FrameTreeElement(fitsFile.getProcedureNumber() + ": " + fitsFile.getProcedureName() + ": " + fitsFile.getFileName(), fitsFile.getFileName()), dateNode);					
				}
				
	    	}
	    	
	    	
	    }

	    /*
		TreeNode sessionNode0 = new DefaultTreeNode(new FrameTreeElement("11/26/2005", ""), telescopeNode1);
		TreeNode sessionNode1 = new DefaultTreeNode(new FrameTreeElement("2/5/2008", ""), telescopeNode1);
		TreeNode sessionNode2 = new DefaultTreeNode(new FrameTreeElement("6/4/2013", ""), telescopeNode2);

		TreeNode sessionNode00 = new DefaultTreeNode("picture", new FrameTreeElement("Passive Tilt 01", ""), sessionNode0);
		TreeNode sessionNode01 = new DefaultTreeNode("picture", new FrameTreeElement("Phasing 01", ""), sessionNode0);
		TreeNode sessionNode02 = new DefaultTreeNode("picture", new FrameTreeElement("Phasing 02", ""), sessionNode0);
		TreeNode sessionNode03 = new DefaultTreeNode("picture", new FrameTreeElement("Phasing 03", ""), sessionNode0);

		TreeNode sessionNode10 = new DefaultTreeNode("picture", new FrameTreeElement("UFS 01", ""), sessionNode1);
		TreeNode sessionNode11 = new DefaultTreeNode("picture", new FrameTreeElement("UFS 02", ""), sessionNode1);

		TreeNode sessionNode20 = new DefaultTreeNode("picture", new FrameTreeElement("SUFS 01", ""), sessionNode2);
		TreeNode sessionNode21 = new DefaultTreeNode("picture", new FrameTreeElement("SUFS 02", ""), sessionNode2);
		*/
		// dummy for type root

		typeRoot = new DefaultTreeNode("folder", new FrameTreeElement("Frames", "-"), null);

		TreeNode typeNode1 = new DefaultTreeNode(new FrameTreeElement("UFS", ""), typeRoot);
		TreeNode typeNode2 = new DefaultTreeNode(new FrameTreeElement("SUFS", ""), typeRoot);

		TreeNode typeNode10 = new DefaultTreeNode("picture", new FrameTreeElement("K1 - 11/26/2005 - Passive Tilt 01", ""), typeNode1);
		TreeNode typeNode11 = new DefaultTreeNode("picture", new FrameTreeElement("K1 - 2/5/2008 - Passive Tilt 01", ""), typeNode1);
		TreeNode typeNode12 = new DefaultTreeNode("picture", new FrameTreeElement("K1 - 6/4/2013 - Passive Tilt 01", ""), typeNode1);

		TreeNode typeNode20 = new DefaultTreeNode("picture", new FrameTreeElement("K1 - 11/26/2005 - Passive Tilt 01", ""), typeNode2);
		TreeNode typeNode21 = new DefaultTreeNode("picture", new FrameTreeElement("K1 - 2/5/2008 - Passive Tilt 01", ""), typeNode2);
		TreeNode typeNode22 = new DefaultTreeNode("picture", new FrameTreeElement("K1 - 6/4/2013 - Passive Tilt 01", ""), typeNode2);

		breadcrumbMenuBean.addItem("Frame Viewer ", "newProcedure.xhtml");

		return "/modules/frameViewer/frameViewer.xhtml?faces-redirect=true";
	}

}
