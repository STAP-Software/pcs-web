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
import org.tmt.aps.peas.extInterface.business.AcsMgmt;

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

	Integer actDeltas[][] = new Integer[36][3];
	int snapshotNumber;
	double mirrorTemp;
	boolean acsRunning;
	double rmsActuatorMove;

	@PostConstruct
	public void init() {

		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltas[i][j] = 0;
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

	public Integer[][] getActDeltas() {
		return actDeltas;
	}

	public void setActDeltas(Integer[][] actDeltas) {
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
					Double temp = new Double(line);
					actDeltas[i][j] = temp.intValue();
				}
			}

			FacesMessage msg = new FacesMessage("values uploaded successfully");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public String doViewAcsManualInterface() {

		breadcrumbMenuBean.addFirstItem("ACS Manual Interface", "doViewAcsManualInterface()");

		return "/modules/diagnostic/acsManualInterface.xhtml?faces-redirect=true";

	}

	public void doNothing() {
		
	}
	
	public void doClear() {
		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltas[i][j] = 0;
			}
		}
	}
	
	
	public void doSendActDeltaCommands() {

		// interface requires that we use indexes 1-108
		double[] actDeltaCmds = new double[109];

		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltaCmds[1 + i * 3 + j] = actDeltas[i][j];
			}
		}

		try {
			logger.info("doSendActDeltaCommands: actDeltaCmds = ");
			for (int i=0; i<109; i++) {
				logger.info(actDeltaCmds[i]);
			}
			// send out the commands
			acsMgmt.commandActuatorDelta(actDeltaCmds);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Actuator Delta Send Successful"));
			logger.info("doSendActDeltaCommands: success");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error sending actuator deltas"));
		}
		// remember to clear the list when done
		init();
	}

	public void doLoadSnapshot() {

		try {
			logger.info("doLoadSnapshot: snapshotNumber = " + snapshotNumber);

			acsMgmt.commandLoadSnap(snapshotNumber);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Snapshot Load Successful"));
			logger.info("doLoadSnapshot: success");

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error loading snapshot"));
		}

	}

	public void doTakeSnapshot() {

		try {
			logger.info("doTakeSnapshot: ");
			snapshotNumber = acsMgmt.commandTakeSnap();
			logger.info("doTakeSnapshot successful, snapshot number = " + snapshotNumber);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Snapshot Successful"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error taking snapshot"));
		}

	}

	public void doQueryMirrorTemp() {
		try {
			mirrorTemp = acsMgmt.queryMirrorTemp();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Query Successful"));
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying mirror temp"));
		}
	}

	public void doQueryRunning() {
		try {
			acsRunning = acsMgmt.queryRunning();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Query Successful"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying is running"));
		}
	}

	public void doQueryRmsActMove() {
		try {
			rmsActuatorMove = acsMgmt.queryRmsActuMove();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Query Successful"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying RMS act move"));
		}
	}

	public void doQueryAll() {
		try {
			mirrorTemp = acsMgmt.queryMirrorTemp();
			rmsActuatorMove = acsMgmt.queryRmsActuMove();
			acsRunning = acsMgmt.queryRunning();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Query Successful"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying acs"));
		}
	}

}
