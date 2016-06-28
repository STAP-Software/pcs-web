package org.tmt.aps.peas.computation.model;

import org.tmt.aps.peas.common.Point;

/**
 * Computation data result class for calcPrCommands computation.
 * @author smichaels
 * @see org.tmt.aps.peas.computation.business.ComputationLibraryImpl#calcPrCommands(boolean, int, PupilRegErrorResult, org.tmt.aps.peas.config.model.PupilRegErrorConfig, org.tmt.aps.peas.instrument.model.FineTiltMirror, org.tmt.aps.peas.instrument.model.CoarseTiltMirror)
 */
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
	
	public CalcPrCommandsResult() {};
	

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

	public void setCoarseMirrorCommands(Point coarseMirrorCommands) {
		this.coarseMirrorCommands = coarseMirrorCommands;
	}

	public void setFineMirrorCommands(Point fineMirrorCommands) {
		this.fineMirrorCommands = fineMirrorCommands;
	}

	public void setOffloaded(boolean offloaded) {
		this.offloaded = offloaded;
	}

	public void setCoarseMirrorDeltas(Point coarseMirrorDeltas) {
		this.coarseMirrorDeltas = coarseMirrorDeltas;
	}

	public void setFineMirrorDeltas(Point fineMirrorDeltas) {
		this.fineMirrorDeltas = fineMirrorDeltas;
	}

}
