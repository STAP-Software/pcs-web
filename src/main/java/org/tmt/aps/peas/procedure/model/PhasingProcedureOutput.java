package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.computation.model.FixPistonsResult;
import org.tmt.aps.peas.computation.model.PhasingStatsResult;
import org.tmt.aps.peas.visualization.model.EdgeHeightsDisplayValues;

public class PhasingProcedureOutput extends ProcedureOutput implements EdgeHeightsDisplayValues {

	BbAnalyzeSequenceResult bbAnalyzeSequenceResult;
	FixPistonsResult fixPistonsResult;
	PhasingStatsResult phasingStatsResult;

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
	}

	public PhasingStatsResult getPhasingStatsResult() {
		return phasingStatsResult;
	}

	public void setPhasingStatsResult(PhasingStatsResult phasingStatsResult) {
		this.phasingStatsResult = phasingStatsResult;
	}

	
}
