package com.liteon.icgwearable.util;

import org.springframework.web.servlet.ModelAndView;
import com.liteon.icgwearable.hibernate.entity.Users;

public class LoginUtil {

	public ModelAndView checkUserAndSessionValidity(Users user, String sessionValidity) {
		
		ModelAndView model = new ModelAndView();
		
		if (user == null || !user.getRoleType().equals("parent_admin")) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		} 
		
		if (sessionValidity.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}
		return model;
	}
	
}
