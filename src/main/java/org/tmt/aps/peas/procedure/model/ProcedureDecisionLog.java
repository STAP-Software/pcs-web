package org.tmt.aps.peas.procedure.model;

public class ProcedureDecisionLog {

	private boolean m1CmdsSent;
	private boolean m2CmdsSent;
	
	private int m1SnapNumberAfter = -1;
	private int m1SnapNumberBefore = -1;

	private boolean telescopeMoved;
	
	public boolean isM1CmdsSent() {
		return m1CmdsSent;
	}

	public void setM1CmdsSent(boolean m1CmdsSent) {
		this.m1CmdsSent = m1CmdsSent;
	}

	public boolean isM2CmdsSent() {
		return m2CmdsSent;
	}

	public void setM2CmdsSent(boolean m2CmdsSent) {
		this.m2CmdsSent = m2CmdsSent;
	}

	public int getM1SnapNumberAfter() {
		return m1SnapNumberAfter;
	}

	public void setM1SnapNumberAfter(int m1SnapNumberAfter) {
		this.m1SnapNumberAfter = m1SnapNumberAfter;
	}

	public int getM1SnapNumberBefore() {
		return m1SnapNumberBefore;
	}

	public void setM1SnapNumberBefore(int m1SnapNumberBefore) {
		this.m1SnapNumberBefore = m1SnapNumberBefore;
	}

	public boolean isTelescopeMoved() {
		return telescopeMoved;
	}

	public void setTelescopeMoved(boolean telescopeMoved) {
		this.telescopeMoved = telescopeMoved;
	}

	
	
	
}
