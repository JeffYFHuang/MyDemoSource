package com.liteon.icgwearable.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liteon.icgwearable.model.StudentsModel;

public class StudentValidator implements Validator{
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return StudentsModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rollNo", "rollNo.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studentClass", "class.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "uuid", "student.uuid.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firmware", "student.firmware.required");
		StudentsModel stModel = (StudentsModel)target;
		
		if(stModel.getStudentClass()!= null && stModel.getName().length() > 45) {
			errors.rejectValue("name", "student.name.length.exceeded", "");
		}
		
		if(stModel.getStudentClass()!= null && stModel.getStudentClass().length() > 5) {
			errors.rejectValue("studentClass", "student.class.length.exceeded", "");
		}
		
		if(stModel.getRollNo()!= null && stModel.getRollNo().length() > 5) {
			errors.rejectValue("rollNo", "student.rollNo.length.exceeded", "");
		}
		
		if(stModel.getHeight()!= null && stModel.getHeight().length() > 5) {
			errors.rejectValue("height", "student.height.length.exceeded", "");
		}
		
		if(stModel.getWeight()!= null && stModel.getWeight().length() > 5) {
			errors.rejectValue("weight", "student.weight.length.exceeded", "");
		}
		
		if(stModel.getUuid()!= null && stModel.getUuid().length() > 45) {
			errors.rejectValue("uuid", "student.uuid.length.exceeded", "");
		}
		
		if(stModel.getFirmware() != null && stModel.getFirmware().length() > 45) {
			errors.rejectValue("firmware", "student.firmware.length.exceeded", "");
		}
	}
}
