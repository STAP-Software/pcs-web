/**
 * @author Scott Michaels
 * Copyright (C) 2015 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.visualization.ui;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.FloatListEncoder;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.FloatPointListEncoder;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.procedure.ui.ProcedureController;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

@Named
@SessionScoped
public class VisualizationTools implements Serializable {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	CentroidMapMgmt centroidMapMgmt;

	@Inject
	ProcedureController procedureController;

	String centDefPassiveTiltXs;
	String centDefPassiveTiltYs;

	List<FloatPoint> refDefValueListPassiveTilt;

	@PostConstruct
	private void init() {

		try {
			// TODO: do other queries as each new procedure type is added
			RefBeamMap refDefMapPassiveTilt = centroidMapMgmt.getRefBeamDefMap(PupilMaskType.PUPIL_MASK_TYPE_ID_36);
			List<FloatPoint> refDefValueListPassiveTilt = refDefMapPassiveTilt.getCentroidMap().getValues();
			float[] xArray = FloatPointListEncoder.extractXArray(refDefValueListPassiveTilt);
			centDefPassiveTiltXs = FloatListEncoder.encodeList(xArray);
			float[] yArray = FloatPointListEncoder.extractYArray(refDefValueListPassiveTilt);
			centDefPassiveTiltYs = FloatListEncoder.encodeList(yArray);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCentDefXs() {
		// determine which procedure type we are in
		if (procedureController.getProcedureType() != null) {
			if (procedureController.getProcedureType().isPassiveTilt()) {
				return centDefPassiveTiltXs;
			}
		}
		return centDefPassiveTiltXs; // default
	}

	public void setCentDefXs(String str) {
	}

	public String getCentDefYs() {
		// determine which procedure type we are in
		if (procedureController.getProcedureType() != null) {
			if (procedureController.getProcedureType().isPassiveTilt()) {
				return centDefPassiveTiltYs;
			}
		}
		return centDefPassiveTiltXs; // default
	}

	public void setCentDefYs(String str) {
	}

}