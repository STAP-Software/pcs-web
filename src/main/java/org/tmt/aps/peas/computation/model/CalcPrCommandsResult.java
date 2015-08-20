package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.Point;

public class CalcPrCommandsResult {

	
	Point coarseMirrorCommands;
	Point fineMirrorCommands;
	boolean offloaded;
	Point coarseMirrorDeltas;
	Point fineMirrorDeltas;
	
	public CalcPrCommandsResult(Point coarseMirrorCommands, Point fineMirrorCommands, Point coarseMirrorDeltas, Point fineMirrorDeltas) {
		this.coarseMirrorCommands = coarseMirrorCommands;
		this.fineMirrorCommands = fineMirrorCommands;
		this.coarseMirrorDeltas = coarseMirrorDeltas;
		this.fineMirrorDeltas = fineMirrorDeltas;
	}

	public CalcPrCommandsResult(Point coarseMirrorCommands, Point fineMirrorCommands,  Point coarseMirrorDeltas, Point fineMirrorDeltas, boolean offloaded) {
		this.coarseMirrorCommands = coarseMirrorCommands;
		this.fineMirrorCommands = fineMirrorCommands;
		this.coarseMirrorDeltas = coarseMirrorDeltas;
		this.fineMirrorDeltas = fineMirrorDeltas;
		this.offloaded = offloaded;
	}

	public boolean hasCoarseMirrorCommands() {
		
		return coarseMirrorCommands != null;
	}

	public boolean hasFineMirrorCommands() {
		
		return fineMirrorCommands != null;
	}

	public Point getCoarseMirrorCommands() {
		return coarseMirrorCommands;
	}

	public Point getFineMirrorCommands() {
		return fineMirrorCommands;
	}

	public Point getCoarseMirrorDeltas() {
		return coarseMirrorDeltas;
	}

	public Point getFineMirrorDeltas() {
		return fineMirrorDeltas;
	}

	public boolean isOffloaded() {
		return offloaded;
	}

}
