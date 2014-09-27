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

@Named
@SessionScoped
public class BreadcrumbMenuBean implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	private MenuModel model;
	String immediateUrl;

	public BreadcrumbMenuBean() {
		model = new DefaultMenuModel();
		MenuItem item = new MenuItem();
		item.setValue("Session List");
		item.setUrl("sessionList.xhtml");
		item.setIcon(null);
		item.setId("breadCrumbMenu_Item_0");  // need to set ids explicitly to avoid collisions in view
		model.addMenuItem(item);
	}
	
	public void addFirstItem(String name, String url) {
		model = new DefaultMenuModel();
		MenuItem item = new MenuItem();
		item.setValue(name);
		item.setUrl(url);
		immediateUrl = url;
		item.setId("breadcrumbMenu_Item_" + model.getContents().size());
		model.addMenuItem(item);
	}
	
	public void addItem(String name, String url) {
		MenuItem item = new MenuItem();
		item.setValue(name);
		item.setUrl(url);
		immediateUrl = url;
		item.setId("breadcrumbMenu_Item_" + model.getContents().size());
		model.addMenuItem(item);
	}
	
	public void removeTo(String name) {

		MenuModel newModel = new DefaultMenuModel();
		
		List<UIComponent> components = model.getContents();
		for (UIComponent component: components) {
			MenuItem item = (MenuItem)component;
			String candidate = (String)item.getValue();
			newModel.addMenuItem(item);
			if (candidate.equals(name)) {
				model = newModel;
				return;
			}
		}
		
	}

	public MenuModel getModel() {
		return model;
	}

	public String getImmediateUrl() {
		return immediateUrl;
	}
	
	
}
