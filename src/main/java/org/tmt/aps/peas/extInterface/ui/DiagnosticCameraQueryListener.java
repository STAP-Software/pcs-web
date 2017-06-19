package org.tmt.aps.peas.extInterface.ui;

import org.tmt.aps.peas.extinf.CameraQueryListener;
import org.tmt.aps.peas.extinf.CameraQueryResult;
import org.tmt.aps.peas.instrument.model.Instrument;

public class DiagnosticCameraQueryListener implements CameraQueryListener {

	boolean updateRequested;
	Instrument instrument;
	
	// the UI will create one of these listeners for each device code
	public DiagnosticCameraQueryListener(Instrument instrument) {
		this.instrument = instrument;
		this.updateRequested = false;
	}
	
	public void cameraQueryUpdate(int deviceCode, CameraQueryResult result) {
		
		// update the physicalModel 
		instrument.updateDevice(deviceCode, result);
		
		updateRequested = true;  // set true for poller to pick up
	}

	
	public boolean isUpdateRequested() {
		return updateRequested;
	}

	public void setUpdateRequested(boolean updateRequested) {
		this.updateRequested = updateRequested;
	}

	
	
}
