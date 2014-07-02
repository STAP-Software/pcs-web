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
import org.primefaces.context.RequestContext;
import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.extInterface.business.CcdMgmt;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.extinf.CommunicationException;
import org.tmt.aps.peas.frame.ui.FrameController;

@Named
@SessionScoped
public class CcdManualController implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	@EJB
	CcdMgmt ccdMgmt;

	@Inject
	FrameController frameController;
	
	int commandSelection;
	int integrationTime;
	
	int advCommandSelection;
	int channel;
	double gain;
	double offset;
	int binning[];
	double exposureTime;
	boolean useShutter;
	
	double plateScale;
	int imageSize[];

	
	@PostConstruct
	public void init() {
		imageSize = new int[2];
		imageSize[0] = 1024;
		imageSize[1] = 1024;
		binning = new int[2];
		binning[0] = 1;
		binning[1] = 1;
		advCommandSelection = 1;
	}
	
	public int getCommandSelection() {
		return commandSelection;
	}

	public void setCommandSelection(int commandSelection) {
		this.commandSelection = commandSelection;
	}

	public int getAdvCommandSelection() {
		return advCommandSelection;
	}

	public void setAdvCommandSelection(int advCommandSelection) {
		this.advCommandSelection = advCommandSelection;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public int[] getBinning() {
		return binning;
	}

	public void setBinning(int[] binning) {
		this.binning = binning;
	}

	public double getExposureTime() {
		return exposureTime;
	}

	public void setExposureTime(double exposureTime) {
		this.exposureTime = exposureTime;
	}

	public boolean isUseShutter() {
		return useShutter;
	}

	public void setUseShutter(boolean useShutter) {
		this.useShutter = useShutter;
	}

	public double getPlateScale() {
		return plateScale;
	}

	public void setPlateScale(double plateScale) {
		this.plateScale = plateScale;
	}

	public int[] getImageSize() {
		return imageSize;
	}

	public void setImageSize(int[] imageSize) {
		this.imageSize = imageSize;
	}

	public String doViewCcdDiagnostic() {

		commandSelection = 1;
		
		breadcrumbMenuBean.addFirstItem("CCD Diagnostic", "doViewCcdDiagnostic()");

		return "/modules/diagnostic/ccdDiagnostic.xhtml?faces-redirect=true";
	}

	public void doSendCommand() {
		try {

			switch (commandSelection) {

			case 1: // FastWipe
				ccdMgmt.fastWipeCcd();
				break;

			case 2: // Continuous Wipe on
				ccdMgmt.wipeOn();
				break;

			case 3: // Read CCD Raw
				int[][] frame = ccdMgmt.getImage();
				short[][] rawFrame = new short[frame.length][frame[0].length];
				for (int i=0; i< frame.length; i++) {
					StringBuffer buf = new StringBuffer();
					for (int j=0; j<frame[i].length; j++) {
						rawFrame[i][j] = (short)frame[i][j];
						if (rawFrame[i][j] > 400) {
							buf.append("[" + rawFrame[i][j] + "]");
						}
					}
					if (buf.length() > 0) {
					logger.debug(buf);
					}
				}
				frameController.setupFrameToolFrameDisplay(rawFrame);
				
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.update("frameViewerForm:framePanel");
				requestContext.execute("drawFrame()");
				
				break;


			default:

			}

			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0"));


		} catch (CommandFailureException e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Command Failure Exception: failure code = " + e.getFailureCode() , e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error:", e.getMessage()));

		}

	}
	public void doSendAdvCommand() {
		try {

			switch (advCommandSelection) {

			case 1: // Set Gain
				ccdMgmt.setGain(channel, gain);
				break;

			case 2: // Set Offset
				ccdMgmt.setOffset(channel, offset);
				break;

			case 3: // Set Binning
				ccdMgmt.setBinning(binning[0], binning[1]);
				break;

			case 4: // Get Image
				int[][] frame = ccdMgmt.getImage(exposureTime, useShutter);
				short[][] rawFrame = new short[frame.length][frame[0].length];
				for (int i=0; i< frame.length; i++) {
					for (int j=0; j<frame[i].length; j++) {
						rawFrame[i][j] = (short)frame[i][j];
					}
				}
				frameController.setupFrameToolFrameDisplay(rawFrame);
				RequestContext requestContext = RequestContext.getCurrentInstance();
				requestContext.update("frameViewerForm:framePanel");
				requestContext.execute("drawFrame()");
				
				
				break;

			default:

			}

			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0"));


		} catch (CommandFailureException e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Command Failure Exception: failure code = " + e.getFailureCode() , e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error:", e.getMessage()));

		}

	}
	
	public void doRefresh() {
		try {

			imageSize[0] = ccdMgmt.getImageWidth();
			imageSize[1] = ccdMgmt.getImageHeight();
			plateScale = ccdMgmt.getPlateScale();
			
			FacesContext context = FacesContext.getCurrentInstance();

			context.addMessage(null, new FacesMessage("Successful", "Command response = 0x0"));

		} catch (CommandFailureException e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Command Failure Exception: failure code = " + e.getFailureCode() , e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();

			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Error:", e.getMessage()));

		}

	}

	public String doCancel() {

		return "/modules/sessionDetail.xhtml?faces-redirect=true";
	}


}
