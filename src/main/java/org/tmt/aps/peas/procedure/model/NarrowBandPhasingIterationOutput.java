package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.CoherenceAnalyzerResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.MakeTemplateResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeFrameResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeStepSequenceResult;
import org.tmt.aps.peas.computation.model.PhasingStatsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.instrument.model.Filter;
import org.tmt.aps.peas.visualization.model.SingleFilterEdgeHeightsDisplayValues;

/**
 * Procedure output data for a single step during the Phasing procedure
 * @author smichaels
 *
 */
public class NarrowBandPhasingIterationOutput extends ProcedureIterationOutput implements SingleFilterEdgeHeightsDisplayValues  {


	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	PupilRegErrorResult pupilRegErrorResult;
	CalcPrCommandsResult calcPrCommandsResult;
	FindCentroidsResult findCentroidsResult;
	
	MakeTemplateResult makeTemplateResult;
	NbAnalyzeFrameResult nbAnalyzeFrameResult;
	NbAnalyzeStepSequenceResult nbAnalyzeStepSequenceResult;
	PhasingStatsResult phasingStatsResult;
	CoherenceAnalyzerResult coherenceAnalyzerResult;
	
	public CenterTelescopeCalcResult getCenterTelescopeCalcResult() {
		return centerTelescopeCalcResult;
	}
	public void setCenterTelescopeCalcResult(CenterTelescopeCalcResult centerTelescopeCalcResult) {
		this.centerTelescopeCalcResult = centerTelescopeCalcResult;
	}
	public CentroidOffsetsResult getCentroidOffsetsResult() {
		return centroidOffsetsResult;
	}
	public void setCentroidOffsetsResult(CentroidOffsetsResult centroidOffsetsResult) {
		this.centroidOffsetsResult = centroidOffsetsResult;
	}
	public PupilRegErrorResult getPupilRegErrorResult() {
		return pupilRegErrorResult;
	}
	public void setPupilRegErrorResult(PupilRegErrorResult pupilRegErrorResult) {
		this.pupilRegErrorResult = pupilRegErrorResult;
	}
	public CalcPrCommandsResult getCalcPrCommandsResult() {
		return calcPrCommandsResult;
	}
	public void setCalcPrCommandsResult(CalcPrCommandsResult calcPrCommandsResult) {
		this.calcPrCommandsResult = calcPrCommandsResult;
	}
	public FindCentroidsResult getFindCentroidsResult() {
		return findCentroidsResult;
	}
	public void setFindCentroidsResult(FindCentroidsResult findCentroidsResult) {
		this.findCentroidsResult = findCentroidsResult;
	}
	public MakeTemplateResult getMakeTemplateResult() {
		return makeTemplateResult;
	}
	public void setMakeTemplateResult(MakeTemplateResult makeTemplateResult) {
		this.makeTemplateResult = makeTemplateResult;
	}
	public NbAnalyzeFrameResult getNbAnalyzeFrameResult() {
		return nbAnalyzeFrameResult;
	}
	public void setNbAnalyzeFrameResult(NbAnalyzeFrameResult nbAnalyzeFrameResult) {
		this.nbAnalyzeFrameResult = nbAnalyzeFrameResult;
	}
	public NbAnalyzeStepSequenceResult getNbAnalyzeStepSequenceResult() {
		return nbAnalyzeStepSequenceResult;
	}
	public void setNbAnalyzeStepSequenceResult(NbAnalyzeStepSequenceResult nbAnalyzeStepSequenceResult) {
		this.nbAnalyzeStepSequenceResult = nbAnalyzeStepSequenceResult;
	}

	public PhasingStatsResult getPhasingStatsResult() {
		return phasingStatsResult;
	}
	public void setPhasingStatsResult(PhasingStatsResult phasingStatsResult) {
		this.phasingStatsResult = phasingStatsResult;
	}
	public CoherenceAnalyzerResult getCoherenceAnalyzerResult() {
		return coherenceAnalyzerResult;
	}
	public void setCoherenceAnalyzerResult(CoherenceAnalyzerResult coherenceAnalyzerResult) {
		this.coherenceAnalyzerResult = coherenceAnalyzerResult;
	}

	// Edge Heights Display Values implementation methods
	
	// EdgeHeights Display Values
	

	

	public float[] getStepCorr() {
		
		return nbAnalyzeStepSequenceResult.getStepTable();
	}
	
	public float[] getResid() {
		return new float[84];
	}

	public int[] getRowFlagOut() {
		return nbAnalyzeStepSequenceResult.getRowFlagOut();
	}
	
	public int getGoodEdgeCount() {
		return phasingStatsResult.getGoodEdgeCount();
	}

	public float getEdgeErrorMax() {
		return phasingStatsResult.getEdgeErrorMax();
	}

	public void setEdgeErrorMax(float edgeErrorMax) {
		// TODO Auto-generated method stub
		
	}
	public void setResidualEdgeErrorMax(float residualEdgeErrorMax) {
		// TODO Auto-generated method stub	
	}

	public float getEdgeErrorRss() {
		return phasingStatsResult.getEdgeErrorRss();
	}

	public float getResidualEdgeErrorMax() {
		return phasingStatsResult.getResidualEdgeErrorMax();
	}

	public float getResidualEdgeErrorRss() {
		return phasingStatsResult.getResidualEdgeErrorRss();
	}


	
	
	

	
	
}
