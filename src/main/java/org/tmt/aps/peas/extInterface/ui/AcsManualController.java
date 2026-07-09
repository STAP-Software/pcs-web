/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extInterface.business.AcsMgmt;
import org.tmt.aps.peas.extinf.CommandFailureException;

/**
 * JSF Controller class for ACS manual/diagnostic user interface.
 * @author smichaels
 *
 */
@Named
@SessionScoped
public class AcsManualController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6811190837797833367L;

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
	double sensorRange;
	UploadedFile uploadFile;

	@PostConstruct
	public void init() {
		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltas[i][j] = 0.0f;
			}
		}

	}
	
	public UploadedFile getUploadFile() {
		return uploadFile;
	}
    
	public void setUploadFile(UploadedFile uploadFile) {
        this.uploadFile = uploadFile;
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
	
	public double getSensorRange() {
		return sensorRange;
	}

	public void setSensorRange(double sensorRange) {
		this.sensorRange = sensorRange;
	}

	public Float[][] getActDeltas() {
		return actDeltas;
	}

	public void setActDeltas(Float[][] actDeltas) {
		this.actDeltas = actDeltas;
	}

	/**
	 * Handles file update of actuator deltas files
	 */
	public void handleFileUpload(FileUploadEvent event) {
		
	    UploadedFile file = event.getFile();
	    if (file == null || file.getSize() == 0) {
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage(FacesMessage.SEVERITY_WARN, "No file selected", null));
	        return;
	    }
	    BufferedReader br = null;
	    try {
	        br = new BufferedReader(new InputStreamReader(file.getInputStream()));
	        for (int i = 0; i < 36; i++) {
	            for (int j = 0; j < 3; j++) {
	                String line = br.readLine();
	                if (line == null) break;
	                // Strip UTF-8 BOM if present
	                line = line.replace("\uFEFF", "");
	                actDeltas[i][j] = Float.valueOf(line.trim());
	            }
	        }
	        FacesContext.getCurrentInstance().addMessage(null,
	            new FacesMessage("Values uploaded successfully from: " + file.getFileName()));
	    } catch (Exception e) {
	        FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
	        logger.error(MessageGenerator.generateMessage("generic.error"), e);
	    } finally {
	        if (br != null) {
	            try { br.close(); } catch (Exception e2) {
	                logger.error(MessageGenerator.generateMessage("generic.error"), e2);
	            }
	        }
	    }
	}
	
	
	/**
	 * JSF Action method to render the ACS manual/diagnostic view
	 * @return the JSF page to render
	 */
	public String doViewAcsManualInterface() {

		breadcrumbMenuBean.addFirstItem("ACS Manual Interface", "/modules/diagnostic/acsManualInterface.xhtml");

		return "/modules/diagnostic/acsManualInterface.xhtml?faces-redirect=true";

	}

	/**
	 * JSF Action method that does nothing
	 */
	public void doNothing() {
		
	}
	
	/**
	 * JSF Action method to clear the actuator deltas
	 */
	public void doClear() {
		for (int i = 0; i < 36; i++) {
			for (int j = 0; j < 3; j++) {
				actDeltas[i][j] = 0.0f;
			}
		}
	}
	
	/**
	 * JSF Action method that sends the actuator commands
	 */
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

	/**
	 * JSF Action method that loads the ACS snapshot
	 */
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

	/**
	 * JSF Action methos that takes an ACS snaphot
	 */
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

	/**
	 * JSF Action method that sends commands to query the mirror temperature
	 */
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

	/**
	 * JSF Action method that sends commands to query if ACS is running
	 */
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

	/**
	 * JSF Action method that sends commands to query the RMS actuator move
	 */
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

	/**
	 * JSF Action method that sends commands to query the sensor range
	 */
	public void doQuerySensorRange() {
		try {
			sensorRange = acsMgmt.querySensorRange();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query Sensor Range"));
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying sensor range"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying sensor range"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	/**
	 * JSF Action method that sends all ACS query commands
	 */
	public void doQueryAll() {
		try {
			mirrorTemp = acsMgmt.queryMirrorTemp();
			rmsActuatorMove = acsMgmt.queryRmsActuMove();
			acsRunning = acsMgmt.queryRunning();
			sensorRange = acsMgmt.querySensorRange();
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
