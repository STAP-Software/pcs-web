package org.tmt.aps.peas.lang.interop.test;

//import org.tmt.aps.peas.lang.interop.JfindAndIdentify;
import org.tmt.aps.peas.lang.interop.RetVal;

public class Test {

	public static void main(String[] args) {
		
		RetVal retVal = new RetVal();

		//JfindAndIdentify jfid = new JfindAndIdentify();

		float[][] frame = new float[1024][1025];
		for (int i=0; i<1024; i++) {
			for (int j=0; j<1025; j++) {
				frame[i][j] = i*1025 + j;
			}
		}

		//float[][] centroids = new float[36][2];

		//Object[] returnValues = jfid.jfindAndIdentify(retVal, frame, centroids);

		System.out.println("retVal.code = " + retVal.getCode());
		System.out.println("retVal.arg0 = " + retVal.getArg0());
		System.out.println("retVal.arg1 = " + retVal.getArg1());

	/*	for (int i=0; i<36; i++) {
			System.out.println("centroid[i] = " + centroids[i][0] + "," + centroids[i][1]);
		}
*/
	
	
	}

}
