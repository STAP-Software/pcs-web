package org.tmt.aps.peas.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

public class Utils {

	static Logger logger = Logger.getLogger(Utils.class); 
	
	public static void waitForComplete(Future... futures) throws Exception {

		while (true) {

			boolean allDone = true;
			for (Future f : futures) {
				if (!f.isDone()) {
					allDone = false;
				}
			}
			if (allDone)
				break;

			// wait and try again
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			for (Future f : futures) {
				logger.info("Testing Future: " + f);
				f.get();
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new Exception(e.getCause());
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new Exception(e.getCause());
		}
		
	}

}
