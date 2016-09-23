package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.FixPistonsResult;
import org.tmt.aps.peas.computation.model.MakeTemplateResult;
import org.tmt.aps.peas.computation.model.NbActuatorsResult;
import org.tmt.aps.peas.computation.model.NbAnalyzeFilterSequenceResult;
import org.tmt.aps.peas.computation.model.PhasingStatsResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.EdgeHeightsDisplayValues;

/**
 * Procedure output data (not including step specific data) for Phasing procedure
 * @author smichaels
 *
 */
public class NarrowBandPhasingProcedureOutput extends ProcedureOutput implements EdgeHeightsDisplayValues, ActuatorDeltasDisplayValues {


	MakeTemplateResult makeTemplateResult;
	NbAnalyzeFilterSequenceResult nbAnalyzeFilterSequenceResult;
	NbActuatorsResult nbActuatorsResult;
	FixPistonsResult fixPistonsResult;
	PhasingStatsResult phasingStatsResult;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;
	
	
	public MakeTemplateResult getMakeTemplateResult() {
		return makeTemplateResult;
	}
	public void setMakeTemplateResult(MakeTemplateResult makeTemplateResult) {
		this.makeTemplateResult = makeTemplateResult;
	}
	public NbAnalyzeFilterSequenceResult getNbAnalyzeFilterSequenceResult() {
		return nbAnalyzeFilterSequenceResult;
	}
	public void setNbAnalyzeFilterSequenceResult(NbAnalyzeFilterSequenceResult nbAnalyzeFilterSequenceResult) {
		this.nbAnalyzeFilterSequenceResult = nbAnalyzeFilterSequenceResult;
	}
	public NbActuatorsResult getNbActuatorsResult() {
		return nbActuatorsResult;
	}
	public void setNbActuatorsResult(NbActuatorsResult nbActuatorsResult) {
		this.nbActuatorsResult = nbActuatorsResult;
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
	public CalcDesiredActCommandsResult getCalcDesiredActCommandsResult() {
		return calcDesiredActCommandsResult;
	}
	public void setCalcDesiredActCommandsResult(CalcDesiredActCommandsResult calcDesiredActCommandsResult) {
		this.calcDesiredActCommandsResult = calcDesiredActCommandsResult;
	}
	@Override
	public float[][] getDesiredActDeltas() {
		
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public float getDesiredActDeltasRms() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	// EdgeHeights Display Values
	
	@Override
	public float[] getStepCorr() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public float[] getResid() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int[] getRowFlagOut() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getGoodEdgeCount() {
		// TODO Auto-generated method stub
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
