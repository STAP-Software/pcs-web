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
	@Override
	public BbAnalyzeSequenceResult getBbAnalyzeSequenceResult() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PhasingStatsResult getPhasingStatsResult() {
		// TODO Auto-generated method stub
		return null;
	}
	


	
}
