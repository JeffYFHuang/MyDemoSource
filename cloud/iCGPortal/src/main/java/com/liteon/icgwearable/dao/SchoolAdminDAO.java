package com.liteon.icgwearable.dao;

import com.liteon.icgwearable.hibernate.entity.Users;

public interface SchoolAdminDAO {
	
	  public void addUser(Users user);
	   
	    public Users getUser(int userId);
	   
	    public void updateUser(Users user);

}
