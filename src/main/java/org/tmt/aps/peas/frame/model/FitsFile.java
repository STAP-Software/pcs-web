package org.tmt.aps.peas.frame.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class FitsFile {

	private static SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy");

	int telescope;
	Date date;
	String procedureTypeCd;
	int procedureNumber;
	int ufsSegment;
	int sufsGroup;
	int iteration;
	int phasingStep;  // A-K = 1-11 for phasing

	public FitsFile(String fitsFileName) {

		try {

			StringTokenizer st = new StringTokenizer(fitsFileName, "_");

			String telescopeStr = st.nextToken();
			telescope = new Integer(telescopeStr.substring(1));

			date = sdf.parse(st.nextToken());
			
			procedureTypeCd = st.nextToken();
			
			if (procedureTypeCd.startsWith("UFS")) {
				ufsSegment = new Integer(procedureTypeCd.substring(4));
				procedureTypeCd = procedureTypeCd.substring(0, 3);
			} else if (procedureTypeCd.startsWith("SUFS")) {
				sufsGroup = new Integer(procedureTypeCd.substring(5));
				procedureTypeCd = procedureTypeCd.substring(0, 4);
			}
			
			procedureNumber = new Integer(st.nextToken());
			
			String sequenceCd = st.nextToken();
			
			if (procedureTypeCd.startsWith("CPH")) {
				iteration = new Integer(sequenceCd.substring(0, 1));
				// transform A-K to 1-11
				phasingStep = (int)sequenceCd.charAt(1) - (int)'A' + 1; // A-K
			} else {
				iteration = new Integer(sequenceCd.substring(0,2));
			}
			

		} catch (Exception e) {

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

	public int getProcedureNumber() {
		return procedureNumber;
	}

	public void setProcedureNumber(int procedureNumber) {
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
	
	public String getFileName() {
		
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
		buf.append(String.format("%03d", procedureNumber) + "_");
		if (procedureTypeCd.startsWith("CPH")) {
			buf.append(iteration);			
			buf.append((char)(phasingStep + 'A' - 1));
		} else {
			buf.append(String.format("%02d", iteration));			
		}
		buf.append(".FTS");
		return buf.toString();
		
	}
	
	
	public String toString() {
		return getFileName();
	}
}
