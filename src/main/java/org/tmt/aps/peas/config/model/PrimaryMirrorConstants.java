package org.tmt.aps.peas.config.model;


import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.common.Point;

/**
 * Constants data class containing primary mirror constants.  This class is populated from database data in the {@link Constant} class and is made available to executors and
 * the user interface controllers in the {@link org.tmt.aps.peas.config.business.ConstantsCache}.
 * @author smichaels
 */
public class PrimaryMirrorConstants {

	int[] nColor;
	int[] edgeColor;
	int[] savePlusPiston;
	int[] saveMinusPiston;
	
	int[] segmentRow;
	int[] segmentCol;
	
	int[] edgeAngle;
	
	Point[] nEdge;

	int[] normAngle;
	int[][] neighbors;
	
	FloatPoint[] centerSpot;
	
	float[] act1Pos;
	float aHex;
	float primaryActuatorTriangle;
	
	FloatPoint[] primaryActPos;
	
	float[][] aMatrix;
	
	FloatPoint[][] fineScreenSpotCoords;
	
	FloatPoint[] segmentCenters;
	
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
	
	public FloatPoint[] getSegmentCenters() {
		return segmentCenters;
	}
	public void setSegmentCenters(FloatPoint[] segmentCenters) {
		this.segmentCenters = segmentCenters;
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
	
	public FloatPoint[] getPrimaryActPos() {
		return primaryActPos;
	}
	public void setPrimaryActPos(FloatPoint[] primaryActPos) {
		this.primaryActPos = primaryActPos;
	}
	
	public float[][] getaMatrix() {
		return aMatrix;
	}
	public void setaMatrix(float[][] aMatrix) {
		this.aMatrix = aMatrix;
	}
	
	public FloatPoint[][] getFineScreenSpotCoords() {
		return fineScreenSpotCoords;
	}
	public void setFineScreenSpotCoords(FloatPoint[][] fineScreenSpotCoords) {
		this.fineScreenSpotCoords = fineScreenSpotCoords;
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
		
		buf.append("\nsegmentRow = ");
		for (int i=0; i<segmentRow.length; i++) {
			buf.append(segmentRow[i] + ", ");
		}
		
		buf.append("\nsegmentCol = ");
		for (int i=0; i<segmentCol.length; i++) {
			buf.append(segmentCol[i] + ", ");
		}
		
		buf.append("\nedgeAngle = ");
		for (int i=0; i<edgeAngle.length; i++) {
			buf.append(edgeAngle[i] + ", ");
		}

		buf.append("\nnEdge = ");
		for (int i=0; i<nEdge.length; i++) {
			buf.append(nEdge[i] + ", ");
		}

		buf.append("\nnormAngle = ");
		for (int i=0; i<normAngle.length; i++) {
			buf.append(normAngle[i] + ", ");
		}

		buf.append("\nneighbors = ");
		for (int i=0; i<neighbors.length; i++) {
			buf.append("[");
			for (int j=0; j<neighbors[0].length; j++) {
				buf.append(neighbors[i][j] + ", ");
			}
			buf.append("],");
		}

		buf.append("\ncenterSpot = ");
		for (int i=0; i<centerSpot.length; i++) {
			buf.append(centerSpot[i] + ", ");
		}
		
		buf.append("\nact1Pos = ");
		for (int i=0; i<act1Pos.length; i++) {
			buf.append(act1Pos[i] + ", ");
		}
		
		buf.append("\naHex = " + aHex);
		buf.append("\nprimaryActuatorTriangle = " + primaryActuatorTriangle);

		
		buf.append("\nfineScreenSpotCoords = ");
		for (int i=0; i<fineScreenSpotCoords.length; i++) {
			buf.append("[");
			for (int j=0; j<fineScreenSpotCoords[0].length; j++) {
				buf.append(fineScreenSpotCoords[i][j] + ", ");
			}
			buf.append("],");
		}

		
		buf.append("\n");
		return buf.toString();
	}

	
	
}
