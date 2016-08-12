package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.Constants;
import org.tmt.aps.peas.computation.model.BbAnalyzeSequenceResult;
import org.tmt.aps.peas.computation.model.CalcDesiredActCommandsResult;
import org.tmt.aps.peas.computation.model.ColorStepResult;
import org.tmt.aps.peas.computation.model.ColorStepToActuatorsResult;
import org.tmt.aps.peas.computation.model.FixPistonsResult;
import org.tmt.aps.peas.computation.model.PhasingStatsResult;
import org.tmt.aps.peas.visualization.model.ActuatorDeltasDisplayValues;
import org.tmt.aps.peas.visualization.model.EdgeHeightsDisplayValues;

/**
 * Procedure output data (not including step specific data) for Phasing procedure
 * @author smichaels
 *
 */
public class NarrowBandPhasingProcedureOutput extends ProcedureOutput implements EdgeHeightsDisplayValues, ActuatorDeltasDisplayValues {

	BbAnalyzeSequenceResult bbAnalyzeSequenceResult;
	FixPistonsResult fixPistonsResult;
	PhasingStatsResult phasingStatsResult;
	CalcDesiredActCommandsResult calcDesiredActCommandsResult;
	ColorStepResult colorStepResult;
	ColorStepToActuatorsResult colorStepToActuatorsResult;

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

	
	public CalcDesiredActCommandsResult getCalcDesiredActCommandsResult() {
		return calcDesiredActCommandsResult;
	}

	public void setCalcDesiredActCommandsResult(CalcDesiredActCommandsResult calcDesiredActCommandsResult) {
		this.calcDesiredActCommandsResult = calcDesiredActCommandsResult;
	}

	public ColorStepResult getColorStepResult() {
		return colorStepResult;
	}

	public void setColorStepResult(ColorStepResult colorStepResult) {
		this.colorStepResult = colorStepResult;
	}

	public ColorStepToActuatorsResult getColorStepToActuatorsResult() {
		return colorStepToActuatorsResult;
	}

	public void setColorStepToActuatorsResult(ColorStepToActuatorsResult colorStepToActuatorsResult) {
		this.colorStepToActuatorsResult = colorStepToActuatorsResult;
	}
	
	// actuator deltas display values

	public float[][] getDesiredActDeltas() {
		
		float[][] pistons = new float[36][3];
		for (int i=0; i<bbAnalyzeSequenceResult.getActCalc().length; i++) {
			// convert measured actuator pistons to desired actuator pistons
			pistons[i][0] = bbAnalyzeSequenceResult.getActCalc()[i] * -Constants.MICRONS_TO_NM;
			pistons[i][1] = bbAnalyzeSequenceResult.getActCalc()[i] * -Constants.MICRONS_TO_NM;
			pistons[i][2] = bbAnalyzeSequenceResult.getActCalc()[i] * -Constants.MICRONS_TO_NM;
		
		}
		//return calcDesiredActCommandsResult.getDesiredActDeltas();
		return pistons;
		
	}
	public float getDesiredActDeltasRms() {
		return calcDesiredActCommandsResult.getDesiredActDeltasRms();
	}


	
}
