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
		File file = new File("c:\\workspace\\pcs-fortran-work\\pcs_data\\refbeams\\defs\\REF_DEF_TABLE_160.dat");
		BufferedReader reader = null;

		try {
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;

		    List<String> values = new ArrayList<String>();
		    
		    while ((text = reader.readLine()) != null) { 
		    	// read in values one at a time
		    	String[] strValues = text.trim().split("\\s+");
		    	values.addAll(Arrays.asList(strValues));
		    }
		    
		    // Now create two lists
		    values.size();
		    List<String> listX = values.subList(0, values.size()/2);
		    List<String> listY = values.subList(values.size()/2, values.size());
		    
		    
		    for (int i=0; i<160; i+=6) {
		    	System.out.print("'" + listX.get(i) + "," + listY.get(i) + ", ");
		    	System.out.print(listX.get(i+1) + "," + listY.get(i+1) + ", ");
		    	System.out.print(listX.get(i+2) + "," + listY.get(i+2) + ", ");
		    	System.out.print(listX.get(i+3) + "," + listY.get(i+3) + ", ");
		    	System.out.print(listX.get(i+4) + "," + listY.get(i+4) + ", ");
		    	System.out.print(listX.get(i+5) + "," + listY.get(i+5) + ", ' ||\n");
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
