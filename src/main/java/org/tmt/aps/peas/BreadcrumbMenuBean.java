/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

import java.io.Serializable;
import java.util.List;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIComponent;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.model.menu.Submenu;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuElement;



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
		MenuItem item = DefaultMenuItem.builder()
		.value("Session List")
		.url("sessionList.xhtml")
		.icon(null)
		.id("breadCrumbMenu_Item_0")
		.build();  // need to set ids explicitly to avoid collisions in view
		
		
		model.getElements().add(item);
	}
	
	/**
	 * Clears the breadcrumb menu model and adds one menu item
	 * @param name the name of the menu item
	 * @param url the URL it links to
	 */
	public void addFirstItem(String name, String url) {
		model = new DefaultMenuModel();
		
		MenuItem item = DefaultMenuItem.builder()
			.value(name)
			.url(addBreadcrumbSource(url))
			.icon(null)
			.id("breadcrumbMenu_Item_" + model.getElements().size())
			.build();  // need to set ids explicitly to avoid collisions in view
		
			
		immediateUrl = addBreadcrumbSource(url);

		model.getElements().add(item);


	}
	
	/**
	 * Adds one menu item to the breadcrumb
	 * @param name the name of the menu item
	 * @param url the URL it links to
	 */
	public void addItem(String name, String url) {
		MenuItem item = DefaultMenuItem.builder()
			.value(name)
			.url(addBreadcrumbSource(url))
			.icon(null)
			.id("breadcrumbMenu_Item_" + model.getElements().size())
			.build();  // need to set ids explicitly to avoid collisions in view
		
		

		
		immediateUrl = addBreadcrumbSource(url);


		model.getElements().add(item);
	}
	
	/**
	 * Adds a menu item to the breadcrumb at the front of the list
	 * @param name the name of the menu item
	 * @param url the URL it links to
	 */
	public void insertFirst(String name, String url) {
		MenuItem item = DefaultMenuItem.builder()
			.value(name)
			.url(addBreadcrumbSource(url))
			.icon(null)
			.id("breadcrumbMenu_Item_" + model.getElements().size())
			.build();  // need to set ids explicitly to avoid collisions in view
		
		
		
		if (model.getElements().size() == 0) {
			// if this will be the only item, then set the immediateUrl
			immediateUrl = addBreadcrumbSource(url);
		}

		model.getElements().add(0, item);
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
	public void removeTo(String value) {
	    List<MenuElement> elements = model.getElements();

	    for (int i = elements.size() - 1; i >= 0; i--) {
	        MenuElement element = elements.get(i);

	        if (element instanceof MenuItem) {
	            MenuItem item = (MenuItem) element;

	            if (value.equals(item.getValue())) {
	                break; // stop trimming
	            } else {
	                elements.remove(i);
	            }
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
		List<MenuElement> elements = model.getElements();
	    if (!elements.isEmpty()) {
	        elements.remove(elements.size() - 1);
	    }
	}
	
}
