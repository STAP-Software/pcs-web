package org.tmt.aps.peas.procedure.business;

import java.awt.Point;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.camera.business.CameraMgmt;
import org.tmt.aps.peas.common.fortran.FortranProxy;
import org.tmt.aps.peas.frame.business.FrameMgmt;
import org.tmt.aps.peas.frame.business.ImageProcessor;
import org.tmt.aps.peas.frame.business.PupilRegistrator;
import org.tmt.aps.peas.frame.model.ImageFrame;
import org.tmt.aps.peas.frame.model.RegistrationDelta;
import org.tmt.aps.peas.passiveTilt.model.PassiveTiltConfig;
import org.tmt.aps.peas.statusLog.business.StatusLogMgmt;
import org.tmt.aps.peas.visualization.business.GraphicDisplayMgmt;
import org.tmt.aps.peas.visualization.business.UserPromptMgmt;

@Singleton
public class ProcedureExecutionMgmt {

	private boolean executionStatus;
	private int percentComplete;

	public boolean getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(boolean executionStatus) {
		this.executionStatus = executionStatus;
	}

	public int getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(int percentComplete) {
		this.percentComplete = percentComplete;
	}


}
