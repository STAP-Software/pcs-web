/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas;

public class Constants {

	public static final int FRAME_SOURCE_CCD = 1;
	public static final int FRAME_SOURCE_FILE = 2;
	
	public static final int LIGHT_SOURCE_STAR = 1;
	public static final int LIGHT_SOURCE_LED = 2;
	
	public static final int PUPIL_MASK_PASSIVE_TILT = 1;
	public static final int PUPIL_MASK_PHASING = 2;
	public static final int PUPIL_MASK_FINE_SCREEN = 3;
	public static final int PUPIL_MASK_UFS = 4;
	public static final int PUPIL_MASK_SUFS = 5;
	
	public static final int CALC_OPTION_PURE_TILTS = 1;
	public static final int CALC_OPTION_FOCUS_MODE_PISTONS = 2;
	public static final int CALC_OPTION_PURE_TILTS_PLUS_FOCUS_MODE_PISTONS = 3;
	public static final int CALC_OPTION_PURE_FOCUS_MODE = 4;
	public static final int CALC_OPTION_PURE_TILTS_PLUS_OPTIMAL_PISTONS = 5;
	public static final int CALC_OPTION_OPTIMAL_PISTONS = 6;
	
	public static final int FRAME_SCALE_ROTATION_REMOVAL_NO = 1;
	public static final int FRAME_SCALE_ROTATION_REMOVAL_YES = 2;
	public static final int FRAME_SCALE_ROTATION_REMOVAL_PROMPT = 3;

	public static final int AUTO_CENTER_TELESCOPE_YES = 1;
	public static final int AUTO_CENTER_TELESCOPE_NO = 2;
	public static final int AUTO_CENTER_TELESCOPE_PROMPT = 3;

	public static final int AUTO_COMMAND_TILT_PLATE_YES = 1;
	public static final int AUTO_COMMAND_TILT_PLATE_NO = 2;
	public static final int AUTO_COMMAND_TILT_PLATE_PROMPT = 3;

	public static final int AUTO_CENTER_PUPIL_MECH_COARSE = 1;
	public static final int AUTO_CENTER_PUPIL_MECH_FINE = 2;
	public static final int AUTO_CENTER_PUPIL_MECH_PROMPT = 3;

	public static final int AUTO_SEND_ACT_DELTAS_YES = 1;
	public static final int AUTO_SEND_ACT_DELTAS_NO = 2;
	public static final int AUTO_SEND_ACT_DELTAS_PROMPT = 3;

	public static final int AUTO_TAKE_REF_MAPS_YES = 1;
	public static final int AUTO_TAKE_REF_MAPS_NO = 2;
	public static final int AUTO_TAKE_REF_MAPS_PROMPT = 3;

	
	
}
