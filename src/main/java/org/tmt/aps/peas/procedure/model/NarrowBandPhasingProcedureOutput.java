package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.Constants;
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

	
	public float[][] getDesiredActDeltas() {
		
		float[][] pistons = new float[36][3];
		for (int i=0; i<nbActuatorsResult.getActCalc().length; i++) {
			// convert measured actuator pistons to desired actuator pistons
			pistons[i][0] = nbActuatorsResult.getActCalc()[i] * -Constants.MICRONS_TO_NM;
			pistons[i][1] = nbActuatorsResult.getActCalc()[i] * -Constants.MICRONS_TO_NM;
			pistons[i][2] = nbActuatorsResult.getActCalc()[i] * -Constants.MICRONS_TO_NM;
		
		}
		//return calcDesiredActCommandsResult.getDesiredActDeltas();
		return pistons;
		
	}
	
	public float getDesiredActDeltasRms() {
		return calcDesiredActCommandsResult.getDesiredActDeltasRms();
	}
	
	
	
	
	
	
	
	// EdgeHeights Display Values
	
	public float[] getStepCorr() {
		
		return nbAnalyzeFilterSequenceResult.getNbStep();
	}
	
	public float[] getResid() {
		return nbActuatorsResult.getResid();
	}
	
	public int[] getRowFlagOut() {
		
		return nbAnalyzeFilterSequenceResult.getRowFlagOut();
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
