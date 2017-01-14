package org.tmt.aps.peas.tools;

public class MaskConverter {

	static double[] coords = {0.0,-1.0, -1.5,-0.5, -1.5,0.5,  0.0,1.0,  1.5,0.5,  1.5,-0.5,
	0.0,-2.0, -1.5,-1.5, -3.0,-1.0, -3.0,0.0, -3.0,1.0, -1.5,1.5,
	0.0,2.0,  1.5,1.5,  3.0,1.0,  3.0,0.0,  3.0,-1.0,  1.5,-1.5,
	0.0,-3.0, -1.5,-2.5, -3.0,-2.0, -4.5,-1.5, -4.5,-0.5, -4.5,0.5,
	-4.5,1.5, -3.0,2.0, -1.5,2.5,  0.0,3.0,  1.5,2.5,  3.0,2.0,
	4.5,1.5,  4.5,0.5,  4.5,-0.5,  4.5,-1.5,  3.0,-2.0,  1.5,-2.5};
	
	public static void main(String[] args) {
		int i=0;
		for (double coord : coords) {
			double coordInM = coord * 0.9;
			if (i%2 == 0)
				System.out.print(coordInM + ",");
			else 
				System.out.print(coordInM + ", ");
			i++;
			
			if (i%12 == 0) {
				System.out.print("' ||\n'");
			}
		}
	}
	
}
