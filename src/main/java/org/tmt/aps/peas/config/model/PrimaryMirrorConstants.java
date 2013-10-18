package org.tmt.aps.peas.config.model;

import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;

public class PrimaryMirrorConstants {

	int nColor[];
	int edgeColor[];
	int savePlusPiston[];
	int saveMinusPiston[];
	
	int segmentRow[];
	int segmentCol[];
	
	int edgeAngle[];
	
	Point nEdge[];

	int normAngle[];
	int neighbors[][];
	
	FloatPoint centerSpot[];
	
	float act1Pos[];
	float aHex;
	float primaryActuatorTriangle;
	
	
	
	public int[] getnColor() {
		return nColor;
	}
	public void setnColor(int[] nColor) {
		this.nColor = nColor;
	}
	public int[] getEdgeColor() {
		return edgeColor;
	}
	public void setEdgeColor(int[] edgeColor) {
		this.edgeColor = edgeColor;
	}
	public int[] getSavePlusPiston() {
		return savePlusPiston;
	}
	public void setSavePlusPiston(int[] savePlusPiston) {
		this.savePlusPiston = savePlusPiston;
	}
	public int[] getSaveMinusPiston() {
		return saveMinusPiston;
	}
	public void setSaveMinusPiston(int[] saveMinusPiston) {
		this.saveMinusPiston = saveMinusPiston;
	}
	public int[] getSegmentRow() {
		return segmentRow;
	}
	public void setSegmentRow(int[] segmentRow) {
		this.segmentRow = segmentRow;
	}
	public int[] getSegmentCol() {
		return segmentCol;
	}
	public void setSegmentCol(int[] segmentCol) {
		this.segmentCol = segmentCol;
	}
	public int[] getEdgeAngle() {
		return edgeAngle;
	}
	public void setEdgeAngle(int[] edgeAngle) {
		this.edgeAngle = edgeAngle;
	}
	public Point[] getnEdge() {
		return nEdge;
	}
	public void setnEdge(Point[] nEdge) {
		this.nEdge = nEdge;
	}
	public int[] getNormAngle() {
		return normAngle;
	}
	public void setNormAngle(int[] normAngle) {
		this.normAngle = normAngle;
	}
	public int[][] getNeighbors() {
		return neighbors;
	}
	public void setNeighbors(int[][] neighbors) {
		this.neighbors = neighbors;
	}
	public FloatPoint[] getCenterSpot() {
		return centerSpot;
	}
	public void setCenterSpot(FloatPoint[] centerSpot) {
		this.centerSpot = centerSpot;
	}
	public float[] getAct1Pos() {
		return act1Pos;
	}
	public void setAct1Pos(float[] act1Pos) {
		this.act1Pos = act1Pos;
	}
	public float getaHex() {
		return aHex;
	}
	public void setaHex(float aHex) {
		this.aHex = aHex;
	}
	public float getPrimaryActuatorTriangle() {
		return primaryActuatorTriangle;
	}
	public void setPrimaryActuatorTriangle(float primaryActuatorTriangle) {
		this.primaryActuatorTriangle = primaryActuatorTriangle;
	}
	
	
	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("\nnColor = ");
		for (int i=0; i<nColor.length; i++) {
			buf.append(nColor[i] + ", ");
		}
		
		buf.append("\nedgeColor = ");
		for (int i=0; i<edgeColor.length; i++) {
			buf.append(edgeColor[i] + ", ");
		}
		
		buf.append("\nsavePlusPiston = ");
		for (int i=0; i<savePlusPiston.length; i++) {
			buf.append(savePlusPiston[i] + ", ");
		}
		
		buf.append("\nsaveMinusPiston = ");
		for (int i=0; i<saveMinusPiston.length; i++) {
			buf.append(saveMinusPiston[i] + ", ");
		}
		
		buf.append("\n");
		return buf.toString();
	}

	
	
}
