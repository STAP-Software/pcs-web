package org.tmt.aps.peas.passiveTilt.model;

public class PassiveTiltExecutionPreferences {

	private boolean autoDisplayCentriods;
	private boolean autoDisplayCentriodOffsets;
	private boolean autoDisplayAvgCentriodOffsets;
	private boolean autoDisplayActuatorDeltas;
	private boolean autoDisplayProcedureDataLog;
	private boolean autoSaveFrames;

	public boolean isAutoDisplayCentriods() {
		return autoDisplayCentriods;
	}

	public void setAutoDisplayCentriods(boolean autoDisplayCentriods) {
		this.autoDisplayCentriods = autoDisplayCentriods;
	}

	public boolean isAutoDisplayCentriodOffsets() {
		return autoDisplayCentriodOffsets;
	}

	public void setAutoDisplayCentriodOffsets(boolean autoDisplayCentriodOffsets) {
		this.autoDisplayCentriodOffsets = autoDisplayCentriodOffsets;
	}

	public boolean isAutoDisplayAvgCentriodOffsets() {
		return autoDisplayAvgCentriodOffsets;
	}

	public void setAutoDisplayAvgCentriodOffsets(
			boolean autoDisplayAvgCentriodOffsets) {
		this.autoDisplayAvgCentriodOffsets = autoDisplayAvgCentriodOffsets;
	}

	public boolean isAutoDisplayActuatorDeltas() {
		return autoDisplayActuatorDeltas;
	}

	public void setAutoDisplayActuatorDeltas(boolean autoDisplayActuatorDeltas) {
		this.autoDisplayActuatorDeltas = autoDisplayActuatorDeltas;
	}

	public boolean isAutoDisplayProcedureDataLog() {
		return autoDisplayProcedureDataLog;
	}

	public void setAutoDisplayProcedureDataLog(
			boolean autoDisplayProcedureDataLog) {
		this.autoDisplayProcedureDataLog = autoDisplayProcedureDataLog;
	}

	public boolean isAutoSaveFrames() {
		return autoSaveFrames;
	}

	public void setAutoSaveFrames(boolean autoSaveFrames) {
		this.autoSaveFrames = autoSaveFrames;
	}

}
