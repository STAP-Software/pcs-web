/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

public class FitsFilename {

	Logger logger = Logger.getLogger(this.getClass());
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy");

	int telescope;
	Date date;
	String procedureTypeCd;
	String procedureNumber;
	int ufsSegment;
	int sufsGroup;
	int iteration;
	int phasingStep;  // A-K = 1-11 for phasing
	String fileName;

	public FitsFilename(Long telescopeId, String procedureTypeCd, String procedureNumber, int iteration,
			int ufsSegment, int sufsGroup, int phasingStep) {
		
		this.telescope = (int)telescopeId.longValue();
		this.date = new Date();
		this.procedureTypeCd = procedureTypeCd;
		this.procedureNumber = procedureNumber;
		this.iteration = iteration;
		this.ufsSegment = ufsSegment;
		this.sufsGroup = sufsGroup;
		this.phasingStep = phasingStep;
		
		this.fileName = generateFileName();
	}
	
	// non-phasing, non-SUFS constructor
	public FitsFilename(Long telescopeId, String procedureTypeCd, String procedureNumber, int iteration) {
		
		this.telescope = (int)telescopeId.longValue();
		this.date = new Date();
		this.procedureTypeCd = procedureTypeCd;
		this.procedureNumber = procedureNumber;
		this.iteration = iteration;
		this.ufsSegment = 0;
		this.sufsGroup = 0;
		this.phasingStep = 0;
		
		this.fileName = generateFileName();
	}
	
	// constructor for ad-hoc files
	public FitsFilename(Long telescopeId, PupilMaskType pupilMaskType, int iteration) {
		
		this.telescope = (int)telescopeId.longValue();
		this.date = new Date();
		this.procedureTypeCd = pupilMaskType.getPupilMaskTypeName();
		this.procedureNumber = "0";
		this.iteration = iteration;
		this.ufsSegment = 0;
		this.sufsGroup = 0;
		this.phasingStep = 0;
		
		this.fileName = generateFileName();
	}

	
	public FitsFilename(String fitsFileName) {

		try {

			this.fileName = fitsFileName;
			
			StringTokenizer st = new StringTokenizer(fitsFileName, "_");

			String telescopeStr = st.nextToken();
			telescope = new Integer(telescopeStr.substring(1));

			String dateString = st.nextToken();
			date = sdf.parse(dateString);
			
			//logger.debug("dateString = " + dateString + ", date = " + date);
			
			procedureTypeCd = st.nextToken();
			
			if (procedureTypeCd.startsWith("UFS")) {
				ufsSegment = new Integer(procedureTypeCd.substring(4));
				procedureTypeCd = procedureTypeCd.substring(0, 3);
			} else if (procedureTypeCd.startsWith("SUFS")) {
				sufsGroup = new Integer(procedureTypeCd.substring(5));
				procedureTypeCd = procedureTypeCd.substring(0, 4);
			}
			
			procedureNumber = st.nextToken();
			
			String sequenceCd = st.nextToken();
			
			if (procedureTypeCd.startsWith("CPH")) {
				iteration = new Integer(sequenceCd.substring(0, 1));
				// transform A-K to 1-11
				phasingStep = (int)sequenceCd.charAt(1) - (int)'A' + 1; // A-K
			} else {
				iteration = new Integer(sequenceCd.substring(0,2));
			}
			

		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}

	}

	public int getTelescope() {
		return telescope;
	}

	public void setTelescope(int telescope) {
		this.telescope = telescope;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getProcedureNumber() {
		return procedureNumber;
	}

	public void setProcedureNumber(String procedureNumber) {
		this.procedureNumber = procedureNumber;
	}

	public String getProcedureTypeCd() {
		return procedureTypeCd;
	}

	public void setProcedureTypeCd(String procedureTypeCd) {
		this.procedureTypeCd = procedureTypeCd;
	}

	public int getUfsSegment() {
		return ufsSegment;
	}

	public void setUfsSegment(int ufsSegment) {
		this.ufsSegment = ufsSegment;
	}

	public int getSufsGroup() {
		return sufsGroup;
	}

	public void setSufsGroup(int sufsGroup) {
		this.sufsGroup = sufsGroup;
	}

	public int getIteration() {
		return iteration;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public int getPhasingStep() {
		return phasingStep;
	}

	public void setPhasingStep(int phasingStep) {
		this.phasingStep = phasingStep;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getProcedureName() {
		if (procedureTypeCd == "PR") {
			return "Pupil Registration";
		} else if (procedureTypeCd == "RB") {
			return "Reference Beam";
		} else if (procedureTypeCd == "CPH") {
			return "Coarse Phasing";
		} else if (procedureTypeCd == "FS-B") {
			return "Fine Screen";
		} else if (procedureTypeCd == "CT") {
			return "Center Telescope";
		} else {
			return procedureTypeCd;
		}
	}
	
	
	public String generateFileName() {
		
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		StringBuffer buf = new StringBuffer();
		buf.append("K" + telescope + "_");
		buf.append(sdf.format(date).toUpperCase() + "_");
		if (procedureTypeCd.startsWith("UFS")) {
			buf.append(procedureTypeCd + "-" + String.format("%02d", ufsSegment) + "_");
		} else if (procedureTypeCd.startsWith("SUFS")) {
			buf.append(procedureTypeCd + "-" + String.format("%02d", sufsGroup) + "_");
		} else {
			buf.append(procedureTypeCd + "_");
		}
		buf.append(String.format("%s", procedureNumber) + "_");
		if (procedureTypeCd.startsWith("CPH")) {
			buf.append(iteration);			
			buf.append((char)(phasingStep + 'A' - 1));
		} else {
			buf.append(String.format("%02d", iteration));			
		}
		buf.append(".FTS");
		return buf.toString();
		
	}

	
	
	public boolean isInSamePhasingSequence(FitsFilename candidate) {
		return candidate.getTelescope() == getTelescope() &&	
			candidate.getDate().equals(getDate()) &&
			candidate.getProcedureNumber().equals(getProcedureNumber()) &&
			candidate.getProcedureTypeCd().equals(getProcedureTypeCd()) &&
			candidate.getIteration() == getIteration();
	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FitsFilename) {
			FitsFilename candidate = (FitsFilename)obj;
			return candidate.getFileName().equals(this.getFileName());
		} 
		return false;
	}


	public String toString() {
		return getFileName();
	}
}
