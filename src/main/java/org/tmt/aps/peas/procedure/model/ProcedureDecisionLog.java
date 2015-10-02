package org.tmt.aps.peas.procedure.model;

public class ProcedureDecisionLog {

	private boolean m1CmdsSent;
	
	private int m1SnapNumberAfter = -1;

	private boolean telescopeMoved;
	
	public boolean isM1CmdsSent() {
		return m1CmdsSent;
	}

	public void setM1CmdsSent(boolean m1CmdsSent) {
		this.m1CmdsSent = m1CmdsSent;
	}

	public int getM1SnapNumberAfter() {
		return m1SnapNumberAfter;
	}

	public void setM1SnapNumberAfter(int m1SnapNumberAfter) {
		this.m1SnapNumberAfter = m1SnapNumberAfter;
	}

	public boolean isTelescopeMoved() {
		return telescopeMoved;
	}

	public void setTelescopeMoved(boolean telescopeMoved) {
		this.telescopeMoved = telescopeMoved;
	}

	
	
	
}
