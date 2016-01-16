package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.FixPistonsResult;
import org.tmt.aps.peas.computation.model.PhasingStatsResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.EdgeHeightsDisplayValues;

public class PhasingProcedureOutput extends ProcedureOutput implements EdgeHeightsDisplayValues, ActuatorDeltasDisplayValues {

	BbAnalyzeSequenceResult bbAnalyzeSequenceResult;
	FixPistonsResult fixPistonsResult;
	PhasingStatsResult phasingStatsResult;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;

	public BbAnalyzeSequenceResult getBbAnalyzeSequenceResult() {
		return bbAnalyzeSequenceResult;
	}

	public void setBbAnalyzeSequenceResult(BbAnalyzeSequenceResult bbAnalyzeSequenceResult) {
		this.bbAnalyzeSequenceResult = bbAnalyzeSequenceResult;
	}

	public FixPistonsResult getFixPistonsResult() {
		return fixPistonsResult;
	}

	public void setFixPistonsResult(FixPistonsResult fixPistonsResult) {
		this.fixPistonsResult = fixPistonsResult;
		calcDesiredActCommandsResult = new CalcDesiredActCommandsResult(fixPistonsResult);
	}

	public PhasingStatsResult getPhasingStatsResult() {
		return phasingStatsResult;
	}

	public void setPhasingStatsResult(PhasingStatsResult phasingStatsResult) {
		this.phasingStatsResult = phasingStatsResult;
	}

	// actuator deltas display values

	public float[][] getDesiredActDeltas() {
		return calcDesiredActCommandsResult.getDesiredActDeltas();
	}
	public float getDesiredActDeltasRms() {
		return calcDesiredActCommandsResult.getDesiredActDeltasRms();
	}


	
}
