package org.tmt.aps.peas.common;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.faces.application.FacesMessage;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.extinf.CommandFailureException;

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

	public static int[] floatArrayToIntArray(float[] input) {
		int[] output = new int[input.length];
		for (int i = 0; i < input.length; i++) {
			output[i] = (int) input[i];
		}
		return output;
	}
	
	public static String createExceptionMessage(Throwable e) {
		StringBuffer buf = new StringBuffer();

		addExceptionMessage(e, buf);

		return buf.toString();
	}

	public static void waitFor(long msec) {
		
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	
	// recursively get all exception messages from exception and nested causes
	private static void addExceptionMessage(Throwable e, StringBuffer buf) {

		buf.append("  \n" + e.getClass().getName() + ": " + e.getMessage());
		
		if (e.getCause() != null) {
			buf.append(", caused by: ");
			addExceptionMessage(e.getCause(), buf);
		}
	}

	public static FacesMessage recordUpdateSuccessfulMessage() {
		return new FacesMessage(FacesMessage.SEVERITY_INFO, MessageGenerator.generateMessage("crud.success"), "");
	}

	public static FacesMessage recordUpdateFailedMessage(Throwable e) {
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageGenerator.generateMessage("crud.failure"), Utils.createExceptionMessage(e) + "\nCheck logs for details");
	}

	public static FacesMessage genericErrorMessage(Throwable e) {
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageGenerator.generateMessage("generic.error"), Utils.createExceptionMessage(e) + "\nCheck logs for details");
	}

	public static FacesMessage genericErrorMessage(Throwable e, String message) {
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageGenerator.generateMessage("generic.error") + "/n" + message, Utils.createExceptionMessage(e) + "\nCheck logs for details");
	}

	public static FacesMessage commandSuccessfulMessage() {
		return new FacesMessage(FacesMessage.SEVERITY_INFO, MessageGenerator.generateMessage("command.success"), "");
	}

	public static FacesMessage commandSuccessfulMessage(String commandedName) {
		return new FacesMessage(FacesMessage.SEVERITY_INFO, MessageGenerator.generateMessage("command.success", commandedName), "");
	}

	public static FacesMessage commandFailedMessage(CommandFailureException e) {	
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageGenerator.generateMessage("command.failure", e.getFailureCode()), Utils.createExceptionMessage(e));
	}

	public static FacesMessage commandFailedMessage(CommandFailureException e, String message) {	
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageGenerator.generateMessage("command.failure", e.getFailureCode()) + "\n" + message, Utils.createExceptionMessage(e));
	}

	public static FacesMessage procedureSuccessfulMessage(String procedureType) {
		return new FacesMessage(FacesMessage.SEVERITY_INFO, MessageGenerator.generateMessage("procedure.success", procedureType), "");
	}

	public static FacesMessage procedureFailedMessage(Throwable e) {
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageGenerator.generateMessage("procedure.failure"), Utils.createExceptionMessage(e) + "\nCheck logs for details");
	}

}
