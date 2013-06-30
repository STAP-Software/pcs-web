package org.tmt.aps.peas.config.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.config.model.Setup;

@Named
@SessionScoped
public class SetupController implements Serializable {


	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	Setup setup;
	


	public Setup getSetup() {
		return setup;
	}

	public void setSetup(Setup setup) {
		this.setup = setup;
	}

	
	public String doViewSetup() {

		setup = new Setup();
		
		setup.setAutoPointTelescope(2);
		setup.setCameraRot(0.004f);
		setup.setCoarseMirrorX(1.2345f);
		setup.setCoarseMirrorX(0.0043f);
		setup.setCompPhasingPlogFilename("A very long filename that we can save.xls");
		setup.setFandIAttempts(8);
		setup.setFlattenField(false);
		setup.setRemoveBadPixels(true);
		setup.setSubtractDarkCurrent(false);
		
		breadcrumbMenuBean.addFirstItem("Setup", "doViewSetup()");

		return "/modules/config/setup.xhtml?faces-redirect=true";
	}


	public String doCancelSaveSetup() {

		return "/modules/session/sessionList.xhtml?faces-redirect=true";
	}

}
