/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extinf.CommandFailureException;

@Named
@SessionScoped
public class AcsManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;
	@EJB
	AcsMgmt acsMgmt;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	Float actDeltas[][] = new Float[36][3];
	int snapshotNumber;
	double mirrorTemp;
	boolean acsRunning;
	double rmsActuatorMove;

	@PostConstruct
	public void init() {

		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltas[i][j] = 0.0f;
			}
		}

	}

	public int getSnapshotNumber() {
		return snapshotNumber;
	}

	public void setSnapshotNumber(int snapshotNumber) {
		this.snapshotNumber = snapshotNumber;
	}

	public double getMirrorTemp() {
		return mirrorTemp;
	}

	public void setMirrorTemp(double mirrorTemp) {
		this.mirrorTemp = mirrorTemp;
	}

	public boolean isAcsRunning() {
		return acsRunning;
	}

	public void setAcsRunning(boolean acsRunning) {
		this.acsRunning = acsRunning;
	}

	public double getRmsActuatorMove() {
		return rmsActuatorMove;
	}

	public void setRmsActuatorMove(double rmsActuatorMove) {
		this.rmsActuatorMove = rmsActuatorMove;
	}

	
	public Float[][] getActDeltas() {
		return actDeltas;
	}

	public void setActDeltas(Float[][] actDeltas) {
		this.actDeltas = actDeltas;
	}

	public void handleFileUpload(FileUploadEvent event) {

		BufferedReader br = null;
		try {
			UploadedFile file = event.getFile();
			br = new BufferedReader(new InputStreamReader(file.getInputstream()));

			for (int i = 0; i < 36; i++) {
				for (int j = 0; j < 3; j++) {
					String line = br.readLine();
					if (line == null) break;
					Float temp = new Float(line);
					actDeltas[i][j] = temp;
				}
			}

			FacesMessage msg = new FacesMessage("values uploaded successfully");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e2));
				logger.error(MessageGenerator.generateMessage("generic.error"), e2);
			}
		}
	}

	public String doViewAcsManualInterface() {

		breadcrumbMenuBean.addFirstItem("ACS Manual Interface", "/modules/diagnostic/acsManualInterface.xhtml");

		return "/modules/diagnostic/acsManualInterface.xhtml?faces-redirect=true";

	}

	public void doNothing() {
		
	}
	
	public void doClear() {
		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltas[i][j] = 0.0f;
			}
		}
	}
	
	
	public void doSendActDeltaCommands() {

		try {
			// send out the commands
			acsMgmt.commandActuatorDeltas(actDeltas);
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Actuator Delta Send"));
			
		} catch (CommandFailureException e) {
				
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error sending actuator deltas"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error sending actuator deltas"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	
	
		// remember to clear the list when done
		init();
	}

	public void doLoadSnapshot() {

		try {
			logger.info("doLoadSnapshot: snapshotNumber = " + snapshotNumber);

			acsMgmt.commandLoadSnap(snapshotNumber);
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Snapshot Load"));
			logger.info("doLoadSnapshot: success");
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error loading snapshot"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error loading snapshot"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}


	}

	public void doTakeSnapshot() {

		try {
			logger.info("doTakeSnapshot: ");
			snapshotNumber = acsMgmt.commandTakeSnap();
			logger.info("doTakeSnapshot successful, snapshot number = " + snapshotNumber);
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Snapshot"));
			logger.info("doLoadSnapshot: success");
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error taking snapshot"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error taking snapshot"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	public void doQueryMirrorTemp() {
		try {
			mirrorTemp = acsMgmt.queryMirrorTemp();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query Mirror Temp"));
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying mirror temp"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying mirror temp"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}

	public void doQueryRunning() {
		try {
			acsRunning = acsMgmt.queryRunning();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query Running"));
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying is running"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying is running"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}

	public void doQueryRmsActMove() {
		try {
			rmsActuatorMove = acsMgmt.queryRmsActuMove();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query RmsActMove"));
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying RMS act move"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying RMS act move"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	public void doQueryAll() {
		try {
			mirrorTemp = acsMgmt.queryMirrorTemp();
			rmsActuatorMove = acsMgmt.queryRmsActuMove();
			acsRunning = acsMgmt.queryRunning();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query All"));
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying ACS"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying ACS"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

}
