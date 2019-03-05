package com.liteon.icgwearable.service;

import com.liteon.icgwearable.hibernate.entity.Users;

public interface SchoolAdminService {


	  public void addUser(Users user);
	   
	    public Users getUser(int userId);
	    public void updateUser(Users user);	
}


