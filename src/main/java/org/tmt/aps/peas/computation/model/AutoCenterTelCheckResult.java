package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.TriState;

public class AutoCenterTelCheckResult {
	
	TriState recenterTelescope;
	TriState retakeFrame;
	
	String reasonKey;
	Object[] reasonArgs;
	
	public AutoCenterTelCheckResult(TriState recenterTelescope, TriState retakeFrame) {
		this.recenterTelescope = recenterTelescope;
		this.retakeFrame = retakeFrame;
	}
	
	public AutoCenterTelCheckResult(TriState recenterTelescope, TriState retakeFrame, String reasonKey, Object[] reasonArgs) {
		this(recenterTelescope, retakeFrame);
		this.reasonKey = reasonKey;
		this.reasonArgs = reasonArgs;
	}
	
	public TriState getRecenterTelescope() {
		return recenterTelescope;
	}

	public void setRecenterTelescope(TriState recenterTelescope) {
		this.recenterTelescope = recenterTelescope;
	}

	public TriState getRetakeFrame() {
		return retakeFrame;
	}

	public void setRetakeFrame(TriState retakeFrame) {
		this.retakeFrame = retakeFrame;
	}

	public String getReasonKey() {
		return reasonKey;
	}
	public void setReasonKey(String reasonKey) {
		this.reasonKey = reasonKey;
	}

	public Object[] getReasonArgs() {
		return reasonArgs;
	}

	public void setReasonArgs(Object[] reasonArgs) {
		this.reasonArgs = reasonArgs;
	}
	
	
}
