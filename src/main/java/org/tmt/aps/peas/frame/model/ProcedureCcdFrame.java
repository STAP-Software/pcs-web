/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.frame.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.tmt.aps.peas.procedure.model.Procedure;

@Entity
@Table(name = "ProcedureCcdFrame")
@NamedQueries({
	@NamedQuery(name = "findAllFramesForProcedure", query = "SELECT pcf from ProcedureCcdFrame pcf "
			+ "INNER JOIN FETCH pcf.ccdFrame INNER JOIN FETCH pcf.procedure p where p.procedureId = :procedureId" )
})
public class ProcedureCcdFrame {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long procedureCcdFrameId;

	private boolean newFrameFlg;
	
	private int procedureFrameNumber;
	private int procedureIterationNumber;
	private Integer phasingStepNumber;
	

	@OneToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "ccdFrameId")
	private CcdFrame ccdFrame;

	@OneToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureId")
	private Procedure procedure;

	
	
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


	
}
