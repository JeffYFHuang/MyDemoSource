package com.liteon.icgwearable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.liteon.icgwearable.dao.SchoolAdminDAO;
import com.liteon.icgwearable.hibernate.entity.Users;


@Service
@Transactional
public class SchoolAdminServiceImpl implements SchoolAdminService{

	@Autowired
    private SchoolAdminDAO schoolAdminDAO;

	@Override
	public void addUser(Users user) {
		this.schoolAdminDAO.addUser(user);
		// TODO Auto-generated method stub
		
	}

	@Override
	public Users getUser(int userId) {
		// TODO Auto-generated method stub
		
		return this.schoolAdminDAO.getUser(userId);
	}

	@Override
	public void updateUser(Users user) {
		// TODO Auto-generated method stub
		
		this.schoolAdminDAO.updateUser(user);
		
	}
}
