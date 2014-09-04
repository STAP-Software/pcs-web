package org.tmt.aps.peas.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.tmt.aps.peas.lang.interop.RetVal;

public class MessageGenerator {

	
	public static String generateMessage(String key) {
		String pattern = ResourceBundle.getBundle("messages").getString(key);
		Object[] args = new Object[0];
		return MessageFormat.format(pattern, args);
	}
	
	public static String generateMessage(String key, Object val1) {
		
		String pattern = ResourceBundle.getBundle("messages").getString(key);
		Object[] args = new Object[2];
		args[0] = val1;
		return MessageFormat.format(pattern, args);	
	}
	
	public static String generateMessage(String key, Object val1, Object val2) {
		
		String pattern = ResourceBundle.getBundle("messages").getString(key);
		Object[] args = new Object[2];
		args[0] = val1;
		args[1] = val2;
		return MessageFormat.format(pattern, args);	
	}
	
	public static String generateMessage(String key, Point p) {
		return generateMessage(key, p.x, p.y);
	}
	public static String generateMessage(String key, FloatPoint fp) {
		return generateMessage(key, fp.x, fp.y);
	}
	
	public static String generateErrorMessage(RetVal retVal) {
		String key = "E" + String.format("%05d", retVal.getCode());
		String pattern = ResourceBundle.getBundle("errorCodes").getString(key);
		
		
		Double[] args = new Double[10];
		args[0] = retVal.getArg0();
		args[1] = retVal.getArg1();
		args[2] = retVal.getArg2();
		args[3] = retVal.getArg3();
		args[4] = retVal.getArg4();
		args[5] = retVal.getArg5();
		args[6] = retVal.getArg6();
		args[7] = retVal.getArg7();
		args[8] = retVal.getArg8();
		args[9] = retVal.getArg9();
		
		System.out.println("MessageGenerator:: " + args[0] +", " + args[1] +", " + args[2] +", " + args[3] +", " + args[4]);
		
		String message = MessageFormat.format(pattern, (Object[])args);
		
		return message;

	}		
	
}
