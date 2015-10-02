package org.tmt.aps.peas.procedure.model;

import org.tmt.aps.peas.computation.model.CenterTelescopeCalcResult;
import org.tmt.aps.peas.computation.model.FindCentResult;

public class CenterTelescopeProcedureOutput extends ProcedureOutput {

	
	CenterTelescopeCalcResult centerTelescopeCalcResult;
	FindCentResult findCentResult;
	
	
	public CenterTelescopeCalcResult getCenterTelescopeCalcResult() {
		return centerTelescopeCalcResult;
	}
	public void setCenterTelescopeCalcResult(CenterTelescopeCalcResult centerTelescopeCalcResult) {
		this.centerTelescopeCalcResult = centerTelescopeCalcResult;
	}
	public FindCentResult getFindCentResult() {
		return findCentResult;
	}
	public void setFindCentResult(FindCentResult findCentResult) {
		this.findCentResult = findCentResult;
	}
	

	
	
}
