package com.liteon.icgwearable.federallogin;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.liteon.icgwearable.service.UserService;

public class FederatedLogin {
	private static Logger log = Logger.getLogger(FederatedLogin.class);
	@Autowired
	private UserService userService;
	private final int LENGTH = 32;
	private SecureRandom random = new SecureRandom();
	@Autowired
	private HttpSession httpSession;
	
	public FederatedLogin() {}
	
	public boolean federatedLogin(String profileName, String email,String token,String userAgent) {
		log.info("Into federatedLogin() {"); 
		//String sessionId = getSessionId();
		//log.info("sessionId in federatedLogin()"+"\t"+sessionId);
		boolean isUserExists = this.userService.isUsersExist(email);
		boolean openIdExist = this.userService.openIdExist(email);
		boolean loginSuccess = false;
		
		
		log.info("isUserExists"+"\t"+isUserExists);
		log.info("openIdExist"+"\t"+openIdExist);
	
	//	this.httpSession.setAttribute("sessionId", sessionId);
		if(openIdExist) {
			loginSuccess = this.userService.updateOpenId(token, profileName, email,userAgent);
		}else if(isUserExists) {
			loginSuccess = this.userService.updateFederatedLogin(token, profileName, email,userAgent);
		}else {
			loginSuccess = this.userService.insertFederatedLogin(token, profileName, email,userAgent);
		}	
		log.info("Exiting federatedLogin() }");
		
		return loginSuccess;
	}
	
	public String getSessionId() {
		BigInteger bigInteger = new BigInteger(130, random);
		String sessionId = String.valueOf(bigInteger.toString(LENGTH));
		return sessionId.toUpperCase();
	}
}
