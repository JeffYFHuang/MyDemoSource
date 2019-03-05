package com.liteon.icgwearable.validator;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import com.liteon.icgwearable.model.StudentsModel;

public class KidsValidator  implements Validator{
	private static Logger log = Logger.getLogger(KidsValidator.class);
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return StudentsModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nickName", "nick.name.required");

		StudentsModel stModel = (StudentsModel)target;
		if(stModel.getNickname() != null && stModel.getNickname().length() > 45) {
			log.info("Into If");
			errors.rejectValue("nickName", "nick.name.length.exceeded", "Nick Name cannot be more than 45 characters");
		}
		
	}
}
