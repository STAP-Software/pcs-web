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
import org.tmt.aps.peas.extInterface.business.DcsMgmt;
import org.tmt.aps.peas.extinf.DcsCommand;
import org.tmt.aps.peas.extinf.StarInfo;

@Named
@SessionScoped
public class DcsManualController implements Serializable {

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
	

	@PostConstruct
	public void init() {
		telescopePosition[0] = 0.345f;
		telescopePosition[1] = 4.111f;
		
		m2Pos[0] = 0.111f;
		m2Pos[1] = 1.232f;
		m2Pos[2] = 7.022f;
	}


	public int getDcsStatus() {
		return dcsStatus;
	}

	public void setDcsStatus(int dcsStatus) {
		this.dcsStatus = dcsStatus;
	}

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

	public String doViewDcsManualInterface() {

		breadcrumbMenuBean.addFirstItem("DCS Manual Interface", "doViewDcsManualInterface()");

		return "/modules/diagnostic/dcsManualInterface.xhtml?faces-redirect=true";

	}
	
	public void doQueryTelescopePosition() {
		try {
			telescopePosition = dcsMgmt.queryTelescopePosition();
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying telescope position"));
		}
	}

	public void doQueryStar() {
		try {
			starInfo = dcsMgmt.queryStar();
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying star info"));
		}
	}

	public void doQueryDcsStatus() {
		try {
			dcsStatus = dcsMgmt.queryDcsStatus();
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying telescope position"));
		}
	}
	
	public void doQuerySecondary() {
		try {
			m2Pos = dcsMgmt.querySecondary();
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error querying secondary position"));
		}
	}
	
	public void doQueryAll() {
		doQueryTelescopePosition();
		doQueryStar();
		doQueryDcsStatus();
		doQuerySecondary();
	}
	

	
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
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Telescope Delta Send Successful"));
			logger.info("doCommandTelescopeDelta: success");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error sending telescope deltas"));
		}
		// remember to clear the list when done
		doQueryAll();
	}
	
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
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Secondary Delta Send Successful"));
			logger.info("doCommandSecondaryDelta: success");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error sending secondary deltas"));
		}
		// remember to clear the list when done
		doQueryAll();
	}
}


