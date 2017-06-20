package org.tmt.aps.peas.extInterface.ui;

import org.tmt.aps.peas.extinf.VoltageListener;
import org.tmt.aps.peas.extinf.Voltages;
import org.tmt.aps.peas.instrument.model.Instrument;

public class DiagnosticCameraVoltageListener implements VoltageListener {

	
	Instrument instrument;
	boolean updateRequested;
	
	public DiagnosticCameraVoltageListener(Instrument instrument) {
		this.instrument = instrument; 
		this.updateRequested = false;
	}

	@Override
	public void updateVoltages(Voltages voltages) {
		
		// update the voltage states in the camera
		instrument.getCamera().setVoltages(voltages);
		
		updateRequested = true;
	}

	public boolean isUpdateRequested() {
		return updateRequested;
	}

	public void setUpdateRequested(boolean updateRequested) {
		this.updateRequested = updateRequested;
	}
	
	
}
