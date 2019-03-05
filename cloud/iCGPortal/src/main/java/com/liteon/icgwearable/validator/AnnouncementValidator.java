package com.liteon.icgwearable.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.liteon.icgwearable.model.AnnouncementModel;

public class AnnouncementValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return AnnouncementModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studentClass", "studentClass.required");

		AnnouncementModel announcementtModel = (AnnouncementModel)target;
		
		if(announcementtModel.getName() != null && announcementtModel.getName().length() > 45) {
			errors.rejectValue("name", "announcement.name.length.exceeded", "");
		}
		if(announcementtModel.getStudentClass() != null && announcementtModel.getStudentClass().length() > 5) {
			errors.rejectValue("studentClass", "announcement.class.length.exceeded", "");
		}
	}
}
