package org.tmt.aps.peas;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.primefaces.event.FlowEvent;

@Named
@SessionScoped
public class ProcedureWizardBean implements Serializable {

	private String currentStepId;

	public boolean getRenderNext() {
		return currentStepId.equals("setup");
	}

	public boolean getRenderBack() {
		return !currentStepId.equals("setup");
	}

	public void reset() {
		currentStepId = "setup";
	}

	public String onFlowProcess(FlowEvent event) {
		currentStepId = event.getNewStep();
		return event.getNewStep();
	}
}
