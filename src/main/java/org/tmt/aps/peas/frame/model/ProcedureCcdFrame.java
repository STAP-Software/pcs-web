package org.tmt.aps.peas.frame.model;

import javax.persistence.Column;
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
import javax.persistence.Transient;

import org.tmt.aps.peas.procedure.model.ProcedureContext;

@Entity
@Table(name = "ProcedureCcdFrame")
@NamedQueries({
	@NamedQuery(name = "findAllFramesForProcedure", query = "SELECT p from ProcedureCcdFrame p INNER JOIN FETCH p.ccdFrame where p.procedureId = :procedureId" )
})
public class ProcedureCcdFrame {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long procedureCcdFrameId;

	private Long procedureId;
	
	private boolean newFrameFlg;
	
	private int frameNumber;

	@OneToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "ccdFrameId")
	private CcdFrame ccdFrame;

	
	
	public Long getProcedureCcdFrameId() {
		return procedureCcdFrameId;
	}

	public void setProcedureCcdFrameId(Long procedureCcdFrameId) {
		this.procedureCcdFrameId = procedureCcdFrameId;
	}

	public Long getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(Long procedureId) {
		this.procedureId = procedureId;
	}

	public boolean isNewFrameFlg() {
		return newFrameFlg;
	}

	public void setNewFrameFlg(boolean newFrameFlg) {
		this.newFrameFlg = newFrameFlg;
	}

	public int getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}

	public CcdFrame getCcdFrame() {
		return ccdFrame;
	}

	public void setCcdFrame(CcdFrame ccdFrame) {
		this.ccdFrame = ccdFrame;
	}

	
}
