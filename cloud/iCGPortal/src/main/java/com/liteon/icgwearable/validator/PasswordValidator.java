package com.liteon.icgwearable.validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.liteon.icgwearable.model.UsersModel;

public class PasswordValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return UsersModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "teacher.password.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cpassword", "teacher.cpassword.required");
		
		UsersModel teachers = (UsersModel) target;
		
		if(teachers.getPassword() != null && teachers.getPassword().length() > 245) {
			errors.rejectValue("password", "password.length.exceeded", "");
		}
		
		if(teachers.getCpassword() != null && teachers.getCpassword().length() > 245) {
			errors.rejectValue("password", "confirm.password.length.exceeded", "");
		}
	}

}
