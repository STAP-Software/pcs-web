/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.help.ui;

import java.io.File;
import java.io.FileReader;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;

@Named
@SessionScoped
public class HelpController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private TreeNode helpContentRoot;
	
	private String currentPage = "/pcs-web/help/content/git-scm.com.htm";

	@PostConstruct
	private void init() {
		
		
		helpContentRoot = new DefaultTreeNode("Root", null);

		
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
				
				TreeNode treeNode = new DefaultTreeNode("folder", sectionName, helpContentRoot);
				
				
				JSONArray subsectionsObj = (JSONArray)sectionObj.get("subsections");
				
				for (int j=0; j<subsectionsObj.size(); j++) {

					JSONObject subsectionObj = (JSONObject)subsectionsObj.get(j);
					
					String subsectionName = (String)subsectionObj.get("sectionName");
					String subsectionLink = (String)subsectionObj.get("contentFilename");

					new DefaultTreeNode("link", new HelpPageLink(subsectionName, subsectionLink), treeNode);

				}
				
				treeNode.setExpanded(true);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public TreeNode getHelpContentRoot() {
		return helpContentRoot;
	}

	public void setHelpContentRoot(TreeNode helpContentRoot) {
		this.helpContentRoot = helpContentRoot;
	}

	// returns the content of the current page
	public String getCurrentPage() {

		return currentPage;

	}
	

	public String doViewDocumentation() {

		breadcrumbMenuBean.addFirstItem("Documentation", "/modules/util/documentation.xhtml");
		return "/modules/util/documentation.xhtml?faces-redirect=true";

	}

	public void doUpdatePage(String newPage) {
		// set the current page
		this.currentPage = "/pcs-web/help/content/" + newPage;
	}

}