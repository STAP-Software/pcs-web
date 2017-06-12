package org.tmt.aps.peas.extInterface.ui;

import org.tmt.aps.peas.extinf.VoltageListener;
import org.tmt.aps.peas.extinf.Voltages;
import org.tmt.aps.peas.instrument.model.Instrument;

public class DiagnosticCameraVoltageListener implements VoltageListener {

	
	Instrument instrument;
	
	public DiagnosticCameraVoltageListener(Instrument instrument) {
		this.instrument = instrument; 
	}

	@Override
	public void updateVoltages(Voltages voltages) {
		
		// TODO: implement
		//instrument.updateVoltages(voltages);
	}
	
	
}
