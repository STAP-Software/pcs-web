/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.tmt.aps.peas.procedure.model.Procedure;
import org.tmt.aps.peas.refBeamMap.model.CentroidMap;
import org.tmt.aps.peas.session.model.FrameFieldDisplay;

/**
 * Entity class representing the ProcedureCcdFrame database table.  The class also contains the <code>@Transient</code> frameFieldDisplayList used to display frame
 * data in the reporting interface.
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcedureCcdFrame")
@NamedQueries({
    @NamedQuery(
        name = "findAllFramesForProcedure",
        query = "SELECT pcf FROM ProcedureCcdFrame pcf " +
                "INNER JOIN FETCH pcf.ccdFrame " +
                "INNER JOIN FETCH pcf.procedure " +
                "LEFT OUTER JOIN FETCH pcf.centroidMap " +
                "WHERE pcf.procedure.procedureId = :procedureId"
    ),
    @NamedQuery(
        name = "findProcedureCcdFramesShallow",
        query = "SELECT pcf FROM ProcedureCcdFrame pcf " +
                "INNER JOIN FETCH pcf.ccdFrame " +
                "INNER JOIN FETCH pcf.ccdFrame.cameraState " +
                "INNER JOIN pcf.procedure " +
                "WHERE pcf.procedure.procedureId = :procedureId"
    )
})

public class ProcedureCcdFrame {
	
	@Id
	@SequenceGenerator(
		    name = "procedureCcdFrame_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
	)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "procedureCcdFrame_gen"
	)
	private Long procedureCcdFrameId;

	private boolean newFrameFlg;
	
	private int procedureFrameNumber;
	private int procedureIterationNumber;
	private Integer phasingStepNumber;
	private Integer phasingFilterNumber;
	

	@OneToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "ccdFrameId")
	private CcdFrame ccdFrame;

	@OneToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureId")
	private Procedure procedure;

	// a centroid map can be generated more than once for a frame (frame from file used in more than one procedure), 
	// but only once for a frame within a procedure, which is why it appears in this join table
	@OneToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "centroidMapId")
	private CentroidMap centroidMap;
	
	
	public Long getProcedureCcdFrameId() {
		return procedureCcdFrameId;
	}

	public void setProcedureCcdFrameId(Long procedureCcdFrameId) {
		this.procedureCcdFrameId = procedureCcdFrameId;
	}


	public boolean isNewFrameFlg() {
		return newFrameFlg;
	}

	public void setNewFrameFlg(boolean newFrameFlg) {
		this.newFrameFlg = newFrameFlg;
	}

	public CcdFrame getCcdFrame() {
		return ccdFrame;
	}

	public void setCcdFrame(CcdFrame ccdFrame) {
		this.ccdFrame = ccdFrame;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public int getProcedureFrameNumber() {
		return procedureFrameNumber;
	}

	public void setProcedureFrameNumber(int procedureFrameNumber) {
		this.procedureFrameNumber = procedureFrameNumber;
	}

	public int getProcedureIterationNumber() {
		return procedureIterationNumber;
	}

	public void setProcedureIterationNumber(int procedureIterationNumber) {
		this.procedureIterationNumber = procedureIterationNumber;
	}

	public Integer getPhasingStepNumber() {
		return phasingStepNumber;
	}

	public void setPhasingStepNumber(Integer phasingStepNumber) {
		this.phasingStepNumber = phasingStepNumber;
	}

	public Integer getPhasingFilterNumber() {
		return phasingFilterNumber;
	}

	public void setPhasingFilterNumber(Integer phasingFilterNumber) {
		this.phasingFilterNumber = phasingFilterNumber;
	}

	public CentroidMap getCentroidMap() {
		return centroidMap;
	}

	public void setCentroidMap(CentroidMap centroidMap) {
		this.centroidMap = centroidMap;
	}

	@Transient
	List<FrameFieldDisplay> frameFieldDisplayList;


	public List<FrameFieldDisplay> getFrameFieldDisplayList() {
		return frameFieldDisplayList;
	}

	public void setFrameFieldDisplayList(List<FrameFieldDisplay> frameFieldDisplayList) {
		this.frameFieldDisplayList = frameFieldDisplayList;
	}

	public float getMedianPeakIntensityAdu() {
		if (centroidMap != null && ccdFrame != null && centroidMap.getMedianPeakIntensity() != null) {
			return centroidMap.getMedianPeakIntensity() * ccdFrame.getCcdGainValue();
		} else {
			return 0.0f;
		}
	}

	
}
