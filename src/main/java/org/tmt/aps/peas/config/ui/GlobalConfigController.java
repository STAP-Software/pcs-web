package org.tmt.aps.peas.config.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.config.model.DisplayPreferences;
import org.tmt.aps.peas.config.model.GlobalConfig;

@Named
@SessionScoped
public class GlobalConfigController implements Serializable {


	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	GlobalConfig globalConfig;
	DisplayPreferences displayPreferences;

	public GlobalConfig getGlobalConfig() {
		return globalConfig;
	}


	public void setGlobalConfig(GlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}


	public DisplayPreferences getDisplayPreferences() {
		return displayPreferences;
	}


	public void setDisplayPreferences(DisplayPreferences displayPreferences) {
		this.displayPreferences = displayPreferences;
	}


	@PostConstruct
	public void init() {

		globalConfig = new GlobalConfig();
		
		globalConfig.setAutoPointTelescope(2);
		globalConfig.setCameraRot(0.004f);
		globalConfig.setCoarseMirrorX(1.2345f);
		globalConfig.setCoarseMirrorX(0.0043f);
		globalConfig.setCompPhasingPlogFilename("A very long filename that we can save.xls");
		globalConfig.setFandIAttempts(8);
		globalConfig.setFlattenField(false);
		globalConfig.setRemoveBadPixels(true);
		globalConfig.setSubtractDarkCurrent(false);
		
		displayPreferences = new DisplayPreferences();
		displayPreferences.setAutoDisplayActuatorDeltas(true);
		displayPreferences.setAutoDisplayCentroidOffsets(true);
		displayPreferences.setAutoDisplayProcedureDataLog(true);
	}


	public void doCancelSaveSetup() {

	}

}
