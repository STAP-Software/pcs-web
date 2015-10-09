package org.tmt.aps.peas.common;

import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.faces.application.FacesMessage;

import org.apache.log4j.Logger;
import org.tmt.aps.peas.config.model.Constant;
import org.tmt.aps.peas.extinf.CommandFailureException;
import org.tmt.aps.peas.session.model.FieldDescriptor;

public class Utils {

	static Logger logger = Logger.getLogger(Utils.class);

	public static void waitForComplete(Future... futures) throws Exception {

		// if a future is null, then ignore it
		
		
		while (true) {

			boolean allDone = true;
			for (Future f : futures) {
				if (f == null) continue;
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
				if (f == null) continue;
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
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageGenerator.generateMessage("generic.error.w_arg", message), Utils.createExceptionMessage(e) + "\nCheck logs for details");
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

	
	
	public static String reformatData(String value, FieldDescriptor fieldDescriptor) {

		if (value == null || value.trim().length() == 0 || value.equals("null")) {
			return "null";
		}
		
		String format = "{0,number," + fieldDescriptor.getDisplayFormat() + "}";

		
		if (fieldDescriptor.isScalar()) {
			
			switch (fieldDescriptor.getDataType()) {

			case Constant.DATA_TYPE_INT:
				Integer intValue = new Integer(value);
				return MessageFormat.format(format, intValue);

			case Constant.DATA_TYPE_FLOAT:
				Float floatValue = new Float(value);
				return MessageFormat.format(format, floatValue);

			case Constant.DATA_TYPE_DOUBLE:
				Double doubleValue = new Double(value);
				return MessageFormat.format(format, doubleValue);

			case Constant.DATA_TYPE_BOOLEAN:
				return value;

			case Constant.DATA_TYPE_INT_POINT:
				Point intPointArray[] = PointListEncoder.decodeList(value).toArray(new Point[] {});
				Point intPointValue = intPointArray[0];
				return MessageFormat.format(format, intPointValue.x) + ", " + MessageFormat.format(format, intPointValue.y);
			
			case Constant.DATA_TYPE_FLOAT_POINT:
				FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(value).toArray(new FloatPoint[] {});
				FloatPoint floatPointValue = floatPointArray[0];
				return MessageFormat.format(format, floatPointValue.x) + ", " + MessageFormat.format(format, floatPointValue.y);
			}
			
		}
		
		StringBuffer buf = new StringBuffer();

		buf.append("<table>");

		if (fieldDescriptor.isOneDimensional()) {

			switch (fieldDescriptor.getDataType()) {

			case Constant.DATA_TYPE_INT:
				Integer intArray[] = IntegerListEncoder.decodeList(value).toArray(new Integer[] {});
				for (int i = 0; i < intArray.length; i++) {
					buf.append((i + 1) + "&nbsp;&nbsp;&nbsp;" + intArray[i] + "\n");
				}
				break;

			case Constant.DATA_TYPE_FLOAT:
				Float floatArray[] = FloatListEncoder.decodeList(value).toArray(new Float[] {});
				for (int i = 0; i < floatArray.length; i++) {
					buf.append((i + 1) + "&nbsp;&nbsp;&nbsp;" + floatArray[i] + "\n");
				}
				break;

			case Constant.DATA_TYPE_INT_POINT:
				Point pointArray[] = PointListEncoder.decodeList(value).toArray(new Point[] {});
				for (int i = 0; i < pointArray.length; i++) {
					buf.append((i + 1) + "&nbsp;&nbsp;&nbsp;" + pointArray[i].x + "      " + pointArray[i].y + "\n");
				}
				break;

			case Constant.DATA_TYPE_FLOAT_POINT:
				FloatPoint floatPointArray[] = FloatPointListEncoder.decodeList(value).toArray(new FloatPoint[] {});
				for (int i = 0; i < floatPointArray.length; i++) {
					buf.append("<tr>");
					buf.append("<td>" + (i + 1) + "</td>");
					buf.append("<td style=\"text-align:right\">" + MessageFormat.format(format, floatPointArray[i].x) + "</td>");
					buf.append("<td>&nbsp;</td>");
					buf.append("<td style=\"text-align:right\">" + MessageFormat.format(format, floatPointArray[i].y) + "</td>");
					buf.append("<td>&nbsp;</td>");
					buf.append("</tr>");
				}
				break;
			}

		} else {

			// two dimensional (non-point type) arrays

			// assuming iteration over first index surrounds the second index iterator

			switch (fieldDescriptor.getDataType()) {
			case Constant.DATA_TYPE_INT:
				Integer intArray[] = IntegerListEncoder.decodeList(value).toArray(new Integer[] {});

				int int2dArray[][] = new int[fieldDescriptor.getDimension1()][fieldDescriptor.getDimension2()];

				// flat array now needs to be read into 2-d array
				int k = 0;
				for (int i = 0; i < fieldDescriptor.getDimension1(); i++) {
					for (int j = 0; j < fieldDescriptor.getDimension2(); j++) {

						int2dArray[i][j] = intArray[k++].intValue();
					}
				}

				break;

			case Constant.DATA_TYPE_FLOAT:
				Float floatArray[] = FloatListEncoder.decodeList(value).toArray(new Float[] {});

				// flat array now needs to be read into 2-d array
				int fk = 0;
				for (int fi = 0; fi < fieldDescriptor.getDimension1(); fi++) {
					buf.append("<tr>");
					buf.append("<td>" + (fi + 1) + "</td>");

					for (int fj = 0; fj < fieldDescriptor.getDimension2(); fj++) {
						
						String sresult =  MessageFormat.format(format, floatArray[fk++].floatValue());
						if (sresult.contains("E") && !sresult.contains("E-")) { //don't blast a negative sign
							sresult = sresult.replace("E", "E+");
						}
						buf.append("<td style=\"text-align:right\">" + sresult  + "</td>");
						buf.append("<td>&nbsp;</td>");
					}
					buf.append("</tr>");
				}

				break;

			}
		}
		buf.append("</table>");
		return buf.toString();

	}
}
