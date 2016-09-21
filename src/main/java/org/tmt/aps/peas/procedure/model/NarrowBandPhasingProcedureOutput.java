package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
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
