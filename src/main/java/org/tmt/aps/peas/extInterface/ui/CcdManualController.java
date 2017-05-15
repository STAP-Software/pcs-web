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
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Utils;
import org.tmt.aps.peas.extInterface.business.CameraMgmt;
import org.tmt.aps.peas.extInterface.business.CameraPoller;
import org.tmt.aps.peas.extInterface.business.CcdMgmt;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.Gain;
import org.tmt.aps.peas.frame.ui.FrameController;
import org.tmt.aps.peas.instrument.model.CcdState;

/**
 * JSF Controller class for PCS CCD manual/diagnostic user interface.
 * @author smichaels
 */
@Named
@SessionScoped
public class CcdManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	@EJB
	CcdMgmt ccdMgmt;
	@EJB
	CameraMgmt cameraMgmt;
	@EJB
	CameraPoller cameraPoller;

	@Inject
	FrameController frameController;
	
	int commandSelection;

	int gainNumber;
	int channelOffset;
	int channelOffset1;
	int channelOffset2;
	double exposureTime;
	int channel;
	boolean cmdExecuted;
	int[] offsetCalibration;

	CcdState ccdState;
	
	@PostConstruct
	public void init() {
		ccdState = new CcdState();
	}
	
	public int getCommandSelection() {
		return commandSelection;
	}

	public void setCommandSelection(int commandSelection) {
		this.commandSelection = commandSelection;
	}

	public int getGainNumber() {
		return gainNumber;
	}

	public void setGainNumber(int gainNumber) {
		this.gainNumber = gainNumber;
	}

	public int getChannelOffset() {
		return channelOffset;
	}

	public void setChannelOffset(int channelOffset) {
		this.channelOffset = channelOffset;
	}

	public int getChannelOffset1() {
		return channelOffset1;
	}

	public void setChannelOffset1(int channelOffset1) {
		this.channelOffset1 = channelOffset1;
	}

	public int getChannelOffset2() {
		return channelOffset2;
	}

	public void setChannelOffset2(int channelOffset2) {
		this.channelOffset2 = channelOffset2;
	}

	public double getExposureTime() {
		return exposureTime;
	}

	public void setExposureTime(double exposureTime) {
		this.exposureTime = exposureTime;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	public boolean isCmdExecuted() {
		return cmdExecuted;
	}

	public void setCmdExecuted(boolean cmdExecuted) {
		this.cmdExecuted = cmdExecuted;
	}

	public int[] getOffsetCalibration() {
		return offsetCalibration;
	}

	public void setOffsetCalibration(int[] offsetCalibration) {
		this.offsetCalibration = offsetCalibration;
	}

	public CcdState getCcdState() {
		return ccdState;
	}

	public void setCcdState(CcdState ccdState) {
		this.ccdState = ccdState;
	}

	/**
	 * JSF Action method to render the PCS CCD manual/diagnostic user interface
	 * @return the JSF page to render
	 */
	public String doViewCcdDiagnostic() {

		commandSelection = 1;
		
		breadcrumbMenuBean.addFirstItem("CCD Diagnostic", "/modules/diagnostic/ccdDiagnostic.xhtml");

		return "/modules/diagnostic/ccdDiagnostic.xhtml?faces-redirect=true";
	}

	/**
	 * JSF Action method called when the user clicks on the 'Send Command' button
	 */
	public void doSendCommand() {
		try {

			String commandType = null;
			RequestContext requestContext = RequestContext.getCurrentInstance();
			
			switch (commandSelection) {

			case 1: // Take exposure
				int[][] frame = ccdMgmt.getImage(exposureTime);
				short[][] rawFrame = new short[frame.length][frame[0].length];
				for (int i=0; i< frame.length; i++) {
					StringBuffer buf = new StringBuffer();
					for (int j=0; j<frame[i].length; j++) {
						rawFrame[i][j] = (short)frame[j][i];
						if (rawFrame[i][j] > 400) {
							buf.append("[" + rawFrame[i][j] + "]");
						}
					}
					if (buf.length() > 0) {
					//logger.debug(buf);
					}
				}
				frameController.setupFrameToolFrameDisplay(rawFrame);
				
				requestContext.update("frameDisplayForm:framePanel");
				requestContext.execute("drawFrame()");
				
				commandType = "Take Exposure";
				break;

			case 2: // Take Overscanned Exposure
				int[][] overscanFrame = ccdMgmt.getOverscannedImage(exposureTime);
				short[][] overscanRawFrame = new short[overscanFrame.length][overscanFrame[0].length];
				for (int i=0; i< overscanFrame.length; i++) {
					StringBuffer buf = new StringBuffer();
					for (int j=0; j<overscanFrame[i].length; j++) {
						overscanRawFrame[i][j] = (short)overscanFrame[j][i];
						if (overscanRawFrame[i][j] > 400) {
							buf.append("[" + overscanRawFrame[i][j] + "]");
						}
					}
					if (buf.length() > 0) {
					//logger.debug(buf);
					}
				}
				frameController.setupFrameToolFrameDisplay(overscanRawFrame);
				requestContext.update("frameDisplayForm:framePanel");
				requestContext.execute("drawFrame()");
				
				
				commandType = "Take Overscanned Exposure";
				break;

			case 3: // Set Gain
				
				ccdMgmt.setGain(gainNumber);
				
				commandType = "Set Gain = " + gainNumber;
				break;

			case 4: // Trigger Offset Calibration
				
				offsetCalibration = ccdMgmt.triggerOffsetCalibration();
				
				commandType = "Trigger OffsetCalibration";
				break;

			case 5: // Set one channel offset
				
				ccdMgmt.setOffset(channel, channelOffset);
				
				commandType = "Set One Channel Offset";
				break;

			case 6: // Set both channel offsets
				
				int[] channelOffsets = {channelOffset1, channelOffset2};
				
				ccdMgmt.setOffset(channelOffsets);
				
				commandType = "Set Both Channel Offsets";
				break;


			default:

			}

			cmdExecuted = true;
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage(commandType));
		
			refresh();
			
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}
	

	public void updateCommandListener() {
		cmdExecuted = false;
	}
	
	/**
	 * JSF Action method called when the user clicks on the 'Refresh' button
	 */
	public void doRefresh() {
		try {

			refresh();
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandSuccessfulMessage("Refresh"));
			
		} catch (CommandFailureException e) {
			
			FacesContext.getCurrentInstance().addMessage(null, Utils.commandFailedMessage(e));
			logger.error(MessageGenerator.generateMessage("command.failure"), e);
			
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, Utils.genericErrorMessage(e));
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}
	
	private void refresh() throws CommandFailureException, Exception {
		
		Gain gain = ccdMgmt.getGain();
		int[] offsets = ccdMgmt.getOffset();
		int[] imageSize = ccdMgmt.getImageSize();
		int[] overscannedImageSize = ccdMgmt.getOverscannedImageSize();
		
		ccdState = new CcdState(gain, offsets, imageSize, overscannedImageSize);

	}

	/**
	 * JSF Action method called when the user clicks the 'Cancel' button
	 */
	public String doCancel() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}


}
