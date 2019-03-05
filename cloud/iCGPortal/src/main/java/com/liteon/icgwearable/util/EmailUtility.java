package com.liteon.icgwearable.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtility {
	
	private EmailUtility(){}

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"  
			   + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static boolean verifyEmail(String password) {
		boolean validEmail = false;
		Pattern pattern;  
		Matcher matcher;  
		
		pattern = Pattern.compile(EMAIL_PATTERN);  
		   matcher = pattern.matcher(password);  
			   if (matcher.matches()) {
				   validEmail = true;
			   }
		return validEmail;
	}
	
}
