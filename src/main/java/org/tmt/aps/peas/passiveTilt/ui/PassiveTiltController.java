package org.tmt.aps.peas.passiveTilt.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.Procedure;
import org.tmt.aps.peas.ProcedureWizardBean;
import org.tmt.aps.peas.SessionController;
import org.tmt.aps.peas.passiveTilt.business.PassiveTiltMgmt;
import org.tmt.aps.peas.passiveTilt.model.PassiveTiltDef;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;

@Named
@SessionScoped
public class PassiveTiltController implements Serializable {

	@EJB
	PassiveTiltMgmt passiveTiltMgmt;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;
	@Inject
	private ProcedureWizardBean procedureWizardBean;
	@Inject
	private SessionController sessionController;

	PassiveTiltDef passiveTiltDef;

	List<Procedure> sessionList;
	List<String> frameList;

	@PostConstruct
	private void init() {

		// test only, in the future, the DB will return a list of procedures,
		// and the menus will be generated from those

		passiveTiltDef = new PassiveTiltDef(); // for advanced options access from template

		frameList = new ArrayList<String>();
		frameList.add("1");
		frameList.add("2");
		frameList.add("3");

		/*
		 * selectList = new ArrayList<ConfigurationOption>(); selectList.add(new ConfigurationOption("Pure Tilts", "1")); selectList.add(new
		 * ConfigurationOption("Focus Mode Pistons", "2")); selectList.add(new ConfigurationOption( "Pure Tilts + Focus Mode Pistons",
		 * "3")); selectList.add(new ConfigurationOption("Pure Focus Mode", "4")); selectList.add(new
		 * ConfigurationOption("Pure Tilts + Optimal Pistons", "5")); selectList.add(new ConfigurationOption("Optimal Pistons", "6"));
		 * configElement = new ConfigurationElement("Calculation Options", "calculationOptions",
		 * ConfigurationElement.ELEMENT_TYPE_SELECTLIST, 0, selectList); procedureAdvancedConfigurationList.add(configElement);
		 * 
		 * configElement = new ConfigurationElement("Auto Display Centriods", "autoDisplayCentroids",
		 * ConfigurationElement.ELEMENT_TYPE_CHECKBOX, 0, null); procedureExecutionPreferencesList.add(configElement);
		 * 
		 * configElement = new ConfigurationElement( "Auto Display Centriod Offsets", "autoDisplayCentroidOffsets",
		 * ConfigurationElement.ELEMENT_TYPE_CHECKBOX, 0, null); procedureExecutionPreferencesList.add(configElement);
		 * 
		 * configElement = new ConfigurationElement( "Auto Display Avg Centriod Offsets", "autoDisplayAvgCentroidOffsets",
		 * ConfigurationElement.ELEMENT_TYPE_CHECKBOX, 0, null); procedureExecutionPreferencesList.add(configElement);
		 * 
		 * configElement = new ConfigurationElement( "Auto Display Actuator Deltas", "autoDisplayActuatorDeltas",
		 * ConfigurationElement.ELEMENT_TYPE_CHECKBOX, 0, null); procedureExecutionPreferencesList.add(configElement);
		 * 
		 * configElement = new ConfigurationElement( "Auto Display Procedure Data Log", "autoDisplayProcedureDataLog",
		 * ConfigurationElement.ELEMENT_TYPE_CHECKBOX, 0, null); procedureExecutionPreferencesList.add(configElement);
		 * 
		 * selectList = new ArrayList<ConfigurationOption>(); selectList.add(new ConfigurationOption("Do Not Remove", "1"));
		 * selectList.add(new ConfigurationOption("Remove", "2")); selectList.add(new ConfigurationOption("Prompt User During Procedure",
		 * "3")); configElement = new ConfigurationElement( "Frame Scale and Rotation Removal", "frameScaleRotationRemoval",
		 * ConfigurationElement.ELEMENT_TYPE_SELECTLIST, 0, selectList); procedureAdvancedConfigurationList.add(configElement);
		 * 
		 * configElement = new ConfigurationElement("Auto Save Frames", "autoSaveFrames", ConfigurationElement.ELEMENT_TYPE_CHECKBOX, 0,
		 * null); procedureExecutionPreferencesList.add(configElement);
		 * 
		 * selectList = new ArrayList<ConfigurationOption>(); selectList.add(new ConfigurationOption("Auto Move Telescope", "1"));
		 * selectList.add(new ConfigurationOption("Never Move Telescope", "2")); selectList.add(new
		 * ConfigurationOption("Prompt User During Procedure", "3")); configElement = new ConfigurationElement("Auto Center Telescope",
		 * "autoCenterTelescope", ConfigurationElement.ELEMENT_TYPE_SELECTLIST, 0, selectList);
		 * procedureAdvancedConfigurationList.add(configElement);
		 * 
		 * selectList = new ArrayList<ConfigurationOption>(); selectList.add(new ConfigurationOption("Auto Command Tilt Plate", "1"));
		 * selectList .add(new ConfigurationOption("Never Command Tilt Plate", "2")); selectList.add(new
		 * ConfigurationOption("Prompt User During Procedure", "3")); configElement = new ConfigurationElement("Auto Center Pupil",
		 * "autoCenterPupil", ConfigurationElement.ELEMENT_TYPE_SELECTLIST, 0, selectList);
		 * procedureAdvancedConfigurationList.add(configElement);
		 * 
		 * selectList = new ArrayList<ConfigurationOption>(); selectList.add(new ConfigurationOption("Coarse", "1")); selectList.add(new
		 * ConfigurationOption("Fine", "2")); selectList.add(new ConfigurationOption("Prompt User During Procedure", "3")); configElement =
		 * new ConfigurationElement("Auto Center Pupil Mechanism", "autoCenterPupilMechanism", ConfigurationElement.ELEMENT_TYPE_SELECTLIST,
		 * 0, selectList); procedureAdvancedConfigurationList.add(configElement);
		 * 
		 * selectList = new ArrayList<ConfigurationOption>(); selectList.add(new ConfigurationOption("Auto Command Primary", "1"));
		 * selectList.add(new ConfigurationOption("Never Command Primary", "2")); selectList.add(new
		 * ConfigurationOption("Prompt User During Procedure", "3")); configElement = new ConfigurationElement("Auto Send Actuator Deltas",
		 * "autoSendActuatorDeltas", ConfigurationElement.ELEMENT_TYPE_SELECTLIST, 0, selectList);
		 * procedureAdvancedConfigurationList.add(configElement);
		 * 
		 * selectList = new ArrayList<ConfigurationOption>(); selectList.add(new ConfigurationOption("Auto Take Ref Maps", "1")); selectList
		 * .add(new ConfigurationOption("Do not Auto Take Ref Maps", "2")); selectList.add(new
		 * ConfigurationOption("Prompt User During Procedure", "3")); configElement = new
		 * ConfigurationElement("Take Ref Beam Automatically", "autoRefBeam", ConfigurationElement.ELEMENT_TYPE_SELECTLIST, 0, selectList);
		 * procedureAdvancedConfigurationList.add(configElement);
		 */

	}

	public PassiveTiltDef getPassiveTiltDef() {
		return passiveTiltDef;
	}

	public void setPassiveTiltDef(PassiveTiltDef passiveTiltDef) {
		this.passiveTiltDef = passiveTiltDef;
	}

	public List<String> getFrameList() {
		return frameList;
	}

	public void setFrameList(List<String> frameList) {
		this.frameList = frameList;
	}

	public String getProcedureStatus() {
		String status = "<pre>" + "Camera is not properly initialized. " + "\nProceed with caution. " + "\n "
				+ "\nCurrent frame being used for test " + "\nCalling Find and Identify " + "\nPreparing to read REF_DEF File. "
				+ "\nCompressing UFS spots using file: " + "\npcs_data/config/spot_flag_ufs_sp19.dat "
				+ "\nNo. of interior spots expected =  217.000 " + "\nn_subap               =    372 "
				+ "\nSegment No.           =     19 " + "\nOld PCS threshold          =    1024.96 "
				+ "\nThresh, frame avg., sigma  =     786.98    544.73     48.45 " + "\nPEAKER threshold, no. subimages = 786.976    399 "
				+ "\nNo. of peaks before/after cut =    399    399 " + "\npeak_max              =    3648.81 "
				+ "\nIter., u, v, delta_u        =   0    0.31416    0.00000    0.00785 "
				+ "\nIter., u, v, delta_u        =   1    0.29060    0.00000    71251.8 "
				+ "\nIter., u, v, delta_u        =   2    0.29256    0.00000   108982.6 "
				+ "\nIter., u, v, delta_u        =   4    0.29354   -0.00025   114914.3 "
				+ "\nu_theor, v_theor            =        0.29354   -0.00025 "
				+ "\nIter., u, v, pmax, pmax_th  =   1    0.29354   -0.00025    67253.9      138384 "
				+ "\nIter., u, v, pmax, pmax_th  =   2    0.29551   -0.00025    86530.1      138384 "
				+ "\nIter., u, v, pmax, pmax_th  =   3    0.29551    0.00025    86627.1      138384 "
				+ "\nIter., u, v, pmax, pmax_th  =   4    0.29538    0.00000    86761.4      138384 " + "\nBeginning MATCH_FINE ... "
				+ "\nOffset corr., No. Peaks =    -5.4954   14.1835  109. " + "\nOffset corr., No. Peaks =    -0.2987    1.8624  298. "
				+ "\nOffset corr., No. Peaks =    -0.0168   -0.0403  298. " + "\nNull IDs              =     66 "
				+ "\nSingle IDs            =    298 " + "\nDouble IDs            =      8 " + "\nMultiple IDs          =      0 "
				+ "\nCumulative Offsets    =    -5.8108   16.0056 " + "\nResidual Offsets      =     0.0067    0.0067 "
				+ "\nBeginning MATCH_COARSE_FAST ... " + "\nIncremental Offsets   =     0.0000  -74.0000 "
				+ "\nCumulative Offsets    =    -5.8108  -57.9944 " + "\nEstimated No. Peaks   =    340 "
				+ "\nNumber of Solutions   =      1 " + "\nBest Spiral Search Index =  17 " + "\nBeginning MATCH_FINAL ... "
				+ "\nNull IDs              =     25 " + "\nSingle IDs            =    332 " + "\nDouble IDs            =     15 "
				+ "\nMultiple IDs          =      0 " + "\nCumulative Offsets    =    -5.6994  -55.8016 "
				+ "\nChange This Pass      =     0.1114    2.1928 " + "\nNo. Interior Spots    =    217 "
				+ "\nMax. Peak             =     3648.8 " + "\nUncompressing UFS spot list for segment 19 "
				+ "\n>>> Calculating center of image " + "\nAll centroids found and identified. " + "\nCalculating Centroid Residuals "
				+ "\nRigid body rotation is -0.129E-03 Rads " + "\nThe telescope needs to be moved " + "\n  0.28 arc sec. in AZ. "
				+ "\n -0.19 arc sec. in EL. " + "\nCalculating Zernike Coefficients " + "\n Zenike fitting errors: "
				+ "\n chi squared   =  156.0304 " + "\n fitting error = 0.6023797 " + "\n diam80        =  1.865283 "
				+ "\nUltra Fine Screen Test Completed " + "\n " + "\nplog file name is K1_11AUG06_0000_UFS-19_111.FPLOG " + "</pre>";

		return status;
	}

	public String doNewPassiveTilt() {

		passiveTiltDef = new PassiveTiltDef();
		procedureWizardBean.reset();

		sessionController.setInPassiveTilt(true);

		// perform setup for new...
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a z");

		breadcrumbMenuBean.addItem("Passive Tilt - " + sdf.format(date), "newProcedure.xhtml");

		return "/modules/passiveTilt/passiveTilt.xhtml?faces-redirect=true";
	}

	public String doCancelProcedure() {

		return "/modules/sessionList.xhtml?faces-redirect=true";
	}


	public void doExecuteProcedure(ActionEvent actionEvent) {

		// TODO: maybe this should be a bean that backs the menu bar
		sessionController.setProcedureExecuting(true);

		// validate inputs
		// KECK: warn user and let them use abort, but don't make anyone answer a validation question on the fly
		if (passiveTiltDef.getFilter() != 611) {

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage("Off Nominal Configuration!  Filter is normally 611 for Passive Tilt!"));

		}

		// kick off asynchronous procedure
		passiveTiltMgmt.executeProcedure(passiveTiltDef);

	}

	public void doSaveAdvancedOptions() {

	}

	public void doCancelSaveAdvancedOptions() {

	}

	public void doSaveExecutionPreferences() {

	}

	public void doCancelSaveExecutionPreferences() {

	}

}
