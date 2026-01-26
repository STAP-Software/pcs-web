package org.tmt.aps.peas.common;

/**
 * Class representing a three-state logical.  The states are "yes", "no", and "prompt".  This pattern is used often in PCS where a setting can be either yes, no, or ask the user in real time.
 * @author smichaels
 *
 */
public class TriState {

	/**
	 * constant TriState in the YES state
	 */
	public static final TriState YES = new TriState(1);
	/**
	 * constant TriState in the NO state
	 */
	public static final TriState NO = new TriState(2);
	/**
	 * constant TriState in the PROMPT state
	 */
	public static final TriState PROMPT = new TriState(3);
	
	Boolean state;
	
	private TriState(int state) {
		if (state == 3) {
			this.state = null;
		} else {
			this.state =  Boolean.valueOf(state == 1);
		}
	}
	/**
	 * @return true if the triState is in the 'Yes' state
	 */
	public boolean isYes() {
		return (state != null) && state;
	}
	
	/**
	 * @return true if the triState is in the 'No' state
	 */
	public boolean isNo() {
		return (state != null) && !state;
	}
	
	/**
	 * @return true if the triState is in the 'Prompt' state
	 */
	public boolean isPrompt() {
		return (state == null);
	}
	

}
