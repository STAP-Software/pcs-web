package org.tmt.aps.peas.extInterface.ui;

import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.extinf.CameraStatusListener;
import org.tmt.aps.peas.instrument.model.Instrument;

public class DiagnosticCameraStatusListener implements CameraStatusListener {

	
	Instrument instrument;
	
	public DiagnosticCameraStatusListener(Instrument instrument) {
		this.instrument = instrument; 
	}
	
	@Override
	public void cameraStatusUpdate(CameraStatus cameraStatus) {
		instrument.updateState(cameraStatus);
	}

	
}
