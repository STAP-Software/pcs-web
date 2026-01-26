package org.tmt.aps.peas.session.ui;

import java.util.Comparator;
import java.util.StringTokenizer;

import org.tmt.aps.peas.procedure.model.Procedure;

/**
 * Comparator class used to order procedures by procedure number
 * @author smichaels
 *
 */
public class ProcedureNumberComparator implements Comparator<Procedure> {

	@Override
	public int compare(Procedure p1, Procedure p2) {
		
		String p1Num = p1.getProcedureNumber();
		String p2Num = p2.getProcedureNumber();
		
		// get first number and go from there
		StringTokenizer st1 = new StringTokenizer(p1Num, ".");
		StringTokenizer st2 = new StringTokenizer(p2Num, ".");

		return compareVersions(st1, st2);
	}

	int compareVersions(StringTokenizer st1, StringTokenizer st2) {
		
		if (!st1.hasMoreTokens()) {
			return -1;
		}
		if (!st2.hasMoreTokens()) {
			return 1;
		}
		
		String t1 = st1.nextToken();
		String t2 = st2.nextToken();
				
		if (t1.equals(t2)) {
			return compareVersions(st1, st2);
		} else {
			
			Integer i1 = Integer.valueOf(t1);
			Integer i2 = Integer.valueOf(t2);
			
			return i1.compareTo(i2);
		}
	}
	
	
	
}
