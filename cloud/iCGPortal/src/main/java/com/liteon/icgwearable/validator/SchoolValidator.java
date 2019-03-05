package com.liteon.icgwearable.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liteon.icgwearable.model.SchoolModel;

public class SchoolValidator implements Validator{
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return SchoolModel.class.equals(clazz);
	}
	@Override
	public void validate(Object target, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountName", "school.name.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "streetAddress", "school.street.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "school.city.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "school.state.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "zipcode", "school.zipcode.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "school.country.required");
		
		SchoolModel schoolModel = (SchoolModel) target;
		
		if(schoolModel.getAccountName()!= null && schoolModel.getAccountName().length() > 45) {
			errors.rejectValue("accountName", "school.name.length", "");
		}
		if(schoolModel.getStreetAddress() != null && schoolModel.getStreetAddress().length() > 145) {
			errors.rejectValue("streetAddress", "school.street.length", "");
		}
		if(schoolModel.getCity() != null && schoolModel.getCity().length() > 145) {
			errors.rejectValue("city", "school.city.length", "");
		}
		if(schoolModel.getState() != null && schoolModel.getState().length() > 45) {
			errors.rejectValue("city", "school.state.length", "");
		}
		if(schoolModel.getZipcode() != null && schoolModel.getZipcode().length() > 45) {
			errors.rejectValue("zipcode", "school.zipcode.length", "");
		}
		if(schoolModel.getCountry() != null && schoolModel.getCountry().length() > 15) {
			errors.rejectValue("country", "school.country.length", "");
		}
	}



}
