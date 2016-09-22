package org.tmt.aps.peas.lang.interop.examples;

import java.util.Arrays;

import org.tmt.aps.peas.lang.interop.JgcSort2;
import org.tmt.aps.peas.lang.interop.RetVal;

public class Example1 {

	public static void main(String[] args) {
		JgcSort2 jgcSort2 = new JgcSort2();
		
		float[] arr = {1.0f, 3.0f, 2.0f};
		float[] brr = {3.1f, 1.3f, 2.2f};
		
		float[] arrOut = new float[3];
		float[] brrOut = new float[3];
		
		RetVal retVal = new RetVal();

		jgcSort2.jgcSort2(retVal, arr, brr, arrOut, brrOut);
		
		System.out.println("inputs: ");
		System.out.println("arr: " + Arrays.toString(arr));
		System.out.println("brr: " + Arrays.toString(brr));
		System.out.println("inputs: ");
		System.out.println("arrOut: " + Arrays.toString(arrOut));
		System.out.println("brrOut: " + Arrays.toString(brrOut));
		
	}
	
}
