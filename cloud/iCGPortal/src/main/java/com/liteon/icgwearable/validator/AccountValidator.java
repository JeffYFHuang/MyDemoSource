package com.liteon.icgwearable.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.liteon.icgwearable.model.UsersModel;

public class AccountValidator implements Validator{
	
	private Pattern pattern;  
	private Matcher matcher;  
	private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$";
	
	public boolean supports(Class<?> clazz) {
		return UsersModel.class.equals(clazz);
	}
	
	public void validate(Object target, Errors errors) {
				
		UsersModel usersModel = (UsersModel) target;
	 
		   pattern = Pattern.compile(PASSWORD_PATTERN);  
		   matcher = pattern.matcher(usersModel.getPassword());  
			   if (!matcher.matches()) {
				   errors.rejectValue("password", "superadmin.password.match", "");
			   }
	}
}
