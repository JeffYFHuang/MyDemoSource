package com.liteon.icgwearable.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ParentModelValidation {

	private boolean errorFlag = false;
	private static String passwordRegEx = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
	private static final String nameMsg = "Name cannot be blank/It should be between 1 to 45 characters";
	private static final String passwordMsg = "Password must be of 8 characters long with at least one special character and one number \r\n" ;
	private static final String passwordMacth = "Passwords Entered does not matched";
	private static final String mobileMsg = "Contact number is not valid";
	private static final Pattern usernamePattern = Pattern.compile("^[\\w-_\\.]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
	private static Pattern passwordPattern = Pattern.compile(passwordRegEx);

	public ParentModelValidation() {
	}

	public boolean validateEmailAddress(String email) {
		errorFlag = false;
		if (email != null && email.trim().length() <= 145) {
			Matcher mtch = usernamePattern.matcher(email);
			if (mtch.matches()) {
				errorFlag = true;
			}
			return errorFlag;
		} else {
			return errorFlag;
		}
	}

	public boolean validatePassword(String pass) {
		Matcher mtch = passwordPattern.matcher(pass);
		if (mtch.matches()) {
			return false;
		}
		return true;
	}

	public String validateUserInput(String name, String pass, String cPass, String mobileNumber) {
		errorFlag = false;

		if (pass != null && pass.length() > 0) {

			if (validatePassword(pass)) {
				errorFlag = true;
				return passwordMsg;
			}

			if (!pass.equals(cPass)) {
				errorFlag = true;
				return passwordMacth;
			}

		}
		errorFlag = false;

		return "";

	}

	public boolean validateMobileNumber(String mobileNumber) {
		errorFlag = false;
		try {
			if (mobileNumber != null && mobileNumber.trim().length() == 10 ){
				Long mobNo = Long.parseLong(mobileNumber);
				errorFlag = true;
			}

		} catch (Exception e) {
			
			errorFlag = false;
		}
		return errorFlag;
	}
	
	public boolean validateName(String name){
		
		errorFlag = false;
		try {
			if (name != null && name.trim().length() >= 5 && name.trim().length() <= 45){
				errorFlag = true;
			}

		} catch (Exception e) {
			errorFlag = false;
		}
		return errorFlag;
		
	}
	
	public boolean validateRoleType(String role_type){
		
		errorFlag = false;
		try {
			if (role_type != null && (role_type.trim().equals("parent_admin") || role_type.trim().equals("parent_member"))){
				errorFlag = true;
			}

		} catch (Exception e) {
			errorFlag = false;
		}
		return errorFlag;
	}
	

	public boolean checkErrorFlag() {
		return errorFlag;
	}

}
