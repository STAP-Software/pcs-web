package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.MakeTemplateResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeFrameResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeStepSequenceResult;
import org.tmt.aps.peas.computation.model.PhasingStatsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;
import org.tmt.aps.peas.visualization.model.EdgeHeightsDisplayValues;

/**
 * Procedure output data for a single step during the Phasing procedure
 * @author smichaels
 *
 */
public class NarrowBandPhasingIterationOutput extends ProcedureIterationOutput implements EdgeHeightsDisplayValues  {


	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	PupilRegErrorResult pupilRegErrorResult;
	CalcPrCommandsResult calcPrCommandsResult;
	FindCentroidsResult findCentroidsResult;
	
	MakeTemplateResult makeTemplateResult;
	NbAnalyzeFrameResult nbAnalyzeFrameResult;
	NbAnalyzeStepSequenceResult nbAnalyzeStepSequenceResult;
	
	
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

	// Edge Heights Display Values implementation methods
	
	// EdgeHeights Display Values
	
	@Override
	public float[] getStepCorr() {
		
		// FIXME: is this correct?
		return nbAnalyzeFrameResult.getCoherenceOut();
		
	}
	@Override
	public float[] getResid() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getRowFlagOut() {
		return nbAnalyzeStepSequenceResult.getRowFlagOut();
	}
	
	@Override
	public int getGoodEdgeCount() {
		
		return 0;
	}
	@Override
	public float getEdgeErrorMax() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setEdgeErrorMax(float edgeErrorMax) {
		// TODO Auto-generated method stub
		
	}
	public void setResidualEdgeErrorMax(float residualEdgeErrorMax) {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public float getEdgeErrorRss() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public float getResidualEdgeErrorMax() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public float getResidualEdgeErrorRss() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	

	
	
}
