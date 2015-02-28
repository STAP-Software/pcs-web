package org.tmt.aps.peas.common;

public class TriState {

	public static final TriState YES = new TriState(1);
	public static final TriState NO = new TriState(2);
	public static final TriState PROMPT = new TriState(3);
	
	Boolean state;
	
	private TriState(int state) {
		if (state == 3) {
			this.state = null;
		} else {
			this.state = new Boolean(state == 1);
		}
	}
	
	public boolean isYes() {
		return (state != null) && state;
	}
	
	public boolean isNo() {
		return (state != null) && !state;
	}
	
	public boolean isPrompt() {
		return (state == null);
	}
	

}
