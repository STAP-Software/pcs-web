/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.extInterface.ui;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.DcsCommand;
import org.tmt.aps.peas.extinf.StarInfo;
import org.tmt.aps.peas.extinf.TimeoutException;

/**
 * JSF Controller class for ACS manual/diagnostic user interface.
 * @author smichaels
 */
@Named
@SessionScoped
public class DcsManualController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7807048207847494968L;

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	PeasProperties peasProperties;
	@EJB
	DcsMgmt dcsMgmt;
	
	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;


	int dcsStatus = 2;
	StarInfo starInfo = new StarInfo("SomeName", 6.3f, "blue");
	double[] telescopePosition = new double[2];
	double[] m2Pos = new double[3];
	Double[] telescopeDeltaCmds = new Double[2];
	Double[] secondaryDeltaCmds = new Double[3];
	double[] telescopeFocusAndTilt = new double[3];
	

	@PostConstruct
	public void init() {
		telescopePosition[0] = 0.345f;
		telescopePosition[1] = 4.111f;
		
		m2Pos[0] = 0.111f;
		m2Pos[1] = 1.232f;
		m2Pos[2] = 7.022f;
		
		telescopeFocusAndTilt[0] = 1.1;
		telescopeFocusAndTilt[1] = 22.1;
		telescopeFocusAndTilt[2] = 331.2;
		
		telescopeDeltaCmds[0] = Double.valueOf(0.0f);
		telescopeDeltaCmds[1] = Double.valueOf(0.0f);
		
		secondaryDeltaCmds[0] = Double.valueOf(0.0f);
		secondaryDeltaCmds[1] = Double.valueOf(0.0f);
		secondaryDeltaCmds[2] = Double.valueOf(0.0f);
	}


	public int getDcsStatus() {
		return dcsStatus;
	}

	public void setDcsStatus(int dcsStatus) {
		this.dcsStatus = dcsStatus;
	}

	/**
	 * @return DCS status string 
	 */
	public String getDcsStatusDisplayString() {
		switch (dcsStatus) {
		case DcsCommand.DCS_STATUS_OFF: return "Off";
		case DcsCommand.DCS_STATUS_INIT: return "Init";
		case DcsCommand.DCS_STATUS_STANDBY: return "Standby";
		case DcsCommand.DCS_STATUS_RUN: return "Run";
		case DcsCommand.DCS_STATUS_ESTOP: return "EStop";
		case DcsCommand.DCS_STATUS_MCP: return "MCP";
		case DcsCommand.DCS_STATUS_HALTED: return "Halted";
		case DcsCommand.DCS_STATUS_DISABLED: return "Disabled";
		default: return "Unknown";
		}
	}
	
	public StarInfo getStarInfo() {
		return starInfo;
	}

	public void setStarInfo(StarInfo starInfo) {
		this.starInfo = starInfo;
	}

	public double[] getTelescopePosition() {
		return telescopePosition;
	}

	public void setTelescopePosition(double[] telescopePosition) {
		this.telescopePosition = telescopePosition;
	}

	public double[] getM2Pos() {
		return m2Pos;
	}

	public void setM2Pos(double[] m2Pos) {
		this.m2Pos = m2Pos;
	}

	public double[] getTelescopeFocusAndTilt() {
		return telescopeFocusAndTilt;
	}


	public void setTelescopeFocusAndTilt(double[] telescopeFocusAndTilt) {
		this.telescopeFocusAndTilt = telescopeFocusAndTilt;
	}


	public Double[] getTelescopeDeltaCmds() {
		return telescopeDeltaCmds;
	}

	public void setTelescopeDeltaCmds(Double[] telescopeDeltaCmds) {
		this.telescopeDeltaCmds = telescopeDeltaCmds;
	}

	public Double[] getSecondaryDeltaCmds() {
		return secondaryDeltaCmds;
	}

	public void setSecondaryDeltaCmds(Double[] secondaryDeltaCmds) {
		this.secondaryDeltaCmds = secondaryDeltaCmds;
	}

	/**
	 * JSF Action method to render the DCS manual/diagnostic user interface
	 * @return the JSF page to render
	 */
	public String doViewDcsManualInterface() {

		breadcrumbMenuBean.addFirstItem("DCS Manual Interface", "/modules/diagnostic/dcsManualInterface.xhtml");

		return "/modules/diagnostic/dcsManualInterface.xhtml?faces-redirect=true";

	}
	/**
	 * JSF Action method to query the telescope position
	 */
	public void doQueryTelescopePosition() {
		try {
			telescopePosition = dcsMgmt.queryTelescopePosition();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query Telescope Position"));
			
		} catch (CommandFailureException e) {
				
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying telescope position"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying telescope position"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}			
	}

	/**
	 * JSF Action method to query the telescope position
	 */
	public void doQueryM2FocusAndTilt() {
		try {
			telescopeFocusAndTilt = dcsMgmt.queryM2FocusAndTilts();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query M2 Focus/Tilts"));
			
		} catch (CommandFailureException e) {
				
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying M2 focus/tilts"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying M2 focus/tiltsn"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}			
	}

	/**
	 * JSF Action method to query current star information
	 */
	public void doQueryStar() {
		try {
			starInfo = dcsMgmt.queryStar();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query Star"));
			
		} catch (CommandFailureException e) {
				
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying star info"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying star info"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}						
	}

	/**
	 * JSF Action method to query the DCS status
	 */
	public void doQueryDcsStatus() {
		try {
			dcsStatus = dcsMgmt.queryDcsStatus();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query Dcs Status"));
			
		} catch (CommandFailureException e) {
				
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying telescope position"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying telescope position"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}						
	}
	
	/**
	 * JSF Action method to query the secondary position
	 */
	public void doQuerySecondary() {
		try {
			m2Pos = dcsMgmt.querySecondary();
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query Secondary"));
			
		} catch (CommandFailureException e) {
				
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying secondary position"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying secondary position"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}						
	}
	
	/**
	 * JSF Action method to command all DCS queries
	 */
	public void doQueryAll() {
		
		try {
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Query All"));
			queryAll();
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error querying secondary position"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error querying secondary position"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}						
	}
	
	private void queryAll() throws Exception {
		telescopePosition = dcsMgmt.queryTelescopePosition();
		starInfo = dcsMgmt.queryStar();
		dcsStatus = dcsMgmt.queryDcsStatus();
		m2Pos = dcsMgmt.querySecondary();
		telescopeFocusAndTilt = dcsMgmt.queryM2FocusAndTilts();

	}

	/**
	 * JSF Action method to command delta telescope moves
	 */
	public void doCommandTelescopeDelta() {
		
		try {
			double[] deltaCmds = new double[2];
			logger.info("doCommandTelescopeDelta: deltaCmds = ");
			for (int i=0; i<2; i++) {
				logger.info(telescopeDeltaCmds[i]);
				deltaCmds[i] = telescopeDeltaCmds[i].floatValue();
			}
			// send out the commands
			dcsMgmt.commandTelescopeDeltas(deltaCmds);
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Telescope Deltas"));
			logger.info("doCommandTelescopeDelta: success");
			// remember to clear the list when done
			queryAll();
			
		} catch (CommandFailureException e) {
				
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error sending telescope deltas"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error sending telescope deltas"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}						

	}
	
	/**
	 * JSF Action method to command secondary deltas
	 */
	public void doCommandSecondary() {
		try {
			double[] deltaCmds = new double[3];
			logger.info("doCommandSecondaryDelta: secondaryDeltaCmds = ");
			for (int i=0; i<3; i++) {
				logger.info(secondaryDeltaCmds[i]);
				deltaCmds[i] = secondaryDeltaCmds[i].floatValue();
			}
			// send out the commands
			dcsMgmt.commandSecondaryDeltas(deltaCmds);
			
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Secondary Deltas"));
			logger.info("doCommandTelescopeDelta: success");
			// remember to clear the list when done
			queryAll();
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e, "Error sending secondary deltas"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (TimeoutException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Secondard delta timeout exception"));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
				
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e, "Error sending secondary deltas"));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}						

	}
}


