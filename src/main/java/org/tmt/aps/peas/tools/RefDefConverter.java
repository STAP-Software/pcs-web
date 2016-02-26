package org.tmt.aps.peas.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RefDefConverter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// open and read a file
		File file = new File("/home/smichaels/git/pcs-fortran-work/pcs_data/refbeams/defs/REF_DEF_TABLE_SUFS_NEW.DAT");
		BufferedReader reader = null;

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    List<String> values = new ArrayList<String>();
		    
		    while ((text = reader.readLine()) != null) { 
		    	// read in values one at a time
		    	String[] strValues = text.trim().split("\\s+");
		    	values.add(strValues[1]);
		    	values.add(strValues[2]);
		    }
		    
		    
		    
		    for (int i=0; i<1809*2; i+=2) {
		    	System.out.print(values.get(i) + "," + values.get(i+1) + ", ");
		    	
		    	if ((i-10)%12 == 0) {
		    		System.out.print(" ' ||\n'");
		    	}
		    }
		    
		
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    }
		}
		
	}

}
