package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.visualization.model.EdgeHeightsDisplayValues;

public class PhasingProcedureOutput extends ProcedureOutput implements EdgeHeightsDisplayValues {

	BbAnalyzeSequenceResult bbAnalyzeSequenceResult;

	public BbAnalyzeSequenceResult getBbAnalyzeSequenceResult() {
		return bbAnalyzeSequenceResult;
	}

	public void setBbAnalyzeSequenceResult(BbAnalyzeSequenceResult bbAnalyzeSequenceResult) {
		this.bbAnalyzeSequenceResult = bbAnalyzeSequenceResult;
	}
	

	

	
}
