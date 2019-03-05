package com.liteon.icgwearable.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.liteon.icgwearable.model.UsersModel;

public class TeacherValidator implements Validator{
	private static Logger log = Logger.getLogger(TeacherValidator.class);
	private Pattern pattern;  
	private Matcher matcher;  
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"  
			   + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	@Override
	public boolean supports(Class<?> clazz) {
		return UsersModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "teacher.name.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "teacher.username.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "teacher.password.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "uuid", "teacher.uuid.required");
		
		UsersModel teachers = (UsersModel) target;
		
		if(teachers.getName()!= null && teachers.getName().length() > 45) {
			errors.rejectValue("name", "name.length.exceeded", "");
		}

		if(teachers.getUsername()!= null && teachers.getUsername().length() > 25) {
			errors.rejectValue("username", "user.name.length.exceeded", "");
		}
		
		if(teachers.getPassword()!= null && teachers.getPassword().length() > 245) {
			errors.rejectValue("password", "password.length.exceeded", "");
		}
		
		if(teachers.getUuid() != null && teachers.getUuid().length() > 245) {
			errors.rejectValue("uuid", "uuid.length.exceeded", "");
		}
		log.info("Before Email Validation");
		if (!(teachers.getUsername() != null && teachers.getUsername().isEmpty())) {  
		   pattern = Pattern.compile(EMAIL_PATTERN);  
		   matcher = pattern.matcher(teachers.getUsername());  
		   if (!matcher.matches()) {
			   errors.rejectValue("username", "username.incorrect",  
					      "Enter a correct email as username");
		   }  
		  }
		log.info("After Validation");
	}

}
