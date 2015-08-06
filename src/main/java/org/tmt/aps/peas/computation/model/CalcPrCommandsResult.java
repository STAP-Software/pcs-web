package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.Point;

public class CalcPrCommandsResult {

	
	Point coarseMirrorCommands;
	Point fineMirrorCommands;
	boolean offloaded;
	
	public CalcPrCommandsResult(Point coarseMirrorCommands, Point fineMirrorCommands) {
		this.coarseMirrorCommands = coarseMirrorCommands;
		this.fineMirrorCommands = fineMirrorCommands;
	}

	public CalcPrCommandsResult(Point coarseMirrorCommands, Point fineMirrorCommands, boolean offloaded) {
		this.coarseMirrorCommands = coarseMirrorCommands;
		this.fineMirrorCommands = fineMirrorCommands;
		this.offloaded = offloaded;
	}

	public boolean hasCoarseCommands() {
		
		return coarseMirrorCommands != null;
	}

	public boolean hasFineCommands() {
		
		return fineMirrorCommands != null;
	}

	public Point getCoarseCommands() {
		
		return coarseMirrorCommands;
	}

	public Point getFineCommands() {
		
		return fineMirrorCommands;
	}

	public boolean isOffloaded() {
		return offloaded;
	}

}
