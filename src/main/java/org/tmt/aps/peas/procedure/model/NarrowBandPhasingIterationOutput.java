package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.BbAnalyzeFrameResult;
import org.tmt.aps.peas.computation.model.CalcPrCommandsResult;
import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.CentroidOffsetsResult;
import org.tmt.aps.peas.computation.model.ColorStepToActuatorsResult;
import org.tmt.aps.peas.computation.model.FindCentroidsResult;
import org.tmt.aps.peas.computation.model.PupilRegErrorResult;

/**
 * Procedure output data for a single step during the Phasing procedure
 * @author smichaels
 *
 */
public class NarrowBandPhasingIterationOutput extends ProcedureIterationOutput  {


	CenterTelescopeCalcResult centerTelescopeCalcResult;
	CentroidOffsetsResult centroidOffsetsResult;
	PupilRegErrorResult pupilRegErrorResult;
	CalcPrCommandsResult calcPrCommandsResult;
	FindCentroidsResult findCentroidsResult;
	
	BbAnalyzeFrameResult bbAnalyzeFrameResult;
	ColorStepToActuatorsResult colorStepToActuatorsResult;
	
	
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
	public BbAnalyzeFrameResult getBbAnalyzeFrameResult() {
		return bbAnalyzeFrameResult;
	}
	public void setBbAnalyzeFrameResult(BbAnalyzeFrameResult bbAnalyzeFrameResult) {
		this.bbAnalyzeFrameResult = bbAnalyzeFrameResult;
	}
	public ColorStepToActuatorsResult getColorStepToActuatorsResult() {
		return colorStepToActuatorsResult;
	}
	public void setColorStepToActuatorsResult(ColorStepToActuatorsResult colorStepToActuatorsResult) {
		this.colorStepToActuatorsResult = colorStepToActuatorsResult;
	}
	
	
	
}
