package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.computation.model.FixPistonsResult;
import org.tmt.aps.peas.visualization.model.EdgeHeightsDisplayValues;

public class PhasingProcedureOutput extends ProcedureOutput implements EdgeHeightsDisplayValues {

	BbAnalyzeSequenceResult bbAnalyzeSequenceResult;
	FixPistonsResult fixPistonsResult;

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

	
}
