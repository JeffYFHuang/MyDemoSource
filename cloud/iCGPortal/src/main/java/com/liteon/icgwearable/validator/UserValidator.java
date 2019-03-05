package com.liteon.icgwearable.validator;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.liteon.icgwearable.hibernate.entity.Users;

/**
 * @author Vikas
 *
 */

@Component
public class UserValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return UserValidator.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.required");
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "name.required");
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rollNo", "rollNo.required");
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studentClass", "class.required");
		Users usersModel = (Users)target;
		if(usersModel.getName().length() > 45) {
			errors.rejectValue("name", "student.name.length.exceeded", "");
		}
		
		/*if(usersModel.get.length() > 5) {
			errors.rejectValue("studentClass", "student.class.length.exceeded", "");
		}
		
		if(stModel.getRollNo().length() > 5) {
			errors.rejectValue("rollNo", "student.rollNo.length.exceeded", "");
		}
		
		if(stModel.getHeight().length() > 5) {
			errors.rejectValue("height", "student.height.length.exceeded", "");
		}
		
		if(stModel.getWeight().length() > 5) {
			errors.rejectValue("weight", "student.weight.length.exceeded", "");
		}*/
		
	}
	
	@Autowired
	HttpSession httpSession;
	
	public void validateFields(Users user){
		if(user.getName().equals("")){
             this.httpSession.setAttribute("nameError", "true");
         }else if(user.getUsername().equals("")){
            }else if(user.getUsername().equals("")){
             this.httpSession.setAttribute("usernameError", "true");
         }
	}

}
