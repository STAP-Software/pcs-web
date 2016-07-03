/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

/**
 * JSF named object controlling the breadcrumb
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class BreadcrumbMenuBean implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	private MenuModel model;
	String immediateUrl;

	/**
	 * Default constructor: creates a breadcrumb menu model with one menu item: the session list
	 */
	public BreadcrumbMenuBean() {
		model = new DefaultMenuModel();
		MenuItem item = new MenuItem();
		item.setValue("Session List");
		item.setUrl("sessionList.xhtml");
		item.setIcon(null);
		item.setId("breadCrumbMenu_Item_0");  // need to set ids explicitly to avoid collisions in view
		model.addMenuItem(item);
	}
	
	/**
	 * Clears the breadcrumb menu model and adds one menu item
	 * @param name the name of the menu item
	 * @param url the URL it links to
	 */
	public void addFirstItem(String name, String url) {
		model = new DefaultMenuModel();
		MenuItem item = new MenuItem();
		item.setValue(name);
		item.setUrl(addBreadcrumbSource(url));
		immediateUrl = addBreadcrumbSource(url);
		item.setId("breadcrumbMenu_Item_" + model.getContents().size());
		model.addMenuItem(item);
	}
	
	/**
	 * Adds one menu item to the breadcrumb
	 * @param name the name of the menu item
	 * @param url the URL it links to
	 */
	public void addItem(String name, String url) {
		MenuItem item = new MenuItem();
		item.setValue(name);
		item.setUrl(addBreadcrumbSource(url));
		immediateUrl = addBreadcrumbSource(url);
		item.setId("breadcrumbMenu_Item_" + model.getContents().size());
		model.addMenuItem(item);
	}
	
	/**
	 * Adds a menu item to the breadcrumb at the front of the list
	 * @param name the name of the menu item
	 * @param url the URL it links to
	 */
	public void insertFirst(String name, String url) {
		MenuItem item = new MenuItem();
		item.setValue(name);
		item.setUrl(addBreadcrumbSource(url));
		if (model.getContents().size() == 0) {
			// if this will be the only item, then set the immediateUrl
			immediateUrl = addBreadcrumbSource(url);
		}
		item.setId("breadcrumbMenu_Item_" + model.getContents().size());
		model.getContents().add(0, item);
	}
	
	private String addBreadcrumbSource(String url) {
		if (url.contains("?")) {
			return url + "&from-breadcrumb=true";
		} else {
			return url + "?from-breadcrumb=true";
		}
	}
	
	/**
	 * Removes all menuitems from the breadcrumb until reaching the item matching name
	 * @param name the name to match
	 */
	public void removeTo(String name) {

		MenuModel newModel = new DefaultMenuModel();
		
		List<UIComponent> components = model.getContents();
		for (UIComponent component: components) {
			MenuItem item = (MenuItem)component;
			String candidate = (String)item.getValue();
			newModel.addMenuItem(item);
			if (candidate.contains(name)) {
				model = newModel;
				return;
			}
		}
		
	}

	/**
	 * @return the breadcrumb menu model
	 */
	public MenuModel getModel() {
		return model;
	}

	/**
	 * @return the immediate URL, which is the URL of the last item in the breadcrumb
	 */
	public String getImmediateUrl() {
		return immediateUrl;
	}
	
	/**
	 * @return true if the immediate URL is the procedurePerspective
	 */
	public boolean getInProcedure() {
		
		logger.debug("immediateUrl = " + immediateUrl);
		
		return immediateUrl != null && immediateUrl.contains("procedurePerspective");
	}

	/**
	 * Removes the last item in the breadcrumb
	 */
	public void removeLast() {
		MenuModel newModel = new DefaultMenuModel();
		
		List<UIComponent> components = model.getContents();
		for (int i=0; i<components.size()-1; i++) {
			UIComponent component = components.get(i);
			MenuItem item = (MenuItem)component;
			newModel.addMenuItem(item);
		}		
		model = newModel;
	}
	
}
