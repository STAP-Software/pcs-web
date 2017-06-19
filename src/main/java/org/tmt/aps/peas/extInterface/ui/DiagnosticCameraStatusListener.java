package org.tmt.aps.peas.extInterface.ui;

import org.tmt.aps.peas.extinf.CameraStatus;
import org.tmt.aps.peas.extinf.CameraStatusListener;
import org.tmt.aps.peas.instrument.model.Instrument;

public class DiagnosticCameraStatusListener implements CameraStatusListener {

	
	Instrument instrument;
	boolean updateRequested;
	
	public DiagnosticCameraStatusListener(Instrument instrument) {
		this.updateRequested = false;
		this.instrument = instrument; 
	}
	
	@Override
	public void cameraStatusUpdate(CameraStatus cameraStatus) {
		instrument.updateState(cameraStatus);
		updateRequested = true;
	}

	public boolean isUpdateRequested() {
		return updateRequested;
	}

	public void setUpdateRequested(boolean updateRequested) {
		this.updateRequested = updateRequested;
	}

	
}
