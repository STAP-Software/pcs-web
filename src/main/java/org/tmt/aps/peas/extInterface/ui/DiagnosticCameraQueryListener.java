package org.tmt.aps.peas.extInterface.ui;

import org.tmt.aps.peas.extinf.CameraQueryListener;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.instrument.model.Instrument;

public class DiagnosticCameraQueryListener implements CameraQueryListener {

	Instrument instrument;
	
	// the UI will create one of these listeners for each device code
	public DiagnosticCameraQueryListener(Instrument instrument) {
		this.instrument = instrument;
	}
	
	public void cameraQueryUpdate(int deviceCode, CameraQueryResult result) {
		
		// TODO: implement a method in Instrument that will update a single deviceCode
	}

}
