/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.ui;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.procedure.model.ProcedureConfig;
import org.tmt.aps.peas.procedure.model.ProcedureType;

@Named
@SessionScoped
public class AcsManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;
	@EJB
	AcsMgmt acsCommand;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	Double actDeltas[][] = new Double[36][3];
	int snapshotNumber;
	double mirrorTemp;
	boolean acsRunning;
	double rmsActuatorMove;

	@PostConstruct
	public void init() {

		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				//actDeltas[i][j] = (double) ((i + 1) * 10 + j + 1);
				actDeltas[i][j] = (double) (0.0);
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

	public Double[][] getActDeltas() {
		return actDeltas;
	}

	public void setActDeltas(Double[][] actDeltas) {
		this.actDeltas = actDeltas;
	}

	public String doViewAcsManualInterface() {

		breadcrumbMenuBean.addFirstItem("ACS Manual Interface", "doViewAcsManualInterface()");

		return "/modules/diagnostic/acsManualInterface.xhtml?faces-redirect=true";

	}

	public void doSendActDeltaCommands() {

		// interface requires that we use indexes 1-108
		double[] actDeltaCmds = new double[109];

		System.out.println(actDeltaCmds + "::" + actDeltas);
		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltaCmds[1 + i * 3 + j] = actDeltas[i][j];
			}
		}

		try {

			// send out the commands
			acsCommand.commandActuatorDelta(actDeltaCmds);

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error sending actuator deltas"));
		}

		// remember to clear the list when done
		init();
	}

	public void doLoadSnapshot() {

		try {
			acsCommand.commandLoadSnap(snapshotNumber);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Snapshot Load Successful"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error loading snapshot"));
		}

	}

	public void doTakeSnapshot() {

		try {
			snapshotNumber = acsCommand.commandTakeSnap();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Snapshot Successful"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error taking snapshot"));
		}

	}

	public void doQueryMirrorTemp() {
		try {
			mirrorTemp = acsCommand.queryMirrorTemp();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Query Successful"));
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying mirror temp"));
		}
	}

	public void doQueryRunning() {
		try {
			acsRunning = acsCommand.queryRunning();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Query Successful"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying is running"));
		}
	}

	public void doQueryRmsActMove() {
		try {
			rmsActuatorMove = acsCommand.queryRmsActuMove();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Query Successful"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying RMS act move"));
		}
	}

	public void doQueryAll() {
		try {
			mirrorTemp = acsCommand.queryMirrorTemp();
			rmsActuatorMove = acsCommand.queryRmsActuMove();
			acsRunning = acsCommand.queryRunning();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Query Successful"));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying acs"));
		}
	}

}
