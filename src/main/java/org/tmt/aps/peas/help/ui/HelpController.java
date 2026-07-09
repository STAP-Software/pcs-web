/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.help.ui;

import java.io.File;
import java.io.FileReader;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;

/**
 * JSF Controller for rendering help pages
 * @author smichaels
 */
@Named
@SessionScoped
public class HelpController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8369775933545061345L;

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private TreeNode<String> helpContentRoot;
	
	private String currentPage = "/pcs-web/help/content/overview.htm";

	/**
	 * Initialization method called on bean construction.  This method opens and parses the contents.json file, 
	 * and generates a JSF tree browser from those contents.  Each leaf node will then contain a {@link HelpPageLink} that links to 
	 * HTML content in the help/content/ directory. 
	 */
	@PostConstruct
	private void init() {
		
		
		helpContentRoot = new DefaultTreeNode<String>("Root", null);

		
		// read in and parse the contents file
		try {
			
			String propertiesPath = System.getProperty("org.tmt.aps.peas.peasPropertiesPath");

			File file = new File(propertiesPath + File.separator + "help" + File.separator + "content" + File.separator + "contents.json");

			// read the json file
			FileReader reader = new FileReader(file.getAbsolutePath());

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);


			// get an array from the JSON object
			JSONArray contents = (JSONArray) jsonObject.get("contents");
			
			// take the elements of the json array
			for (int i=0; i<contents.size(); i++) {
				JSONObject sectionObj = (JSONObject)contents.get(i);
				
				String sectionName = (String)sectionObj.get("sectionName");
				
				TreeNode<String> treeNode = new DefaultTreeNode<String>("folder", sectionName, helpContentRoot);
				
				
				JSONArray subsectionsObj = (JSONArray)sectionObj.get("subsections");
				
				for (int j=0; j<subsectionsObj.size(); j++) {

					JSONObject subsectionObj = (JSONObject)subsectionsObj.get(j);
					
					String subsectionName = (String)subsectionObj.get("sectionName");
					String subsectionLink = (String)subsectionObj.get("contentFilename");

					new DefaultTreeNode<HelpPageLink>("link", new HelpPageLink(subsectionName, subsectionLink), treeNode);

				}
				
				treeNode.setExpanded(true);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public TreeNode<String> getHelpContentRoot() {
		return helpContentRoot;
	}

	public void setHelpContentRoot(TreeNode<String> helpContentRoot) {
		this.helpContentRoot = helpContentRoot;
	}

	// returns the content of the current page
	public String getCurrentPage() {

		return currentPage;

	}
	
	/**
	 * JSF Action method that renders the help documentation page
	 * @return the JSF page to render
	 */
	public String doViewDocumentation() {

		breadcrumbMenuBean.addFirstItem("Documentation", "/modules/util/documentation.xhtml");
		return "/modules/util/documentation.xhtml?faces-redirect=true";

	}

	/**
	 * JSF Action method called when the user clicks on a table of contents tree node leaf
	 * @param newPage the HTML file name that was stored in the tree node {@link HelpPageLink}
	 */
	public void doUpdatePage(String newPage) {
		// set the current page
		this.currentPage = "/pcs-web/help/content/" + newPage;
	}

}