/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.help.ui;

import java.io.File;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;

@Named
@SessionScoped
public class HelpController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private TreeNode helpContentRoot;

	@PostConstruct
	private void init() {
		
		// read in and parse the contents file

		helpContentRoot = new DefaultTreeNode("Root", null);

		TreeNode node0 = new DefaultTreeNode("folder", "Session Viewing", helpContentRoot);
		TreeNode node1 = new DefaultTreeNode("folder", "Standard Procedures", helpContentRoot);
		TreeNode node2 = new DefaultTreeNode("folder", "Special Procedures", helpContentRoot);
		TreeNode node3 = new DefaultTreeNode("folder", "Manual Tools", helpContentRoot);
		TreeNode node4 = new DefaultTreeNode("folder", "Frame Tools", helpContentRoot);
		TreeNode node5 = new DefaultTreeNode("folder", "External Interfaces ", helpContentRoot);
		TreeNode node6 = new DefaultTreeNode("folder", "Configuration", helpContentRoot);

		new DefaultTreeNode("link", new HelpPageLink("Sessions History", "doNothing();"), node0);
		new DefaultTreeNode("link", new HelpPageLink("Session View", "doNothing();"), node0);

		new DefaultTreeNode("link", new HelpPageLink("The Procedure Perspective", "doNothing()"), node1);
		new DefaultTreeNode("link", new HelpPageLink("Passive Tilt", "doNothing()"), node1);
		new DefaultTreeNode("link", new HelpPageLink("Phasing", "doNothing()"), node1);
		new DefaultTreeNode("link", new HelpPageLink("Fine Screen", "doNothing()"), node1);
		new DefaultTreeNode("link", new HelpPageLink("SUFS", "doNothing()"), node1);

		new DefaultTreeNode("link", new HelpPageLink("Pupil Registration", "doNothing()"), node2);
		new DefaultTreeNode("link", new HelpPageLink("Create Reference Map", "doNothing()"), node2);
		new DefaultTreeNode("link", new HelpPageLink("Center Telescope", "doNothing()"), node2);

		new DefaultTreeNode("link", new HelpPageLink("Camera Manual Operation", "doNothing()"), node3);
		
		new DefaultTreeNode("link", new HelpPageLink("The Frame Perspective", "doNothing()"), node4);
		new DefaultTreeNode("link", new HelpPageLink("CCD Manual Operation", "doNothing()"), node4);
		new DefaultTreeNode("link", new HelpPageLink("Camera Manual Operation", "doNothing()"), node4);
		new DefaultTreeNode("link", new HelpPageLink("Frame Tools", "doNothing()"), node4);

		new DefaultTreeNode("link", new HelpPageLink("ACS Manual Interface", "doNothing()"), node5);
		new DefaultTreeNode("link", new HelpPageLink("DCS Manual Interface", "doNothing()"), node5);
		
		new DefaultTreeNode("link", new HelpPageLink("Overview", "doNothing()"), node6);
		new DefaultTreeNode("link", new HelpPageLink("Spots Configuration", "doNothing()"), node6);
		new DefaultTreeNode("link", new HelpPageLink("Instrument Configuration", "doNothing()"), node6);


		node0.setExpanded(true);
		node1.setExpanded(true);
		node2.setExpanded(true);
		node3.setExpanded(true);
		node4.setExpanded(true);
		node5.setExpanded(true);
		node6.setExpanded(true);

	}

	public TreeNode getHelpContentRoot() {
		return helpContentRoot;
	}

	public void setHelpContentRoot(TreeNode helpContentRoot) {
		this.helpContentRoot = helpContentRoot;
	}

	// returns the content of the current page
	public String getCurrentPage() {

		System.out.println("GOT TO CURRENT PAGE");
		return "/pcs-web/help/content/git-scm.com.htm";

	}
	

	public String doViewDocumentation() {

		breadcrumbMenuBean.addFirstItem("Documentation", "/modules/util/documentation.xhtml");
		return "/modules/util/documentation.xhtml?faces-redirect=true";

	}

	public void doUpdatePage() {

	}

}