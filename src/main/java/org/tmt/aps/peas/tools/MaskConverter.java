package org.tmt.aps.peas.tools;

import org.tmt.aps.peas.common.FloatPoint;

public class MaskConverter {

	
	// x and y are in camera coordinate orientations
	
	// row 7 is y = 0 in mirror coordinates, each row is side length * sqrt(3/4) from the next
	static float[] segmentRow = {5, 6, 8, 9 ,8, 6, 3, 4, 5, 7, 9,10,11,10, 9, 7, 5, 4, 1, 2, 3, 4, 6, 8,10,11,12,13,12,11,10, 8, 6, 4, 3, 2};
	
	// col 4 is x = 0 in mirror coordinates, each column is side length * 1.5 from the next
	static float[] segmentCol = {4, 3, 3, 4, 5, 5, 4, 3, 2, 2, 2, 3, 4, 5, 6, 6, 6, 5, 4, 3, 2, 1, 1, 1, 1, 2, 3, 4, 5, 6, 7, 7, 7, 7, 6, 5};
			
	
	static double[] fineSpotsInSegSideLen = {0.000,0.0000, 0.000,0.4330, -0.375, 0.2165, -0.375,-0.2165, 0.000, -0.4330, 0.375,-0.2165,
			0.375,0.2165, 0.375,0.6495, -0.375,0.6495, -0.750,0.0000, -0.375,-0.6495, 0.375,-0.6495, 0.750, 0.0000};
			
	
	// The x-coordinates are in units of 0.75a, where a is the hexagon side.  The y-coordinates are in units of sqrt(3)/4 times a.
	static double[] nEdge = {-1,3, -2,0, -1,-3, 1,-3, 2,0, 1,3, 0,6, -1,5, -2,4, -3,3, -3,1, -3,-1, -3,-3, -2,-4, -1,-5, 0,-6, 1,-5, 2,-4, 3,-3, 3,-1, 3,1, 3,3, 2,4, 1,5,  
			-1,7, -3,5, -4,2, -4,-2, -3,-5, -1,-7, 1,-7, 3,-5, 4,-2, 4,2, 3,5, 1,7,
			0,10, -1,9, -2,8, -3,7, -4,6, -5,5, -5,3, -5,1, -5,-1, -5,-3, -5,-5, -4,-6, -3,-7, -2,-8, -1,-9,   
			0,-10, 1,-9, 2,-8, 3,-7, 4,-6, 5,-5, 5,-3, 5,-1, 5,1, 5,3, 5,5, 4,6, 3,7, 2,8, 1,9,
			-1,11, -3,9, -5,7, -6,4, -6,0, -6,-4, -5,-7, -3,-9, -1,-11, 1,-11, 3,-9, 5,-7, 6,-4, 6,0, 6,4, 5,7, 3,9, 1,11,
			-3,11, -4,10, -5,9, -6,8, -7,7, -7,5, -7,3, -7,1, -7,-1, -7,-3, -7,-5, -7,-7,
			-6,-8, -5,-9, -4,-10, -3,-11, -2,-12, 1,-13, 2,-12, 3,-11, 4,-10, 5,-9, 6,-8,
			7,-7, 7,-5, 7,-3, 7,-1, 7,1, 7,3, 7,5, 7,7, 6,8, 5,9, 4,10, 3,11};
			

	
	
	public static void main(String[] args) {
		
		// segment centers in side length is calculated using segmentRow and segmentCol
		FloatPoint[] segmentCentersSL = new FloatPoint[36];
		for (int i=0; i<36; i++) {
			float x = (segmentCol[i] - 4) * 1.5f;
			float y = -(segmentRow[i] - 7) * (float)Math.sqrt(0.75);
			segmentCentersSL[i] = new FloatPoint(x, y);
		}
		
		// segment centers in meters at M1 calculated
		FloatPoint[] segmentCenters = new FloatPoint[36];
		for (int i=0; i<36; i++) {
			segmentCenters[i] = segmentCentersSL[i].prod(0.9);
		}
	
		printCoordinates("Segment Centers in meters at M1", segmentCenters, 6);
	
		
		
		// calculate fine spots in primary mirror coordinates, in meters
		FloatPoint[] fineSpotsInSegSL = doubleToFloatPoint(fineSpotsInSegSideLen);
		
		FloatPoint[] fineSpots = new FloatPoint[503];
		int i=0;
		for (FloatPoint segmentCenterSL : segmentCentersSL) {
			for (FloatPoint fineSpotInSegSL : fineSpotsInSegSL) {
				fineSpots[i++] = segmentCenterSL.add(fineSpotInSegSL).prod(0.9);
			}
		}
		
		// calculate phasing spots 
		
		FloatPoint[] nEdgeFP = doubleToFloatPoint(nEdge);
		
		// need to multiply x and y by appropriate units to get in units of side length
		
		FloatPoint[] edges = new FloatPoint[nEdgeFP.length];
		for (int index=0; index<nEdgeFP.length; index++) {
			FloatPoint spot = new FloatPoint((float)(nEdgeFP[index].x * (0.75)), (float)(nEdgeFP[index].y * Math.sqrt(3)/4));
			
			edges[index] = spot.prod(0.9);
		}
		
		printCoordinates("Coarse Spots in meters at M1", edges, 8);
		

		// 85 - 119 are added to fine screen
		for (int index = 84; index < 119; index++) {
			fineSpots[i++] = edges[index];		
		}
		
		printCoordinates("Fine Spots in meters at M1", fineSpots, 13);

		
	}
	
	
	
	
	private static void printCoordinates(String title, FloatPoint[] input, int coordsPerLine) {
		
		System.out.println(title);
		
		int i=0;
		for (FloatPoint coord : input) {
			
			if (i==0) {
				System.out.print("'");
			}
			
			if (i%coordsPerLine == 0 && i > 0) {
				System.out.print("' ||\n'");
			}
			
			try {
				System.out.print(coord.x + "," + coord.y + ", " );
			} catch (Exception e) {
				System.out.print("xxx,yyy, ");
			}
						
			i++;
		}
		
		System.out.println("\n");


	}
	
	
	private static FloatPoint[] doubleToFloatPoint(double[] input) {
		FloatPoint[] output = new FloatPoint[input.length/2];
		for (int i=0; i<input.length; i+=2) {
			output[i/2] = new FloatPoint((float)input[i], (float)input[i+1]);
		}
		return output;
	}
	
}
