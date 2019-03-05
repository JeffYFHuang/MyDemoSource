package com.liteon.icgwearable.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidation {

	private boolean flag = false;

	private static final String userName = "User filed cannot be empty.Username should be between 1 to 25 characters.";
	private static final String passWordMsg = "Password filed cannot be empty.Password shold be between 1 to 45 characters.";
	private static final String rolltypeMsg = "RoleType filed cannot be empty.";
	private static final String emailMsg = "Mail filed cannot be empty.Mail shold be between  1 to 255 characters.";
	private static Pattern emailPattern = Pattern
			.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

	private static Pattern passwordPattern = Pattern.compile("[a-zA-Z0-9]{8,45}");

	public UserValidation() {
	}

	public boolean validateEmailAddress(String email) {

		Matcher mtch = emailPattern.matcher(email);
		if (mtch.matches()) {
			return false;
		}
		return true;
	}

	private boolean validatePassword(String pass) {
		Matcher mtch = passwordPattern.matcher(pass);
		if (mtch.matches()) {
			return false;
		}
		return true;
	}

	public String validateUserInput(String username, String password, String roleType, String email ) {

		if (username != null || username.trim().equals("") || username.length() > 25) {
			flag = false;
			return userName;
		}
		if (validatePassword(password)) {
			flag = false;
			return passWordMsg;
		}
		if(validateEmailAddress(email)){
			flag = false;
			return emailMsg;
		}
		if(email.length() < 10 || email.length() > 255){
			flag = false;
			return emailMsg;
		}
		if(roleType.equals("school_teacher") || roleType.equals("school_staff")){
			flag = false;
			return rolltypeMsg;
		}

		return null;
	}
	
	public boolean checkFlag (){
		return flag;
	}
	

}
